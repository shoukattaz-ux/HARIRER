package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.AiTryOnConfig
import com.example.data.api.HairstylePromptBuilder
import com.example.data.model.Gender
import com.example.data.model.ProcessingStage
import com.example.data.model.TryOnError
import com.example.data.model.UserPhotoSource
import com.example.data.repository.AiQuotaManager
import com.example.data.repository.GeminiTryOnRepository
import com.example.data.repository.HairstyleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `launch MainActivity successfully`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HairLook", appName)
    }

    @Test
    fun `verify men and women hairstyles separation`() {
        val menStyles = HairstyleRepository.getHairstylesByGender(Gender.MEN)
        val womenStyles = HairstyleRepository.getHairstylesByGender(Gender.WOMEN)

        assertTrue(menStyles.isNotEmpty())
        assertTrue(womenStyles.isNotEmpty())

        assertTrue(menStyles.all { it.gender == Gender.MEN })
        assertTrue(womenStyles.all { it.gender == Gender.WOMEN })
    }

    @Test
    fun `verify home screen men and women horizontal menu categories`() {
        val homeMen = HairstyleRepository.homeMenCategories
        val homeWomen = HairstyleRepository.homeWomenCategories

        assertEquals(listOf("All", "Fade", "Short", "Medium", "Long", "Curly", "Wavy"), homeMen)
        assertEquals(listOf("All", "Short", "Medium", "Long", "Curly", "Wavy", "Braids"), homeWomen)
    }

    @Test
    fun `verify home filtering by category and search`() {
        // Test Men Fade filter
        val menFades = HairstyleRepository.filterHome(Gender.MEN, "Fade", null)
        assertTrue(menFades.isNotEmpty())
        assertTrue(menFades.all { it.gender == Gender.MEN })

        // Test Women Short filter
        val womenShort = HairstyleRepository.filterHome(Gender.WOMEN, "Short", null)
        assertTrue(womenShort.isNotEmpty())
        assertTrue(womenShort.all { it.gender == Gender.WOMEN && it.hairLength.equals("Short", ignoreCase = true) })

        // Test Search query on Men
        val searchBuzz = HairstyleRepository.filterHome(Gender.MEN, "All", "buzz")
        assertTrue(searchBuzz.isNotEmpty())
        assertTrue(searchBuzz.any { it.name.contains("Buzz", ignoreCase = true) })

        // Test Search with no matches
        val noMatch = HairstyleRepository.filterHome(Gender.MEN, "All", "xyznonexistent")
        assertTrue(noMatch.isEmpty())
    }

    @Test
    fun `verify try on processing stages and error models`() {
        val stages = ProcessingStage.values()
        assertEquals(6, stages.size)
        assertTrue(stages.contains(ProcessingStage.ANALYZING_FACE))
        assertTrue(stages.contains(ProcessingStage.AI_RENDERING))
        assertTrue(stages.contains(ProcessingStage.FINALIZING))

        val cameraDenied = TryOnError.CameraPermissionDenied
        assertTrue(cameraDenied.title.isNotEmpty())
        assertTrue(cameraDenied.message.isNotEmpty())

        val missingKey = TryOnError.MissingApiKey
        assertTrue(missingKey.title.contains("API"))
        assertTrue(missingKey.message.contains("GEMINI_API_KEY"))

        // Exact error message tests from Step 4 specs
        val rateLimit = TryOnError.RateLimitExceeded
        assertEquals("Free AI preview limit reached. Please try again later.", rateLimit.message)

        val quotaExceeded = TryOnError.QuotaExceeded
        assertEquals("Free AI preview limit has been reached. Please try again later.", quotaExceeded.message)

        val networkError = TryOnError.NetworkFailure
        assertEquals("Connection problem. Please check your internet and try again.", networkError.message)

        val invalidImage = TryOnError.InvalidImageFormat
        assertEquals("Please choose a clear face photo and try again.", invalidImage.message)

        val otherError = TryOnError.ProcessingFailed
        assertEquals("Unable to create the preview right now. Please try again.", otherError.message)

        val preset = UserPhotoSource.PresetModel(R.drawable.img_men_hairstyles, "Model")
        assertEquals("Model", preset.name)
    }

    @Test
    fun `verify free daily quota limit and tracking`() {
        assertEquals(3, AiTryOnConfig.FREE_DAILY_TRY_LIMIT)
        assertEquals("gemini-2.5-flash-image", AiTryOnConfig.PRIMARY_MODEL)

        AiQuotaManager.resetQuotaForTesting()
        assertEquals(3, AiQuotaManager.remainingAttempts.value)
        assertTrue(AiQuotaManager.canGenerate())
        assertEquals("Free previews remaining: 3/3", AiQuotaManager.getQuotaStatusText())

        // Simulate successful generation 1
        AiQuotaManager.recordSuccess()
        assertEquals(2, AiQuotaManager.remainingAttempts.value)
        assertTrue(AiQuotaManager.canGenerate())
        assertEquals("Free previews remaining: 2/3", AiQuotaManager.getQuotaStatusText())

        // Simulate successful generation 2
        AiQuotaManager.recordSuccess()
        assertEquals(1, AiQuotaManager.remainingAttempts.value)
        assertTrue(AiQuotaManager.canGenerate())
        assertEquals("Free previews remaining: 1/3", AiQuotaManager.getQuotaStatusText())

        // Simulate successful generation 3 (limit reached)
        AiQuotaManager.recordSuccess()
        assertEquals(0, AiQuotaManager.remainingAttempts.value)
        assertFalse(AiQuotaManager.canGenerate())

        // Reset
        AiQuotaManager.resetQuotaForTesting()
        assertEquals(3, AiQuotaManager.remainingAttempts.value)
    }

    @Test
    fun `verify prompt builder preserves identity and category`() {
        val menStyle = HairstyleRepository.getById("m_quiff_01")
        assertNotNull(menStyle)

        val menPrompt = HairstylePromptBuilder.buildTransformationPrompt(menStyle!!, "Dark Brown")
        assertTrue(menPrompt.contains(menStyle.name))
        assertTrue(menPrompt.contains("men's haircut"))
        assertTrue(menPrompt.contains("IDENTITY"))
        assertTrue(menPrompt.contains("Dark Brown"))

        val womenStyle = HairstyleRepository.getById("w_bob_01")
        assertNotNull(womenStyle)

        val womenPrompt = HairstylePromptBuilder.buildTransformationPrompt(womenStyle!!)
        assertTrue(womenPrompt.contains(womenStyle.name))
        assertTrue(womenPrompt.contains("women's hairstyle"))
        assertTrue(womenPrompt.contains("IDENTITY"))
    }

    @Test
    fun `verify all required categories exist`() {
        val menCategories = HairstyleRepository.menCategories
        val womenCategories = HairstyleRepository.womenCategories

        // Men categories check
        assertTrue(menCategories.contains("Fade"))
        assertTrue(menCategories.contains("Low Fade"))
        assertTrue(menCategories.contains("Mid Fade"))
        assertTrue(menCategories.contains("High Fade"))
        assertTrue(menCategories.contains("Buzz Cut"))
        assertTrue(menCategories.contains("Crew Cut"))
        assertTrue(menCategories.contains("Quiff"))
        assertTrue(menCategories.contains("Undercut"))
        assertTrue(menCategories.contains("Pompadour"))
        assertTrue(menCategories.contains("Curly"))
        assertTrue(menCategories.contains("Wavy"))
        assertTrue(menCategories.contains("Long Hair"))
        assertTrue(menCategories.contains("Textured Crop"))

        // Women categories check
        assertTrue(womenCategories.contains("Bob"))
        assertTrue(womenCategories.contains("Pixie"))
        assertTrue(womenCategories.contains("Layers"))
        assertTrue(womenCategories.contains("Long Hair"))
        assertTrue(womenCategories.contains("Curly"))
        assertTrue(womenCategories.contains("Wavy"))
        assertTrue(womenCategories.contains("Braids"))
        assertTrue(womenCategories.contains("Ponytail"))
        assertTrue(womenCategories.contains("Updo"))
    }

    @Test
    fun `verify all 10 key features and 6 supported languages exist`() {
        val features = com.example.ui.components.AllKeyFeatures
        assertEquals(10, features.size)

        val featureIds = features.map { it.id }
        assertTrue(featureIds.contains("men_women_category"))
        assertTrue(featureIds.contains("live_camera_tryon"))
        assertTrue(featureIds.contains("upload_photo_tryon"))
        assertTrue(featureIds.contains("face_shape_analysis"))
        assertTrue(featureIds.contains("color_tinting"))
        assertTrue(featureIds.contains("compare_styles"))
        assertTrue(featureIds.contains("barber_guide"))
        assertTrue(featureIds.contains("favorites_history"))
        assertTrue(featureIds.contains("multi_language"))
        assertTrue(featureIds.contains("monetization_tier"))

        // Verify all have English and Urdu titles
        assertTrue(features.all { it.titleEn.isNotEmpty() && it.titleUrdu.isNotEmpty() })

        val languages = com.example.ui.components.AppLanguage.values()
        assertEquals(6, languages.size)
        val langCodes = languages.map { it.code }
        assertTrue(langCodes.contains("en"))
        assertTrue(langCodes.contains("ur"))
        assertTrue(langCodes.contains("hi"))
        assertTrue(langCodes.contains("es"))
        assertTrue(langCodes.contains("ar"))
        assertTrue(langCodes.contains("fr"))
    }
}
