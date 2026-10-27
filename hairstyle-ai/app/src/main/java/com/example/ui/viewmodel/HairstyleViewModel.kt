package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.analysis.HairColorAdvisor
import com.example.data.api.AiHairstyleService
import com.example.data.api.AiImageEditService
import com.example.data.api.LifestyleScene
import com.example.data.api.SceneRenderStyle
import com.example.data.api.ScenePromptBuilder
import com.example.data.api.AiTryOnConfig
import com.example.data.model.Gender
import com.example.data.model.GridTileState
import com.example.data.model.HairstyleItem
import com.example.data.model.ProcessingStage
import com.example.data.model.SceneStudioState
import com.example.data.model.StyleGridState
import com.example.data.model.TryOnError
import com.example.data.model.TryOnHistoryItem
import com.example.data.model.UserHairProfile
import com.example.data.model.UserPhotoSource
import com.example.data.repository.AiQuotaManager
import com.example.data.repository.AiTryOnResult
import com.example.data.repository.GeminiTryOnRepository
import com.example.data.repository.HairstyleRepository
import com.example.data.repository.PaidApiTryOnRepository
import com.example.data.repository.StyleGridGenerator
import com.example.ui.components.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HairstyleViewModel(
    application: Application,
    private val aiService: AiHairstyleService,
    private val quotaManager: AiQuotaManager,
    private val sceneService: AiImageEditService
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        aiService = GeminiTryOnRepository,
        quotaManager = AiQuotaManager,
        sceneService = GeminiTryOnRepository
    )

    // App Theme Preference (Light vs Dark)
    private val themePrefs = application.getSharedPreferences("hairstyle_theme_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(
        themePrefs.getBoolean("pref_dark_theme", true) // Default is Dark
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        themePrefs.edit().putBoolean("pref_dark_theme", isDark).apply()
    }

    // Language Selection (English, Urdu, Hindi, Spanish, Arabic, French)
    private val _selectedLanguage = MutableStateFlow(
        try {
            val code = themePrefs.getString("pref_language_code", "en")
            AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.ENGLISH
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    )
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        themePrefs.edit().putString("pref_language_code", language.code).apply()
        _userFeedback.value = "Language switched to ${language.englishName}"
    }

    // App Membership Tier: Free vs Paid Version ($2 Subscription)
    private val _isPaidSubscribed = MutableStateFlow(
        themePrefs.getBoolean("pref_paid_subscribed", false)
    )
    val isPaidSubscribed: StateFlow<Boolean> = _isPaidSubscribed.asStateFlow()

    private val _isPaidVersion = MutableStateFlow(
        themePrefs.getBoolean("pref_paid_subscribed", false)
    )
    val isPaidVersion: StateFlow<Boolean> = _isPaidVersion.asStateFlow()

    fun subscribePaidVersion(onComplete: () -> Unit) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _isPaidSubscribed.value = true
            _isPaidVersion.value = true
            themePrefs.edit()
                .putBoolean("pref_paid_subscribed", true)
                .putBoolean("pref_paid_version", true)
                .apply()
            onComplete()
        }
    }

    fun setPaidVersion(isPaid: Boolean) {
        _isPaidVersion.value = isPaid
        themePrefs.edit().putBoolean("pref_paid_version", isPaid).apply()
    }

    // Paid Dedicated REST API Configuration
    fun getPaidApiUrl(): String = com.example.data.repository.PaidApiTryOnRepository.getApiUrl(getApplication())
    fun setPaidApiUrl(url: String) = com.example.data.repository.PaidApiTryOnRepository.setApiUrl(getApplication(), url)
    fun getPaidApiKey(): String = com.example.data.repository.PaidApiTryOnRepository.getApiKey(getApplication())
    fun setPaidApiKey(key: String) = com.example.data.repository.PaidApiTryOnRepository.setApiKey(getApplication(), key)
    fun testPaidApiConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = com.example.data.repository.PaidApiTryOnRepository.testConnection(getApplication())
            onResult(res.first, res.second)
        }
    }


    // Favorites
    private val _favoriteIds = MutableStateFlow<Set<String>>(
        setOf("m_crop_01", "m_quiff_01", "m_fade_01")
    )
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Home Screen Gender & Category State
    private val _homeGender = MutableStateFlow(Gender.MEN)
    val homeGender: StateFlow<Gender> = _homeGender.asStateFlow()

    private val _homeMenCategory = MutableStateFlow("All")
    val homeMenCategory: StateFlow<String> = _homeMenCategory.asStateFlow()

    private val _homeWomenCategory = MutableStateFlow("All")
    val homeWomenCategory: StateFlow<String> = _homeWomenCategory.asStateFlow()

    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

    private val _isHomeSearchVisible = MutableStateFlow(false)
    val isHomeSearchVisible: StateFlow<Boolean> = _isHomeSearchVisible.asStateFlow()

    // Dedicated Men Gallery Filter & Search
    private val _menCategory = MutableStateFlow("All")
    val menCategory: StateFlow<String> = _menCategory.asStateFlow()

    private val _menSearch = MutableStateFlow("")
    val menSearch: StateFlow<String> = _menSearch.asStateFlow()

    // Dedicated Women Gallery Filter & Search
    private val _womenCategory = MutableStateFlow("All")
    val womenCategory: StateFlow<String> = _womenCategory.asStateFlow()

    private val _womenSearch = MutableStateFlow("")
    val womenSearch: StateFlow<String> = _womenSearch.asStateFlow()

    // Active Try-On Target
    private val _activeTryOnItem = MutableStateFlow<HairstyleItem?>(
        HairstyleRepository.getById("m_crop_01")
    )
    val activeTryOnItem: StateFlow<HairstyleItem?> = _activeTryOnItem.asStateFlow()

    // User Photo Source (Captured, Uploaded Uri, or Preset Model)
    private val _userPhoto = MutableStateFlow<UserPhotoSource?>(null)
    val userPhoto: StateFlow<UserPhotoSource?> = _userPhoto.asStateFlow()

    // Real AI Result Bitmap
    private val _aiResultBitmap = MutableStateFlow<Bitmap?>(null)
    val aiResultBitmap: StateFlow<Bitmap?> = _aiResultBitmap.asStateFlow()

    private val _isAiGenerated = MutableStateFlow(false)
    val isAiGenerated: StateFlow<Boolean> = _isAiGenerated.asStateFlow()

    // Try-on Processing State
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingStage = MutableStateFlow(ProcessingStage.ANALYZING_FACE)
    val processingStage: StateFlow<ProcessingStage> = _processingStage.asStateFlow()

    // Free Usage Tracking
    val remainingFreeQuota: StateFlow<Int> = quotaManager.remainingAttempts
    val maxDailyQuota: Int = quotaManager.maxDailyLimit

    // Try-on Error State
    private val _tryOnError = MutableStateFlow<TryOnError?>(null)
    val tryOnError: StateFlow<TryOnError?> = _tryOnError.asStateFlow()

    private val _aiErrorMessage = MutableStateFlow<String?>(null)
    val aiErrorMessage: StateFlow<String?> = _aiErrorMessage.asStateFlow()

    // Hair Color Tint selected on Result Screen
    private val _selectedColorTint = MutableStateFlow("Natural Black")
    val selectedColorTint: StateFlow<String> = _selectedColorTint.asStateFlow()

    // User Hair Profile
    private val _userProfile = MutableStateFlow(
        UserHairProfile(
            userName = themePrefs.getString("pref_user_name", "HairStyle Studio") ?: "HairStyle Studio",
            memberSince = themePrefs.getString("pref_member_since", "October 2026") ?: "October 2026"
        )
    )
    val userProfile: StateFlow<UserHairProfile> = _userProfile.asStateFlow()

    // Try-on History
    private val _tryOnHistory = MutableStateFlow(HairstyleRepository.sampleHistory)
    val tryOnHistory: StateFlow<List<TryOnHistoryItem>> = _tryOnHistory.asStateFlow()

    // Toast/Snackbar notifications
    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    private var tryOnJob: Job? = null

    fun toggleFavorite(id: String) {
        _favoriteIds.update { current ->
            if (current.contains(id)) {
                _userFeedback.value = "Removed from Favorites"
                current - id
            } else {
                _userFeedback.value = "Saved to Favorites"
                current + id
            }
        }
    }

    fun isFavorite(id: String): Boolean = _favoriteIds.value.contains(id)

    // Home Screen Actions
    fun setHomeGender(gender: Gender) {
        _homeGender.value = gender
    }

    fun setHomeMenCategory(category: String) {
        _homeMenCategory.value = category
    }

    fun setHomeWomenCategory(category: String) {
        _homeWomenCategory.value = category
    }

    fun setHomeSearchQuery(query: String) {
        _homeSearchQuery.value = query
    }

    fun toggleHomeSearch(show: Boolean? = null) {
        _isHomeSearchVisible.value = show ?: !_isHomeSearchVisible.value
    }

    // Dedicated Gallery Actions
    fun setMenCategory(category: String) {
        _menCategory.value = category
    }

    fun setMenSearch(query: String) {
        _menSearch.value = query
    }

    fun setWomenCategory(category: String) {
        _womenCategory.value = category
    }

    fun setWomenSearch(query: String) {
        _womenSearch.value = query
    }

    fun setActiveTryOnItem(item: HairstyleItem) {
        _activeTryOnItem.value = item
    }

    // Photo & Try-on Workflow Actions
    fun setUserPhoto(photo: UserPhotoSource?) {
        _userPhoto.value = photo
        _tryOnError.value = null
        _aiErrorMessage.value = null
    }

    fun clearUserPhoto() {
        _userPhoto.value = null
        _aiResultBitmap.value = null
        _isAiGenerated.value = false
        _tryOnError.value = null
        _aiErrorMessage.value = null
    }

    fun setTryOnError(error: TryOnError?, detailedMessage: String? = null) {
        _tryOnError.value = error
        _aiErrorMessage.value = detailedMessage ?: error?.message
    }

    fun clearTryOnError() {
        _tryOnError.value = null
        _aiErrorMessage.value = null
    }

    fun cancelTryOn() {
        tryOnJob?.cancel()
        tryOnJob = null
        _isProcessing.value = false
        _processingStage.value = ProcessingStage.ANALYZING_FACE
    }

    /**
     * Executes the AI hairstyle transformation workflow:
     * User Photo -> Selected Hairstyle -> Check Free Usage Limit -> AI Request -> Result
     *
     * Features Cost & Free-Tier Protections:
     * - Re-entrant guard against double-taps
     * - Rejects request immediately if daily free limit has been exhausted
     * - Decrements quota ONLY on successful generation
     * - No automated retry loops
     */
    fun startRealAiTryOn(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Cost Protection 1: Prevent double-tapping / concurrent executions
        if (_isProcessing.value) {
            Log.w("HairstyleViewModel", "Ignored duplicate AI request while already processing.")
            return
        }

        // Cost Protection 2: Check Free Usage Limit before making any network call
        if (!quotaManager.canGenerate()) {
            val error = TryOnError.QuotaExceeded
            _tryOnError.value = error
            _aiErrorMessage.value = error.message
            onError(error.message)
            return
        }

        val photo = _userPhoto.value
        val hairstyle = _activeTryOnItem.value

        if (photo == null) {
            val error = TryOnError.NoPhotoSelected
            _tryOnError.value = error
            _aiErrorMessage.value = error.message
            onError(error.message)
            return
        }

        if (hairstyle == null) {
            val error = TryOnError.ProcessingFailed
            _tryOnError.value = error
            _aiErrorMessage.value = "Please select a hairstyle to preview."
            onError("Please select a hairstyle to preview.")
            return
        }

        tryOnJob?.cancel()
        tryOnJob = viewModelScope.launch {
            _isProcessing.value = true
            _tryOnError.value = null
            _aiErrorMessage.value = null

            // Animate through progression stages to keep user informed
            val stageJob = launch {
                val stages = ProcessingStage.values()
                for (stage in stages) {
                    _processingStage.value = stage
                    delay(1200)
                }
            }

            // Call AI service: If user is on Paid Version, use Dedicated REST API (decoupled from Gemini)
            val serviceToUse = if (_isPaidVersion.value) {
                com.example.data.repository.PaidApiTryOnRepository
            } else {
                aiService
            }
            val result = serviceToUse.transformHairstyle(
                context = context,
                userPhoto = photo,
                hairstyle = hairstyle,
                colorTint = _selectedColorTint.value
            )
            stageJob.cancel()

            _isProcessing.value = false

            when (result) {
                is AiTryOnResult.Success -> {
                    // Cost Protection 3: Decrement free quota ONLY on successful generation
                    quotaManager.recordSuccess()

                    _aiResultBitmap.value = result.resultBitmap
                    _isAiGenerated.value = result.isAiGenerated
                    _tryOnError.value = null
                    _aiErrorMessage.value = null

                    // Add to session history
                    val newHistory = TryOnHistoryItem(
                        id = "hist_${System.currentTimeMillis()}",
                        hairstyleName = hairstyle.name,
                        category = "${hairstyle.gender.displayName} • ${hairstyle.category}",
                        date = "Just now",
                        matchScore = 98,
                        imageRes = R.drawable.img_tryon_result
                    )
                    _tryOnHistory.update { listOf(newHistory) + it }
                    onSuccess()
                }

                is AiTryOnResult.Error -> {
                    Log.w("HairstyleViewModel", "AI Transformation failed: ${result.detailedMessage}")
                    // Do NOT count a failed API request as a successful generation
                    _aiResultBitmap.value = null
                    _isAiGenerated.value = false
                    _tryOnError.value = result.error
                    val message = result.detailedMessage ?: result.error.message
                    _aiErrorMessage.value = message
                    onError(message)
                }
            }
        }
    }

    // "4 AI Looks" grid: one photo, four recommended hairstyles generated side by side
    private val _styleGrid = MutableStateFlow(StyleGridState())
    val styleGrid: StateFlow<StyleGridState> = _styleGrid.asStateFlow()

    private var styleGridJob: Job? = null

    // Incremented per run so a cancelled run can never write into a newer grid
    private var styleGridRunId = 0

    /**
     * Starts generating up to four hairstyle previews on the current user photo.
     * Free tier: only as many tiles as the remaining free previews allow; the rest stay locked.
     */
    fun startStyleGrid(context: Context, styles: List<HairstyleItem>) {
        val photo = _userPhoto.value
        if (photo == null) {
            _userFeedback.value = TryOnError.NoPhotoSelected.message
            return
        }
        val picks = styles.take(StyleGridGenerator.GRID_SIZE)
        if (picks.isEmpty()) return

        styleGridJob?.cancel()
        _styleGrid.value = StyleGridState(
            styles = picks,
            tiles = List(picks.size) { GridTileState.Queued }
        )
        runStyleGrid(context.applicationContext, photo, picks.indices.toList())
    }

    /** User-initiated retry of a single failed tile (never automatic). */
    fun retryStyleGridTile(context: Context, index: Int) {
        val state = _styleGrid.value
        if (state.isRunning || state.tiles.getOrNull(index) !is GridTileState.Failed) return
        val photo = _userPhoto.value ?: return
        runStyleGrid(context.applicationContext, photo, listOf(index))
    }

    /** Generates the tiles that were locked by the free limit, e.g. after upgrading. */
    fun unlockStyleGridTiles(context: Context) {
        val state = _styleGrid.value
        if (state.isRunning) return
        val locked = state.tiles.indices.filter { state.tiles[it] is GridTileState.Locked }
        val photo = _userPhoto.value ?: return
        if (locked.isNotEmpty()) runStyleGrid(context.applicationContext, photo, locked)
    }

    /**
     * Makes a finished grid tile the current try-on result so the regular result screen can show it.
     * Returns the hairstyle of that tile, or null if the tile is not ready.
     */
    fun openStyleGridResult(index: Int): HairstyleItem? {
        val state = _styleGrid.value
        val tile = state.tiles.getOrNull(index) as? GridTileState.Ready ?: return null
        val style = state.styles[index]
        _activeTryOnItem.value = style
        _aiResultBitmap.value = tile.bitmap
        _isAiGenerated.value = tile.isAiGenerated
        return style
    }

    private fun runStyleGrid(context: Context, photo: UserPhotoSource, indices: List<Int>) {
        val isPaid = _isPaidVersion.value
        // Same routing as single try-on: Paid uses the dedicated REST API, Free uses Gemini
        val service: AiHairstyleService = if (isPaid) PaidApiTryOnRepository else aiService
        // The free preview quota only limits the free tier
        val allowed = if (isPaid) indices.size else minOf(indices.size, quotaManager.remainingAttempts.value)
        val styles = _styleGrid.value.styles
        val tint = _selectedColorTint.value

        val runId = ++styleGridRunId
        _styleGrid.update { it.copy(isRunning = true) }
        styleGridJob = viewModelScope.launch {
            try {
                StyleGridGenerator.generate(
                    styles = styles,
                    allowed = allowed,
                    indices = indices,
                    generate = { style ->
                        val result = service.transformHairstyle(context, photo, style, tint)
                        if (result is AiTryOnResult.Success) {
                            if (!isPaid) quotaManager.recordSuccess()
                            _tryOnHistory.update { history ->
                                listOf(
                                    TryOnHistoryItem(
                                        id = "hist_${System.currentTimeMillis()}_${style.id}",
                                        hairstyleName = style.name,
                                        category = "${style.gender.displayName} • ${style.category}",
                                        date = "Just now",
                                        matchScore = 98,
                                        imageRes = R.drawable.img_tryon_result
                                    )
                                ) + history
                            }
                        }
                        result
                    },
                    onTileUpdate = { index, tile ->
                        if (runId == styleGridRunId) _styleGrid.update { state ->
                            state.copy(tiles = state.tiles.toMutableList().also { it[index] = tile })
                        }
                    }
                )
            } finally {
                if (runId == styleGridRunId) _styleGrid.update { it.copy(isRunning = false) }
            }
        }
    }

    // Style Score lifestyle scenes for the current try-on look
    private val _sceneStudio = MutableStateFlow(SceneStudioState())
    val sceneStudio: StateFlow<SceneStudioState> = _sceneStudio.asStateFlow()

    private var sceneJob: Job? = null

    /** Starts a fresh scene set when the current look differs from the one the scenes were made for. */
    fun prepareSceneStudio() {
        val lookId = _aiResultBitmap.value?.let { System.identityHashCode(it) } ?: 0
        if (_sceneStudio.value.lookId != lookId) {
            sceneJob?.cancel()
            _sceneStudio.value = SceneStudioState(lookId = lookId)
        }
    }

    /**
     * Generates one lifestyle scene from the current try-on result (user-initiated, no retries).
     * Free tier: uses one free preview per successful scene; locked when none are left.
     */
    fun generateScene(context: Context, scene: LifestyleScene, renderStyle: SceneRenderStyle) {
        val source = _aiResultBitmap.value ?: return
        val style = _activeTryOnItem.value ?: return
        val state = _sceneStudio.value
        if (state.isGenerating) return

        val key = SceneStudioState.key(scene, renderStyle)
        val isPaid = _isPaidVersion.value
        if (!isPaid && !quotaManager.canGenerate()) {
            _sceneStudio.update { it.copy(scenes = it.scenes + (key to GridTileState.Locked)) }
            return
        }

        val lookId = state.lookId
        val appContext = context.applicationContext
        _sceneStudio.update { it.copy(isGenerating = true, scenes = it.scenes + (key to GridTileState.Generating)) }
        sceneJob = viewModelScope.launch {
            try {
                val prompt = ScenePromptBuilder.buildScenePrompt(style, scene, renderStyle)
                val tile = when (val result = sceneService.editImage(appContext, UserPhotoSource.Captured(source), prompt)) {
                    is AiTryOnResult.Success -> {
                        if (!isPaid) quotaManager.recordSuccess()
                        GridTileState.Ready(result.resultBitmap, result.isAiGenerated)
                    }
                    is AiTryOnResult.Error -> GridTileState.Failed(
                        result.error,
                        result.detailedMessage ?: result.error.message
                    )
                }
                if (_sceneStudio.value.lookId == lookId) {
                    _sceneStudio.update { it.copy(scenes = it.scenes + (key to tile)) }
                }
            } finally {
                if (_sceneStudio.value.lookId == lookId) {
                    _sceneStudio.update { it.copy(isGenerating = false) }
                }
            }
        }
    }

    /**
     * Privacy requirement: allows users to delete current transformation result.
     */
    fun deleteCurrentAiResult() {
        _aiResultBitmap.value = null
        _isAiGenerated.value = false
        _userFeedback.value = "Preview deleted"
    }

    /**
     * Privacy requirement: allows users to delete saved results from history.
     */
    fun deleteHistoryItem(id: String) {
        _tryOnHistory.update { current -> current.filterNot { it.id == id } }
        _userFeedback.value = "Removed from history"
    }

    /**
     * Safely clears all session try-on history without modifying profile or preferences.
     */
    fun clearAllHistory() {
        _tryOnHistory.value = emptyList()
        _userFeedback.value = "Try-on history cleared"
    }

    /**
     * Safely clears all saved favorites without modifying profile or other preferences.
     */
    fun clearAllFavorites() {
        _favoriteIds.value = emptySet()
        _userFeedback.value = "All favorites cleared"
    }

    /**
     * Resets quota during development/testing.
     */
    fun resetQuotaForTesting() {
        quotaManager.resetQuotaForTesting()
        _tryOnError.value = null
        _aiErrorMessage.value = null
        _userFeedback.value = "Free limit reset to ${quotaManager.maxDailyLimit}/${quotaManager.maxDailyLimit}"
    }

    fun setSelectedColorTint(tint: String) {
        _selectedColorTint.value = tint
    }

    fun updateUserFaceShape(faceShape: String) {
        _userProfile.update { it.copy(faceShape = faceShape) }
        _confirmedFaceShape.value = faceShape
    }

    // Face shape detected or chosen by the user (null until then), used by the Style Score
    private val _confirmedFaceShape = MutableStateFlow<String?>(null)
    val confirmedFaceShape: StateFlow<String?> = _confirmedFaceShape.asStateFlow()

    // Skin undertone from the AI Face Scan, used for the Style Score color harmony
    private val _skinUndertone = MutableStateFlow<HairColorAdvisor.Undertone?>(null)
    val skinUndertone: StateFlow<HairColorAdvisor.Undertone?> = _skinUndertone.asStateFlow()

    fun updateSkinTone(skinTone: HairColorAdvisor.SkinToneResult?) {
        if (skinTone != null) _skinUndertone.value = skinTone.undertone
    }

    fun updateUserHairType(hairType: String) {
        _userProfile.update { it.copy(hairType = hairType) }
    }

    fun updateUserName(name: String) {
        val currentMonthYear = try {
            val sdf = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.ENGLISH)
            sdf.format(java.util.Date())
        } catch (e: Exception) {
            "October 2026"
        }
        val cleanName = name.trim().ifBlank { "HairStyle Studio" }
        _userProfile.update {
            it.copy(
                userName = cleanName,
                memberSince = currentMonthYear
            )
        }
        themePrefs.edit()
            .putString("pref_user_name", cleanName)
            .putString("pref_member_since", currentMonthYear)
            .apply()
        _userFeedback.value = "Profile updated: $cleanName"
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    fun showFeedback(msg: String) {
        _userFeedback.value = msg
    }
}
