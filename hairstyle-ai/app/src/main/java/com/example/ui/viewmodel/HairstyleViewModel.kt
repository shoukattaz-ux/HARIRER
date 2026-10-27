package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.api.AiHairstyleService
import com.example.data.api.AiTryOnConfig
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.ProcessingStage
import com.example.data.model.TryOnError
import com.example.data.model.TryOnHistoryItem
import com.example.data.model.UserHairProfile
import com.example.data.model.UserPhotoSource
import com.example.data.repository.AiQuotaManager
import com.example.data.repository.AiTryOnResult
import com.example.data.repository.GeminiTryOnRepository
import com.example.data.repository.HairstyleRepository
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
    private val quotaManager: AiQuotaManager
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        aiService = GeminiTryOnRepository,
        quotaManager = AiQuotaManager
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
