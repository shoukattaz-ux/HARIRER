package com.example

import android.app.Application
import android.util.Log
import com.example.data.api.AiBackendConfig
import com.example.data.repository.AiQuotaManager
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class HairLookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AiQuotaManager.init(this)
        setUpFirebase()
    }

    /**
     * Enables Firebase AI Logic + App Check when google-services.json is present.
     * Release builds attest with Play Integrity; debug builds use the App Check debug provider
     * (register the debug token printed in Logcat in the Firebase console).
     */
    private fun setUpFirebase() {
        val configured = try {
            FirebaseApp.getApps(this).isNotEmpty() || FirebaseApp.initializeApp(this) != null
        } catch (e: Exception) {
            Log.w(TAG, "Firebase is not configured", e)
            false
        }
        if (configured) {
            val factory = if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)
        }
        AiBackendConfig.isFirebaseConfigured = configured
    }

    private companion object {
        const val TAG = "HairLookApp"
    }
}
