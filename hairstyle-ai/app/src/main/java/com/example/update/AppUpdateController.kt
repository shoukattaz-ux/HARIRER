package com.example.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.BuildConfig
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Keeps the app up to date.
 *
 * - Installed from Google Play: uses Google Play In-App Updates (the only update method Play allows).
 *   Urgent or long-ignored updates are IMMEDIATE (blocking); others download in the background.
 * - Installed any other way (sideloaded APK): reads the public, read-only `update.json` at
 *   BuildConfig.UPDATE_MANIFEST_URL and offers the new APK as a download. No credentials are used,
 *   and Android only installs the APK as an update if it is signed with the same key.
 *
 * Create in Activity.onCreate (it registers an activity-result launcher).
 */
class AppUpdateController(private val activity: ComponentActivity) {

    sealed class UiState {
        object Idle : UiState()
        data class Downloading(val progress: Float) : UiState()
        /** A flexible Play update finished downloading; restart to install. */
        object ReadyToInstall : UiState()
        data class SelfHostedAvailable(
            val manifest: UpdatePolicy.UpdateManifest,
            val isRequired: Boolean
        ) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    private val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val isFromPlay: Boolean by lazy { isInstalledFromPlay() }

    private val updateLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            Log.i(TAG, "Update flow closed without updating (result ${result.resultCode})")
        }
    }

    private val installListener = InstallStateUpdatedListener { installState ->
        when (installState.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val total = installState.totalBytesToDownload()
                val progress = if (total > 0) installState.bytesDownloaded().toFloat() / total else 0f
                _state.value = UiState.Downloading(progress.coerceIn(0f, 1f))
            }
            InstallStatus.DOWNLOADED -> {
                _state.value = UiState.ReadyToInstall
            }
            InstallStatus.INSTALLED, InstallStatus.FAILED, InstallStatus.CANCELED -> {
                _state.value = UiState.Idle
            }
            else -> Unit
        }
    }

    /** Call once from onCreate. */
    fun checkForUpdates() {
        if (isFromPlay) {
            appUpdateManager.registerListener(installListener)
            checkPlayUpdate()
        } else {
            checkSelfHostedUpdate()
        }
    }

    /** Call from onResume: resumes interrupted immediate updates and surfaces finished downloads. */
    fun onResume() {
        if (!isFromPlay) return
        activity.lifecycleScope.launch {
            val info = requestPlayInfo() ?: return@launch
            when {
                info.installStatus() == InstallStatus.DOWNLOADED -> {
                    _state.value = UiState.ReadyToInstall
                }
                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                    startPlayFlow(info, AppUpdateType.IMMEDIATE)
            }
        }
    }

    /** Installs a downloaded flexible update (the app restarts). */
    fun completeFlexibleUpdate() {
        appUpdateManager.completeUpdate()
    }

    /** Opens the new APK download for sideloaded installs. */
    fun downloadSelfHostedUpdate(manifest: UpdatePolicy.UpdateManifest) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(manifest.apkUrl)))
        } catch (e: Exception) {
            Log.w(TAG, "No app can open the update link", e)
        }
    }

    /** Hides an optional self-hosted update until a newer version is published. */
    fun dismissSelfHostedUpdate(manifest: UpdatePolicy.UpdateManifest) {
        prefs.edit().putInt(KEY_DISMISSED_VERSION, manifest.versionCode).apply()
        _state.value = UiState.Idle
    }

    fun release() {
        if (isFromPlay) appUpdateManager.unregisterListener(installListener)
    }

    private fun checkPlayUpdate() {
        activity.lifecycleScope.launch {
            val info = requestPlayInfo() ?: return@launch
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                _state.value = UiState.ReadyToInstall
                return@launch
            }
            val type = UpdatePolicy.choosePlayUpdate(
                isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE,
                priority = info.updatePriority(),
                stalenessDays = info.clientVersionStalenessDays(),
                isImmediateAllowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
                isFlexibleAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            )
            when (type) {
                UpdatePolicy.PlayUpdateType.IMMEDIATE -> startPlayFlow(info, AppUpdateType.IMMEDIATE)
                UpdatePolicy.PlayUpdateType.FLEXIBLE -> startPlayFlow(info, AppUpdateType.FLEXIBLE)
                UpdatePolicy.PlayUpdateType.NONE -> Unit
            }
        }
    }

    private suspend fun requestPlayInfo(): AppUpdateInfo? = try {
        appUpdateManager.requestAppUpdateInfo()
    } catch (e: Exception) {
        Log.w(TAG, "Play update check failed", e)
        null
    }

    private fun startPlayFlow(info: AppUpdateInfo, @AppUpdateType type: Int) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                info,
                updateLauncher,
                AppUpdateOptions.newBuilder(type).build()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not start Play update flow", e)
        }
    }

    private fun checkSelfHostedUpdate() {
        val manifestUrl = BuildConfig.UPDATE_MANIFEST_URL
        if (!UpdatePolicy.isManifestUrlConfigured(manifestUrl)) return
        activity.lifecycleScope.launch {
            val manifest = withContext(Dispatchers.IO) { fetchManifest(manifestUrl) }
            val decision = UpdatePolicy.evaluate(
                manifest = manifest,
                currentVersionCode = BuildConfig.VERSION_CODE,
                dismissedVersionCode = prefs.getInt(KEY_DISMISSED_VERSION, -1)
            )
            _state.value = when (decision) {
                is UpdatePolicy.SelfHostedDecision.Required -> UiState.SelfHostedAvailable(decision.manifest, isRequired = true)
                is UpdatePolicy.SelfHostedDecision.Optional -> UiState.SelfHostedAvailable(decision.manifest, isRequired = false)
                UpdatePolicy.SelfHostedDecision.None -> UiState.Idle
            }
        }
    }

    private fun fetchManifest(url: String): UpdatePolicy.UpdateManifest? = try {
        val request = Request.Builder()
            .url(url)
            .header("Cache-Control", "no-cache")
            .build()
        httpClient.newCall(request).execute().use { response ->
            val body = response.body
            if (!response.isSuccessful || body == null || body.contentLength() > MAX_MANIFEST_BYTES) {
                null
            } else {
                UpdateManifestParser.parse(body.string())
            }
        }
    } catch (e: Exception) {
        Log.w(TAG, "Self-hosted update check failed", e)
        null
    }

    private fun isInstalledFromPlay(): Boolean = try {
        val packageManager = activity.packageManager
        val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(activity.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(activity.packageName)
        }
        installer == PLAY_STORE_PACKAGE
    } catch (e: Exception) {
        false
    }

    private companion object {
        const val TAG = "AppUpdate"
        const val PREFS_NAME = "app_update_prefs"
        const val KEY_DISMISSED_VERSION = "dismissed_version_code"
        const val PLAY_STORE_PACKAGE = "com.android.vending"
        const val MAX_MANIFEST_BYTES = 64 * 1024L

        val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
    }
}
