package com.example.ui.navigation

sealed class AppScreen {
    object Splash : AppScreen()
    object Welcome : AppScreen() // First page: Choose Hairstyle Try-On or Clothes Try-On
    object ClothesHome : AppScreen() // Clothes Home Screen (Men, Women, Baby, Saved)
    data class ClothesTryOn(val outfitId: String? = null) : AppScreen() // Outfit & Clothes Virtual Dressing Room
    object Home : AppScreen()
    object ChooseStyle : AppScreen() // Screen 3: Choose Style - Men or Women?
    object MenGallery : AppScreen() // Screen 4: Men Hairstyles
    object WomenGallery : AppScreen() // Screen 5: Women Hairstyles
    data class Detail(val hairstyleId: String) : AppScreen()
    data class TryHairstyle(val hairstyleId: String? = null) : AppScreen() // Screen 6: Live Camera Try-On
    data class CameraUpload(val hairstyleId: String? = null) : AppScreen() // Screen 7: Upload Photo
    data class PhotoPreview(val hairstyleId: String? = null) : AppScreen() // Screen 8: Preview Hairstyle
    data class CompareStyles(val styleId1: String? = null, val styleId2: String? = null) : AppScreen() // Screen 9: Compare Styles
    object Favorites : AppScreen() // Screen 10: Saved & Favorites
    object FaceShape : AppScreen() // Screens 11 & 12: Face Shape & AI Recommendation
    object FaceScan : AppScreen() // AI Face Scan: animated face shape detection + Personalized Style Advice report
    object StyleGrid : AppScreen() // "My 4 AI Looks": four recommended hairstyles generated on the user's photo
    object StyleScore : AppScreen() // Style Score + AI lifestyle scenes for the current try-on look
    data class BarberGuide(val hairstyleId: String? = null) : AppScreen() // Screen 13: Barber Guide
    object SearchExplore : AppScreen() // Screen 14: Search & Filters
    object Profile : AppScreen() // Screen 15: Profile
    data class Processing(val hairstyleId: String? = null) : AppScreen()
    data class TryOnResult(
        val hairstyleId: String? = null,
        val isCustomPhoto: Boolean = false
    ) : AppScreen()
    object PaidAiStudio : AppScreen()
}
