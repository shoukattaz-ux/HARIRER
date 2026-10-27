package com.example.data.repository

import com.example.R
import com.example.data.model.Gender
import com.example.data.model.HairstyleCategory
import com.example.data.model.HairstyleItem
import com.example.data.model.HairstyleVariation
import com.example.data.model.TryOnHistoryItem
import com.example.data.model.toVariationItem

object HairstyleRepository {

    val menCategories = listOf(
        "All",
        "Fade",
        "Low Fade",
        "Mid Fade",
        "High Fade",
        "Buzz Cut",
        "Crew Cut",
        "Quiff",
        "Side Part",
        "Undercut",
        "Pompadour",
        "Curly",
        "Wavy",
        "Long Hair",
        "Textured Crop"
    )

    val homeMenCategories = listOf(
        "All",
        "Fade",
        "Short",
        "Medium",
        "Long",
        "Curly",
        "Buzz Cut",
        "Quiff",
        "Side Part",
        "Wavy"
    )

    val womenCategories = listOf(
        "All",
        "Short",
        "Bob",
        "Medium",
        "Long",
        "Layers",
        "Pixie",
        "Curly",
        "Wavy",
        "Braids",
        "Ponytail",
        "Updo"
    )

    val homeWomenCategories = listOf(
        "All",
        "Ponytail",
        "Braids",
        "Pixie",
        "Layers",
        "Short",
        "Bob",
        "Medium",
        "Long",
        "Curly",
        "Wavy"
    )

    private val allHairstyles = listOf(
        // MEN'S SHORT HAIRSTYLES SERIES (USER UPLOADED - 9 HD SHORT DESIGNS)
        // IMAGE 1 (FRONT / PRIMARY SHORT CARD): Classic Textured Short Crop
        HairstyleItem(
            id = "m_short_01",
            name = "Classic Textured Short Crop",
            category = "Short",
            gender = Gender.MEN,
            description = "A versatile classic short haircut with rich top texture, clean temple contours, and a neat tapered neckline. Image 1 front featured short style.",
            stylingTips = "Rub a dime-sized amount of matte clay between palms and finger-comb forward and upward for clean texture.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_1,
            overlayAssetId = "overlay_m_crop",
            rating = 5.0f,
            tryOnCount = "52.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Crop", "Fade", "Clean", "Trending", "Popular", "Classic"),
            variations = listOf(
                HairstyleVariation(
                    id = "var_short_1",
                    name = "Design 1: Classic Short Crop",
                    hairLength = "Short",
                    hairTexture = "Textured Short Crop",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Taper Fade",
                    description = "Front primary design featuring dense textured short crown and clean contour.",
                    stylingTips = "Work matte paste through dry hair for natural separation.",
                    faceShapes = listOf("Oval", "Square", "Round"),
                    imageRes = R.drawable.img_short_1,
                    overlayAssetId = "overlay_m_crop"
                ),
                HairstyleVariation(
                    id = "var_short_2",
                    name = "Design 2: Modern Tapered Short",
                    hairLength = "Short",
                    hairTexture = "Straight to Textured",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Mid Fade",
                    description = "Modern tapered short cut with sharp side gradation and balanced top volume.",
                    stylingTips = "Light blow dry forward and finish with pliable pomade.",
                    faceShapes = listOf("Oval", "Diamond", "Square"),
                    imageRes = R.drawable.img_short_2,
                    overlayAssetId = "overlay_m_modern_quiff"
                ),
                HairstyleVariation(
                    id = "var_short_3",
                    name = "Design 3: Precision Buzz Mid Fade",
                    hairLength = "Short",
                    hairTexture = "Clean Buzz Cut",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Mid Skin Fade",
                    description = "Ultra-clean uniform buzz cut paired with seamless skin fade transition.",
                    stylingTips = "Zero daily styling required. Keep scalp moisturized.",
                    faceShapes = listOf("Oval", "Square", "Round"),
                    imageRes = R.drawable.img_short_3,
                    overlayAssetId = "overlay_m_buzz"
                ),
                HairstyleVariation(
                    id = "var_short_4",
                    name = "Design 4: Side Swept Executive Taper",
                    hairLength = "Short",
                    hairTexture = "Polished Side Swept",
                    maintenance = "Medium",
                    stylingDifficulty = "Medium",
                    fadeLevel = "Classic Taper",
                    description = "Refined side-swept short style with natural comb parting and executive taper.",
                    stylingTips = "Comb to the side while damp with a touch of medium-shine cream.",
                    faceShapes = listOf("Oval", "Round", "Square"),
                    imageRes = R.drawable.img_short_4,
                    overlayAssetId = "overlay_m_pompadour"
                ),
                HairstyleVariation(
                    id = "var_short_5",
                    name = "Design 5: European Blunt Fringe Crop",
                    hairLength = "Short",
                    hairTexture = "Dense Forward Crop",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Low Taper Fade",
                    description = "Crisp European forward crop with horizontal blunt fringe line and low taper sides.",
                    stylingTips = "Dust texturizing powder over roots for matte lift without grease.",
                    faceShapes = listOf("Oval", "Round", "Heart"),
                    imageRes = R.drawable.img_short_5,
                    overlayAssetId = "overlay_m_crop"
                ),
                HairstyleVariation(
                    id = "var_short_6",
                    name = "Design 6: Tousled Short Wavy Texture",
                    hairLength = "Short",
                    hairTexture = "Natural Wavy Flow",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Soft Taper",
                    description = "Natural wavy movement swept forward with controlled volume and soft edges.",
                    stylingTips = "Spritz sea salt spray and air dry for organic wave pattern.",
                    faceShapes = listOf("Oval", "Square", "Diamond"),
                    imageRes = R.drawable.img_short_6,
                    overlayAssetId = "overlay_m_natural_quiff"
                ),
                HairstyleVariation(
                    id = "var_short_7",
                    name = "Design 7: Short Spiky High Skin Fade",
                    hairLength = "Short",
                    hairTexture = "Upward Spiky Texture",
                    maintenance = "Medium",
                    stylingDifficulty = "Medium",
                    fadeLevel = "High Skin Fade",
                    description = "Dynamic upward spiky texture balanced by a sharp high skin fade.",
                    stylingTips = "Piece out front tips upward with firm-hold matte clay.",
                    faceShapes = listOf("Oval", "Square", "Diamond"),
                    imageRes = R.drawable.img_short_7,
                    overlayAssetId = "overlay_m_modern_quiff"
                ),
                HairstyleVariation(
                    id = "var_short_8",
                    name = "Design 8: Classic Clean Crew Cut",
                    hairLength = "Short",
                    hairTexture = "Traditional Crew Cut",
                    maintenance = "Low",
                    stylingDifficulty = "Easy",
                    fadeLevel = "Medium Taper",
                    description = "Timeless masculine crew cut with short tapered crown and clean edges.",
                    stylingTips = "Towel dry and brush forward for effortless grooming.",
                    faceShapes = listOf("Oval", "Square", "Round"),
                    imageRes = R.drawable.img_short_8,
                    overlayAssetId = "overlay_m_buzz"
                ),
                HairstyleVariation(
                    id = "var_short_9",
                    name = "Design 9: Short Sleek Comb Over Fade",
                    hairLength = "Short",
                    hairTexture = "Sleek Comb Over",
                    maintenance = "Medium",
                    stylingDifficulty = "Medium",
                    fadeLevel = "Drop Fade",
                    description = "Polished comb-over contours with drop fade sides and gentleman aesthetic.",
                    stylingTips = "Part neatly with a fine-tooth comb and blow-dry on low heat.",
                    faceShapes = listOf("Oval", "Square", "Heart"),
                    imageRes = R.drawable.img_short_9,
                    overlayAssetId = "overlay_m_pompadour"
                )
            )
        ),

        // IMAGE 2: Modern Tapered Short Fade
        HairstyleItem(
            id = "m_short_02",
            name = "Modern Tapered Short Fade",
            category = "Short",
            gender = Gender.MEN,
            description = "Modern tapered short haircut with sharp side gradation, clean temple blend, and balanced crown volume.",
            stylingTips = "Light blow dry forward and finish with pliable styling pomade.",
            faceShapes = listOf("Oval", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_2,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "41.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Taper", "Fade", "Modern", "Clean")
        ),

        // IMAGE 3: Precision Short Buzz Cut
        HairstyleItem(
            id = "m_short_03",
            name = "Precision Short Buzz Cut",
            category = "Short",
            gender = Gender.MEN,
            description = "Ultra-clean uniform buzz cut paired with a seamless mid skin fade and sharp razor hairline edge-up.",
            stylingTips = "Virtually zero daily styling required. Keep scalp moisturized with lightweight oil.",
            faceShapes = listOf("Oval", "Square", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_3,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "35.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Buzz Cut", "Fade", "Minimal", "Sharp")
        ),

        // IMAGE 4: Short Side Swept Executive Taper
        HairstyleItem(
            id = "m_short_04",
            name = "Short Side Swept Executive Taper",
            category = "Short",
            gender = Gender.MEN,
            description = "Refined side-swept short style with natural comb parting, neat executive taper, and professional look.",
            stylingTips = "Comb to the side while damp with a touch of medium-shine styling cream.",
            faceShapes = listOf("Oval", "Round", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_short_4,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "38.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Side Part", "Executive", "Classic", "Professional")
        ),

        // IMAGE 5: European Short Blunt Crop
        HairstyleItem(
            id = "m_short_05",
            name = "European Short Blunt Crop",
            category = "Short",
            gender = Gender.MEN,
            description = "Crisp European forward crop with horizontal blunt fringe line and low taper fade sides.",
            stylingTips = "Dust texturizing powder over roots for matte lift and piecey separation.",
            faceShapes = listOf("Oval", "Round", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_5,
            overlayAssetId = "overlay_m_crop",
            rating = 4.9f,
            tryOnCount = "29.4k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Crop", "Blunt Fringe", "Fade", "Textured")
        ),

        // IMAGE 6: Short Tousled Wave Texture
        HairstyleItem(
            id = "m_short_06",
            name = "Short Tousled Wave Texture",
            category = "Short",
            gender = Gender.MEN,
            description = "Natural wavy movement swept forward with controlled volume, soft temple edges, and easy management.",
            stylingTips = "Spritz sea salt spray and air dry for organic wave pattern.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_6,
            overlayAssetId = "overlay_m_natural_quiff",
            rating = 4.8f,
            tryOnCount = "31.0k",
            isTrending = false,
            isPopular = false,
            tags = listOf("Short", "Wavy", "Tousled", "Natural", "Volume")
        ),

        // IMAGE 7: Short Spiky High Skin Fade
        HairstyleItem(
            id = "m_short_07",
            name = "Short Spiky High Skin Fade",
            category = "Short",
            gender = Gender.MEN,
            description = "Dynamic upward spiky texture balanced by a sharp high skin fade and crisp modern outline.",
            stylingTips = "Piece out front tips upward with firm-hold matte clay.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_short_7,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "34.6k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Short", "Spiky", "Skin Fade", "Textured", "Modern")
        ),

        // IMAGE 8: Classic Short Crew Cut
        HairstyleItem(
            id = "m_short_08",
            name = "Classic Short Crew Cut",
            category = "Short",
            gender = Gender.MEN,
            description = "Timeless masculine crew cut with short tapered crown, tidy sideburns, and clean neckline.",
            stylingTips = "Towel dry and brush forward for effortless daily grooming.",
            faceShapes = listOf("Oval", "Square", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_short_8,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "27.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Crew Cut", "Clean", "Taper", "Classic")
        ),

        // IMAGE 9: Short Sleek Comb Over Fade
        HairstyleItem(
            id = "m_short_09",
            name = "Short Sleek Comb Over Fade",
            category = "Short",
            gender = Gender.MEN,
            description = "Polished comb-over contours with drop fade sides, gentleman elegance, and sharp finish.",
            stylingTips = "Part neatly with a fine-tooth comb and blow-dry on low heat.",
            faceShapes = listOf("Oval", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_short_9,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "33.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Comb Over", "Fade", "Polished", "Gentleman")
        ),

        // =========================================================================
        // MEN'S MEDIUM HAIRSTYLES SERIES (USER UPLOADED - 11 HD MEDIUM DESIGNS)
        // High quality, neck to crown hairstyle focus, complete face & hair silhouette
        // =========================================================================

        // MEDIUM DESIGN 1 (img_medium_1.jpg)
        HairstyleItem(
            id = "m_medium_01",
            name = "Textured Medium Crop & Taper",
            category = "Medium",
            gender = Gender.MEN,
            description = "Natural medium textured crop with balanced crown volume, clean taper sides, and structured face framing.",
            stylingTips = "Apply sea salt spray to damp hair, blow-dry on medium heat, and piece out texture with matte clay.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_1,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "36.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Crop", "Textured", "Modern", "Taper")
        ),

        // MEDIUM DESIGN 2 (img_medium_2.webp)
        HairstyleItem(
            id = "m_medium_02",
            name = "Classic Medium Taper Flow",
            category = "Medium",
            gender = Gender.MEN,
            description = "Versatile medium length taper cut with elegant natural flow, soft contours, and timeless masculine appeal.",
            stylingTips = "Warm a small scoop of styling cream and sweep hair backward and to the side for effortless flow.",
            faceShapes = listOf("Oval", "Square", "Round", "Oblong"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_2,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.8f,
            tryOnCount = "31.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Flow", "Taper", "Classic", "Clean")
        ),

        // MEDIUM DESIGN 3 (img_medium_3.png)
        HairstyleItem(
            id = "m_medium_03",
            name = "Executive Medium Pompadour Fade",
            category = "Medium",
            gender = Gender.MEN,
            description = "Polished medium pompadour featuring generous swept-back height, clean temple transition, and sophisticated finish.",
            stylingTips = "Use a round brush while blow-drying backward, then lock in volume with a pliable water-based pomade.",
            faceShapes = listOf("Oval", "Round", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_3,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "42.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Pompadour", "Executive", "Volume", "Fade")
        ),

        // MEDIUM DESIGN 4 (img_medium_4.jpg)
        HairstyleItem(
            id = "m_medium_04",
            name = "Modern Medium Quiff & Drop Fade",
            category = "Medium",
            gender = Gender.MEN,
            description = "High-impact medium quiff with sharp front fringe elevation, soft drop fade gradient, and sharp contour line.",
            stylingTips = "Direct airflow upward at the roots while drying, then set with matte paste for all-day hold.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_4,
            overlayAssetId = "overlay_m_natural_quiff",
            rating = 4.8f,
            tryOnCount = "29.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Quiff", "Drop Fade", "Sharp", "Trending")
        ),

        // MEDIUM DESIGN 5 (img_medium_5.webp)
        HairstyleItem(
            id = "m_medium_05",
            name = "Tousled Medium Wavy Waves",
            category = "Medium",
            gender = Gender.MEN,
            description = "Dynamic medium cut showcasing textured natural wave clumps, lightweight bounce, and modern casual confidence.",
            stylingTips = "Scrunch curl mousse into towel-dried hair and air-dry or diffuse on low for soft natural movement.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_5,
            overlayAssetId = "overlay_m_curly",
            rating = 4.9f,
            tryOnCount = "39.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Wavy", "Tousled", "Casual", "Volume")
        ),

        // MEDIUM DESIGN 6 (img_medium_6.jpg)
        HairstyleItem(
            id = "m_medium_06",
            name = "Relaxed Medium Layered Shag",
            category = "Medium",
            gender = Gender.MEN,
            description = "Effortless medium layered silhouette with natural neck drape, face-flattering curtain fringe, and easy movement.",
            stylingTips = "Finger-comb with a leave-in conditioner to enhance strand definition and prevent frizz.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_medium_6,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.8f,
            tryOnCount = "34.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Shag", "Layers", "Relaxed", "Curtain")
        ),

        // MEDIUM DESIGN 7 (img_medium_7.webp)
        HairstyleItem(
            id = "m_medium_07",
            name = "Gentleman Medium Side Part Contour",
            category = "Medium",
            gender = Gender.MEN,
            description = "Neat razor-precise medium parting with sculptured side sweep, balanced crown weight, and refined profile.",
            stylingTips = "Comb diagonally across from natural part with styling wax for subtle shine and control.",
            faceShapes = listOf("Oval", "Square", "Round"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_7,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.7f,
            tryOnCount = "27.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Medium", "Side Part", "Gentleman", "Refined", "Classic")
        ),

        // MEDIUM DESIGN 8 (img_medium_8.webp)
        HairstyleItem(
            id = "m_medium_08",
            name = "Voluminous Medium Brushed Back",
            category = "Medium",
            gender = Gender.MEN,
            description = "Smooth medium swept-back cut with healthy density, low taper edges, and athletic gentleman presence.",
            stylingTips = "Blow-dry straight back using warm heat, finishing with a light grooming paste.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_8,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "37.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Brushed Back", "Volume", "Sleek", "Modern")
        ),

        // MEDIUM DESIGN 9 (img_medium_9.jpg)
        HairstyleItem(
            id = "m_medium_09",
            name = "Medium Textured Fringe & Low Fade",
            category = "Medium",
            gender = Gender.MEN,
            description = "Textured forward medium fringe with soft feathering over forehead, low skin taper, and expressive contrast.",
            stylingTips = "Disperse texture clay through dry fringe and pinch ends for defined separation.",
            faceShapes = listOf("Oval", "Oblong", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_medium_9,
            overlayAssetId = "overlay_m_crop",
            rating = 4.8f,
            tryOnCount = "31.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Fringe", "Fade", "Textured", "Youthful")
        ),

        // MEDIUM DESIGN 10 (img_medium_10.webp)
        HairstyleItem(
            id = "m_medium_10",
            name = "Urban Medium Curtain Flow",
            category = "Medium",
            gender = Gender.MEN,
            description = "Trending middle-parted medium curtain flow framing cheekbones naturally with soft dimensional layers.",
            stylingTips = "Center-part slightly off-axis and flick outward with hair dryer nozzle on low heat.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_medium_10,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "45.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Curtains", "Middle Part", "Trending", "Flow")
        ),

        // MEDIUM DESIGN 11 (img_medium_11.jpg)
        HairstyleItem(
            id = "m_medium_11",
            name = "Sculpted Medium Ivy League Taper",
            category = "Medium",
            gender = Gender.MEN,
            description = "Prestige medium Ivy League cut with gentle front crest rise, clean natural sideburns, and neat neck taper.",
            stylingTips = "Style front fringe upward and slightly to side with matte pomade for collegiate sharp finish.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_medium_11,
            overlayAssetId = "overlay_m_natural_quiff",
            rating = 4.9f,
            tryOnCount = "35.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Ivy League", "Taper", "Prestige", "Clean")
        ),

        // =========================================================================
        // MEN'S LONG HAIRSTYLES SERIES (USER UPLOADED - 12 HD LONG DESIGNS)
        // High quality, neck to crown hairstyle focus, complete face & long hair silhouette
        // =========================================================================

        // LONG DESIGN 1 (img_long_1.webp)
        HairstyleItem(
            id = "m_long_01",
            name = "Textured Long Flow & Subtle Layers",
            category = "Long",
            gender = Gender.MEN,
            description = "Natural shoulder-grazing long layered flow with effortless organic wave separation, healthy volume, and masculine appeal.",
            stylingTips = "Work lightweight texturizing leave-in conditioner through damp ends and allow to air-dry naturally.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_1,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "38.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Flow", "Layers", "Textured", "Natural")
        ),

        // LONG DESIGN 2 (img_long_2.jpg)
        HairstyleItem(
            id = "m_long_02",
            name = "Classic Long Layered Curtain Waves",
            category = "Long",
            gender = Gender.MEN,
            description = "Sleek flowing long hair with face-flattering curtain part, refined crown depth, and handsome dimensional waves.",
            stylingTips = "Center-part while damp and blow-dry on low with a wide diffuser to lock in soft waves.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_2,
            overlayAssetId = "overlay_m_curly",
            rating = 4.8f,
            tryOnCount = "32.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Curtains", "Waves", "Classic", "Modern")
        ),

        // LONG DESIGN 3 (img_long_3.jpg)
        HairstyleItem(
            id = "m_long_03",
            name = "Gentleman Long Side-Swept Mane",
            category = "Long",
            gender = Gender.MEN,
            description = "Sophisticated long hairstyle with deep diagonal sweep, rich strand texture, and neat executive neck contour.",
            stylingTips = "Warm argan grooming cream between palms and finger-comb back from temple to nape.",
            faceShapes = listOf("Oval", "Round", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_3,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "41.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Side-Swept", "Gentleman", "Volume", "Elegance")
        ),

        // LONG DESIGN 4 (img_long_4.jpg)
        HairstyleItem(
            id = "m_long_04",
            name = "Modern Long Surfer Shag",
            category = "Long",
            gender = Gender.MEN,
            description = "Carefree beach-textured long shag featuring choppy multi-length layers, feathering, and natural bounce.",
            stylingTips = "Spritz sea-salt mist generously and scrunch lightly with hands for matte beachy separation.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_4,
            overlayAssetId = "overlay_m_curly",
            rating = 4.8f,
            tryOnCount = "35.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Shag", "Surfer", "Textured", "Casual")
        ),

        // LONG DESIGN 5 (img_long_5.jpg)
        HairstyleItem(
            id = "m_long_05",
            name = "Sculpted Long Viking Waves",
            category = "Long",
            gender = Gender.MEN,
            description = "Thick masculine long mane with dense wavy volume, bold face framing, and rugged Nordic aesthetic.",
            stylingTips = "Comb with a wide-tooth sandalwood comb and moisturize weekly with natural jojoba hair oil.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_5,
            overlayAssetId = "overlay_m_curly",
            rating = 5.0f,
            tryOnCount = "46.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Viking", "Bold", "Thick", "Waves")
        ),

        // LONG DESIGN 6 (img_long_6.webp)
        HairstyleItem(
            id = "m_long_06",
            name = "Bohemian Long Natural Curls",
            category = "Long",
            gender = Gender.MEN,
            description = "Springy defined long ringlet curls cascading naturally with lightweight structure and hydrated definition.",
            stylingTips = "Apply curl activator cream to wet hair and dry using a diffuser or gentle microfiber towel.",
            faceShapes = listOf("Oval", "Square", "Heart", "Round"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_6,
            overlayAssetId = "overlay_m_curly",
            rating = 4.9f,
            tryOnCount = "39.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Curly", "Bohemian", "Ringlets", "Natural")
        ),

        // LONG DESIGN 7 (img_long_7.jpg)
        HairstyleItem(
            id = "m_long_07",
            name = "Sleek Long Straight Flow",
            category = "Long",
            gender = Gender.MEN,
            description = "Polished straight long locks falling evenly with mirror gloss, clean middle part, and minimal frizz.",
            stylingTips = "Smooth a drop of anti-frizz serum down the mid-lengths and ends after towel drying.",
            faceShapes = listOf("Oval", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_7,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.7f,
            tryOnCount = "29.3k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Long", "Straight", "Sleek", "Minimal", "Polished")
        ),

        // LONG DESIGN 8 (img_long_8.jpg)
        HairstyleItem(
            id = "m_long_08",
            name = "Tapered Long Mullet Flow",
            category = "Long",
            gender = Gender.MEN,
            description = "Modern stylish long mullet with controlled top volume, neat temple tapering, and long flowing back drapery.",
            stylingTips = "Use pliable matte paste at the roots for lift and leave the back locks free-flowing.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Oblong"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_8,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.8f,
            tryOnCount = "33.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Mullet", "Taper", "Modern", "Edgy")
        ),

        // LONG DESIGN 9 (img_long_9.jpg)
        HairstyleItem(
            id = "m_long_09",
            name = "Layered Samurai Flow Cut",
            category = "Long",
            gender = Gender.MEN,
            description = "Versatile layered long hair perfect worn down or tied into a neat mid-height topknot with clean contours.",
            stylingTips = "Gather loosely into a bun or finger-comb back with flexible grooming wax.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_9,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "43.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Samurai", "Bun", "Versatile", "Clean")
        ),

        // LONG DESIGN 10 (img_long_10.webp)
        HairstyleItem(
            id = "m_long_10",
            name = "Voluminous Long Wavy Cascade",
            category = "Long",
            gender = Gender.MEN,
            description = "Rich, luxuriant long wavy hair with full crown body, dimensional movement, and effortless masculine charm.",
            stylingTips = "Blow-dry upside down for 2 minutes for base volume, then finish with lightweight sea mist.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_long_10,
            overlayAssetId = "overlay_m_curly",
            rating = 5.0f,
            tryOnCount = "47.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Cascade", "Volume", "Wavy", "Trending")
        ),

        // LONG DESIGN 11 (img_long_11.jpg)
        HairstyleItem(
            id = "m_long_11",
            name = "Classic Center-Parted Long Mane",
            category = "Long",
            gender = Gender.MEN,
            description = "Timeless 90s-inspired symmetrical long flow framing the cheekbones neatly with natural fall and easy upkeep.",
            stylingTips = "Comb directly back while damp, let hair find its natural middle split, and allow to air dry.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_11,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.8f,
            tryOnCount = "31.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Long", "Center Part", "90s", "Classic", "Timeless")
        ),

        // LONG DESIGN 12 (img_long_12.webp)
        HairstyleItem(
            id = "m_long_12",
            name = "Urban Long Textured Feathered Cut",
            category = "Long",
            gender = Gender.MEN,
            description = "Contemporary long haircut with fine feathered tips, soft collarbone sweep, and effortless modern swagger.",
            stylingTips = "Rake a small dab of clay pomade through the tips to accentuate feathering.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_long_12,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "36.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long", "Feathered", "Urban", "Textured", "Swagger")
        ),

        // CURLY DESIGN 1 (img_curly_1.jpg)
        HairstyleItem(
            id = "m_curly_01",
            name = "Modern Textured Curly Fade",
            category = "Curly",
            gender = Gender.MEN,
            description = "High contrast low taper fade paired with natural textured curls on top for a sharp, contemporary look.",
            stylingTips = "Apply curl defining cream on damp curls and let air dry or diffuse gently.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_1,
            overlayAssetId = "overlay_m_curly",
            rating = 4.9f,
            tryOnCount = "38.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Fade", "Textured", "Modern", "Low Maintenance")
        ),

        // CURLY DESIGN 2 (img_curly_2.jpg)
        HairstyleItem(
            id = "m_curly_02",
            name = "Classic Curly Drop Fade",
            category = "Curly",
            gender = Gender.MEN,
            description = "Smooth drop fade around the temples and nape leaving a full crown of defined bouncy spirals.",
            stylingTips = "Scrunch leave-in conditioner upward into damp coils to lock in moisture and bounce.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_2,
            overlayAssetId = "overlay_m_fade",
            rating = 4.8f,
            tryOnCount = "29.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Drop Fade", "Defined", "Spirals", "Sharp")
        ),

        // CURLY DESIGN 3 (img_curly_3.jpg)
        HairstyleItem(
            id = "m_curly_03",
            name = "Natural Coiled Curly Taper",
            category = "Curly",
            gender = Gender.MEN,
            description = "Rich natural coil definition with a gentle perimeter taper, accentuating facial contours and cheekbones.",
            stylingTips = "Use curl custard or lightweight mousse to avoid stiffness while enhancing curl pattern.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_curly_3,
            overlayAssetId = "overlay_m_curly",
            rating = 5.0f,
            tryOnCount = "45.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Natural", "Coils", "Taper", "Hydrated")
        ),

        // CURLY DESIGN 4 (img_curly_4.jpg)
        HairstyleItem(
            id = "m_curly_04",
            name = "Curly Fringe Undercut",
            category = "Curly",
            gender = Gender.MEN,
            description = "Clean undercut sides with an artistic forward-falling curly fringe that adds dramatic volume to the forehead.",
            stylingTips = "Comb fringe forward with fingers using sea salt curl spray for matte separation.",
            faceShapes = listOf("Oval", "Oblong", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_4,
            overlayAssetId = "overlay_m_crop",
            rating = 4.9f,
            tryOnCount = "33.6k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Curly", "Fringe", "Undercut", "Bold", "Textured")
        ),

        // CURLY DESIGN 5 (img_curly_5.webp)
        HairstyleItem(
            id = "m_curly_05",
            name = "Defined Ringlet Crown Cut",
            category = "Curly",
            gender = Gender.MEN,
            description = "Sculpted ringlets distributed symmetrically over the crown with neat ear arches and a soft neckline taper.",
            stylingTips = "Finger-coil individual ringlets around the crown and diffuse on low heat.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_5,
            overlayAssetId = "overlay_m_curly",
            rating = 4.8f,
            tryOnCount = "27.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Ringlets", "Crown", "Sculpted", "Youthful")
        ),

        // CURLY DESIGN 6 (img_curly_6.png)
        HairstyleItem(
            id = "m_curly_06",
            name = "Voluminous Textured Curly Shag",
            category = "Curly",
            gender = Gender.MEN,
            description = "Full-bodied curly shag with cascading crown layers and relaxed temple fall for an effortlessly stylish vibe.",
            stylingTips = "Shake out curls upside down and finish with a spritz of flexible-hold curl spray.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_6,
            overlayAssetId = "overlay_m_quiff",
            rating = 5.0f,
            tryOnCount = "52.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Shag", "Volume", "Effortless", "Dimensional")
        ),

        // CURLY DESIGN 7 (img_curly_7.webp)
        HairstyleItem(
            id = "m_curly_07",
            name = "Low Skin Fade Tight Curls",
            category = "Curly",
            gender = Gender.MEN,
            description = "Razor-sharp low skin fade that seamlessly transitions into tight, polished curly clusters on top.",
            stylingTips = "Use a curl sponge in small circular motions to group and define natural coils.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "High",
            imageRes = R.drawable.img_curly_7,
            overlayAssetId = "overlay_m_fade",
            rating = 4.9f,
            tryOnCount = "41.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Skin Fade", "Tight Curls", "Crisp", "Barbershop")
        ),

        // CURLY DESIGN 8 (img_curly_8.webp)
        HairstyleItem(
            id = "m_curly_08",
            name = "Bohemian Long Wave Curls",
            category = "Curly",
            gender = Gender.MEN,
            description = "Shoulder-grazing loose curly waves with effortless movement and relaxed bohemian charisma.",
            stylingTips = "Apply argan curl oil to the ends to seal split strands and preserve natural luster.",
            faceShapes = listOf("Oval", "Square", "Oblong", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_curly_8,
            overlayAssetId = "overlay_m_curly",
            rating = 4.9f,
            tryOnCount = "37.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Curly", "Long Hair", "Bohemian", "Flowing", "Romantic")
        ),

        // CURLY DESIGN 9 (img_curly_9.jpg)
        HairstyleItem(
            id = "m_curly_09",
            name = "Mid Taper Soft Curls",
            category = "Curly",
            gender = Gender.MEN,
            description = "Balanced mid taper that leaves soft, pillowy curls on the crown and fringe, flattering all jawlines.",
            stylingTips = "Towel scrunch with microfiber and apply lightweight styling cream for softness.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_curly_9,
            overlayAssetId = "overlay_m_curly",
            rating = 4.8f,
            tryOnCount = "24.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Curly", "Mid Taper", "Soft", "Versatile", "Gentleman")
        ),

        // CURLY DESIGN 10 (img_curly_10.webp)
        HairstyleItem(
            id = "m_curly_10",
            name = "Urban Edge Curly Crop",
            category = "Curly",
            gender = Gender.MEN,
            description = "Short textured curly crop with a micro blunt hairline and high fade for a street-smart silhouette.",
            stylingTips = "Pinch front curls with light matte clay to keep the forward edge neat.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_curly_10,
            overlayAssetId = "overlay_m_crop",
            rating = 4.9f,
            tryOnCount = "39.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Crop", "Urban", "High Fade", "Clean")
        ),

        // BUZZ CUT HAIRSTYLES (12 DESIGNS)
        // BUZZ DESIGN 1 (img_buzz_01.jpg) - Cropped to focus on front face and hairstyle prominently
        HairstyleItem(
            id = "m_buzz_01",
            name = "Classic Military Precision Buzz Cut",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Ultra-sharp front profile buzz cut cropped close to the scalp with razor-sharp hairline symmetry and defined facial contours.",
            stylingTips = "Apply a light drop of scalp oil or matte balm to keep the buzz healthy, hydrated, and clean.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Rectangle"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_01,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.9f,
            tryOnCount = "45.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Buzz Cut", "Military", "Sharp", "Short", "Front View", "Clean")
        ),

        // BUZZ DESIGN 2 (img_buzz_02.webp)
        HairstyleItem(
            id = "m_buzz_02",
            name = "Clean Skin Fade Induction Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Seamless induction buzz blending smoothly down to skin at the sides and nape, emphasizing chiseled jaw structure.",
            stylingTips = "Exfoliate the scalp weekly and moisturize regularly for a flawless fade line.",
            faceShapes = listOf("Oval", "Square", "Chiseled", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_02,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "38.6k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Skin Fade", "Induction", "Short", "Athletic")
        ),

        // BUZZ DESIGN 3 (img_buzz_03.jpg)
        HairstyleItem(
            id = "m_buzz_03",
            name = "High & Tight Textured Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "High and tight clipper cut leaving subtle texture on top while taking temple areas down to skin.",
            stylingTips = "Zero-maintenance daily style; brush forward with a soft bristle brush.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_03,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.9f,
            tryOnCount = "34.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Buzz Cut", "High & Tight", "Textured", "Short", "Minimal")
        ),

        // BUZZ DESIGN 4 (img_buzz_04.jpg)
        HairstyleItem(
            id = "m_buzz_04",
            name = "Line-Up Sharp Edge Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Uniform length buzz cut paired with a geometric razor line-up across the forehead and temples.",
            stylingTips = "Maintain line-up edges every 10–14 days with an edge trimmer.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_buzz_04,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.7f,
            tryOnCount = "29.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Line-Up", "Geometric", "Edge Up", "Short")
        ),

        // BUZZ DESIGN 5 (img_buzz_05.jpg)
        HairstyleItem(
            id = "m_buzz_05",
            name = "Mid Fade Butch Buzz Cut",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Slightly fuller butch cut on top tapering into a soft mid fade, offering great balance for fuller faces.",
            stylingTips = "Pat dry after shower; use scalp sunscreen when outdoors.",
            faceShapes = listOf("Round", "Oval", "Square", "Rectangle"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_05,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "22.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Mid Fade", "Butch Cut", "Short", "Casual")
        ),

        // BUZZ DESIGN 6 (img_buzz_06.jpg)
        HairstyleItem(
            id = "m_buzz_06",
            name = "Taper Fade Minimalist Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Subtle low taper around the ears and neck preserving a neat, uniform clipper length all across the crown.",
            stylingTips = "A touch of hydrating scalp serum gives natural sheen without greasiness.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_06,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "26.7k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Taper Fade", "Minimalist", "Short", "Clean")
        ),

        // BUZZ DESIGN 7 (img_buzz_07.webp)
        HairstyleItem(
            id = "m_buzz_07",
            name = "Drop Fade Modern Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Modern drop fade that dips gracefully behind the ear, providing an aerodynamic profile to the classic buzz.",
            stylingTips = "Keep the crown groomed with a soft bristle brush once a day.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Oblong"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_buzz_07,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.9f,
            tryOnCount = "31.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Buzz Cut", "Drop Fade", "Modern", "Short", "Profile")
        ),

        // BUZZ DESIGN 8 (img_buzz_08.jpg)
        HairstyleItem(
            id = "m_buzz_08",
            name = "Low Skin Fade Uniform Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Low skin fade keeping the transition subtle near the sideburns with a solid, masculine density on top.",
            stylingTips = "Use a gentle scalp cleanser to maintain skin clarity and comfort.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_08,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.7f,
            tryOnCount = "20.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Low Fade", "Uniform", "Short", "Rugged")
        ),

        // BUZZ DESIGN 9 (img_buzz_09.jpg)
        HairstyleItem(
            id = "m_buzz_09",
            name = "High Fade Chiseled Crew Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Slightly raised front crest blending into a high skin fade, accentuating cheekbones and sharp jawlines.",
            stylingTips = "Use fingertip matte wax on the front millimeter for subtle micro-definition.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Chiseled"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_buzz_09,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.9f,
            tryOnCount = "42.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Buzz Cut", "High Fade", "Chiseled", "Short", "Dynamic")
        ),

        // BUZZ DESIGN 10 (img_buzz_10.webp)
        HairstyleItem(
            id = "m_buzz_10",
            name = "Burr Cut Rounded Fade",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Slightly longer #1 to #2 clipper length giving soft velvet texture with a clean rounded neckline.",
            stylingTips = "Great for active lifestyles; wash and go with zero product needed.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_10,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "24.3k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Burr Cut", "Rounded", "Short", "Velvet")
        ),

        // BUZZ DESIGN 11 (img_buzz_11.jpg)
        HairstyleItem(
            id = "m_buzz_11",
            name = "Sharp Temple Fade Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Precision temple fade (Brooklyn fade) leaving dense buzz length while carving razor-clean temple points.",
            stylingTips = "Brush sides forward and down to keep hairline borders crisp.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_buzz_11,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.9f,
            tryOnCount = "37.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Buzz Cut", "Temple Fade", "Sharp", "Short", "Urban")
        ),

        // BUZZ DESIGN 12 (img_buzz_12.jpg)
        HairstyleItem(
            id = "m_buzz_12",
            name = "Contemporary Fade Brush Buzz",
            category = "Buzz Cut",
            gender = Gender.MEN,
            description = "Crisp contemporary buzz cut with graduated taper fade and natural hairline framing the face comfortably.",
            stylingTips = "Rinse with cool water and moisturize daily for long-lasting freshness.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_buzz_12,
            overlayAssetId = "overlay_m_buzz",
            rating = 4.8f,
            tryOnCount = "28.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Buzz Cut", "Graduated Taper", "Contemporary", "Short", "Classic")
        ),

        // ==========================================
        // QUIFF HAIRSTYLES (11 HD DESIGNS)
        // ==========================================
        // QUIFF DESIGN 01 (img_quiff_01.jpg)
        HairstyleItem(
            id = "m_quiff_01",
            name = "Classic Voluminous Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "A full-bodied, high-volume classic quiff with brushed-up front elevation and neatly tapered side contours.",
            stylingTips = "Blow-dry upward using a round brush, then lock in shape with medium-hold styling pomade.",
            faceShapes = listOf("Oval", "Square", "Round"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_01,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "45.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Voluminous", "Classic", "Modern", "Textured")
        ),

        // QUIFF DESIGN 02 (img_quiff_02.jpg)
        HairstyleItem(
            id = "m_quiff_02",
            name = "Modern Textured Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Contemporary piecey quiff accented with a smooth skin fade and defined top separation.",
            stylingTips = "Apply matte clay through damp hair and finger-style for distinct natural definition.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_02,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.8f,
            tryOnCount = "38.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Textured", "Matte", "Fade", "Casual")
        ),

        // QUIFF DESIGN 03 (img_quiff_03.jpg)
        HairstyleItem(
            id = "m_quiff_03",
            name = "Sleek Tapered Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "A refined gentleman's quiff with low taper and clean diagonal comb lines for a suave finish.",
            stylingTips = "Comb back and slightly sideways with a light styling cream for a natural satin sheen.",
            faceShapes = listOf("Oval", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_03,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "32.4k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Quiff", "Taper", "Clean", "Polished", "Gentleman")
        ),

        // QUIFF DESIGN 04 (img_quiff_04.webp)
        HairstyleItem(
            id = "m_quiff_04",
            name = "High Fade Pompadour Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Dramatic high fade framing high front loft and swept back crown structure.",
            stylingTips = "Blow-dry roots upward on warm setting and finish with flexible-hold hairspray.",
            faceShapes = listOf("Round", "Oval", "Square"),
            hairLength = "Medium",
            maintenance = "High",
            imageRes = R.drawable.img_quiff_04,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "51.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "High Fade", "Pompadour", "Volume", "Sharp")
        ),

        // QUIFF DESIGN 05 (img_quiff_05.jpg)
        HairstyleItem(
            id = "m_quiff_05",
            name = "Casual Flow Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Relaxed everyday quiff with organic strand separation and soft scissor-tapered sides.",
            stylingTips = "Air dry or quick blow-dry with sea salt spray for effortless touchable texture.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Round"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_quiff_05,
            overlayAssetId = "overlay_m_natural_quiff",
            rating = 4.7f,
            tryOnCount = "29.7k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Quiff", "Casual", "Flow", "Natural", "Everyday")
        ),

        // QUIFF DESIGN 06 (img_quiff_06.jpg)
        HairstyleItem(
            id = "m_quiff_06",
            name = "Edgy Undercut Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "High-contrast disconnected undercut with bold top height creating dramatic architectural lines.",
            stylingTips = "Warm heavy-hold paste in palms and lift from front roots upward and back.",
            faceShapes = listOf("Square", "Oval", "Heart"),
            hairLength = "Medium",
            maintenance = "High",
            imageRes = R.drawable.img_quiff_06,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.8f,
            tryOnCount = "43.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Undercut", "Edgy", "High Contrast", "Bold")
        ),

        // QUIFF DESIGN 07 (img_quiff_07.webp)
        HairstyleItem(
            id = "m_quiff_07",
            name = "Low Fade Brushed Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Gentle low drop fade paired with clean brushed-back crest that fits effortlessly in boardroom or weekend.",
            stylingTips = "Brush back while blow-drying on medium heat, seal with grooming cream.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_07,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.8f,
            tryOnCount = "34.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Quiff", "Low Fade", "Brushed", "Neat", "Professional")
        ),

        // QUIFF DESIGN 08 (img_quiff_08.jpg)
        HairstyleItem(
            id = "m_quiff_08",
            name = "Messy Matte Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Youthful and undone quiff with intentionally disheveled layers and low-shine matte finish.",
            stylingTips = "Rub texturizing clay between palms and ruffle through dry hair targeting the fringe.",
            faceShapes = listOf("Oval", "Round", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_quiff_08,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "47.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Messy", "Matte Clay", "Texture", "Trendy")
        ),

        // QUIFF DESIGN 09 (img_quiff_09.jpg)
        HairstyleItem(
            id = "m_quiff_09",
            name = "Executive Sharp Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Crisp, tailored quiff featuring a razor-sharp mid taper and immaculate hairline contouring.",
            stylingTips = "Use a fine-toothed comb to distribute water-based pomade from front to crown.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_09,
            overlayAssetId = "overlay_m_quiff",
            rating = 4.9f,
            tryOnCount = "39.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Executive", "Sharp Fade", "Sophisticated", "Formal")
        ),

        // QUIFF DESIGN 10 (img_quiff_10.jpg)
        HairstyleItem(
            id = "m_quiff_10",
            name = "Short Crop Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Low-profile mini quiff blending short French crop sides with an elevated forward fringe roll.",
            stylingTips = "Quick morning style: towel dry and flick the front fringe up with fingertips.",
            faceShapes = listOf("Square", "Round", "Oval"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_quiff_10,
            overlayAssetId = "overlay_m_crop",
            rating = 4.7f,
            tryOnCount = "26.3k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Quiff", "Crop", "Short", "Low Maintenance", "Youthful")
        ),

        // QUIFF DESIGN 11 (img_quiff_11.jpg)
        HairstyleItem(
            id = "m_quiff_11",
            name = "Wavy Volume Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            description = "Celebrates natural hair wave with textured lift, dynamic flow, and soft perimeter fading.",
            stylingTips = "Enhance wave curl with leave-in conditioner and diffuse dry on low speed.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_quiff_11,
            overlayAssetId = "overlay_m_modern_quiff",
            rating = 4.9f,
            tryOnCount = "41.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Quiff", "Wavy", "Natural Volume", "Layered", "Dynamic")
        ),

        // ==========================================
        // SIDE PART HAIRSTYLES (11 HD DESIGNS)
        // ==========================================
        // SIDE PART DESIGN 01 (img_side_01.jpg)
        HairstyleItem(
            id = "m_side_01",
            name = "Classic Gentleman Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Timeless gentleman's side part with neat parting line, combed symmetry, and subtle scissor-tailored edges.",
            stylingTips = "Find the natural part with a comb while wet, comb flat and finish with light gloss pomade.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_side_01,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "52.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "Classic", "Gentleman", "Professional", "Timeless")
        ),

        // SIDE PART DESIGN 02 (img_side_02.webp)
        HairstyleItem(
            id = "m_side_02",
            name = "Modern Fade Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Crisp mid-skin fade meeting a distinguished side-swept top for a modern urban aesthetic.",
            stylingTips = "Blow-dry toward the parted side and set with matte clay for contemporary zero-grease look.",
            faceShapes = listOf("Oval", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_side_02,
            overlayAssetId = "overlay_m_fade",
            rating = 4.9f,
            tryOnCount = "44.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "Fade", "Sharp", "Contemporary", "Clean")
        ),

        // SIDE PART DESIGN 03 (img_side_03.jpg)
        HairstyleItem(
            id = "m_side_03",
            name = "Textured Hard Part Pompadour",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Razor-shaved hard part line dividing elevated pompadour front from cleanly tapered temples.",
            stylingTips = "Comb fringe slightly back and diagonally away from the razor part line.",
            faceShapes = listOf("Round", "Oval", "Square"),
            hairLength = "Medium",
            maintenance = "High",
            imageRes = R.drawable.img_side_03,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.8f,
            tryOnCount = "36.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Side Part", "Hard Part", "Textured", "Volume", "Sleek")
        ),

        // SIDE PART DESIGN 04 (img_side_04.jpg)
        HairstyleItem(
            id = "m_side_04",
            name = "Slick Low Fade Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "High-shine tailored comb-over featuring a low taper fade that retains density around the ears.",
            stylingTips = "Comb with classic barber pomade directly following a warm shower.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_side_04,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "48.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "Low Fade", "Slick", "Gloss", "Sharp")
        ),

        // SIDE PART DESIGN 05 (img_side_05.webp)
        HairstyleItem(
            id = "m_side_05",
            name = "Natural Flow Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Relaxed, natural side parting with gentle strand curvature and touchable soft-focus texture.",
            stylingTips = "Run a dab of lightweight styling cream through damp hair with fingers.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_side_05,
            overlayAssetId = "overlay_m_natural_quiff",
            rating = 4.7f,
            tryOnCount = "31.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Side Part", "Natural Flow", "Casual", "Soft Part", "Effortless")
        ),

        // SIDE PART DESIGN 06 (img_side_06.jpg)
        HairstyleItem(
            id = "m_side_06",
            name = "Tapered Executive Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Pristine scissor-over-comb taper on the sides with distinguished diagonal volume on top.",
            stylingTips = "Blow-dry on low heat along the comb teeth and set with subtle shine paste.",
            faceShapes = listOf("Oval", "Square", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_side_06,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "40.3k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Side Part", "Taper", "Executive", "Corporate", "Polished")
        ),

        // SIDE PART DESIGN 07 (img_side_07.jpg)
        HairstyleItem(
            id = "m_side_07",
            name = "High Fade Disconnected Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "High drop fade delivering sharp contrast against a voluminous side-parted crown overlay.",
            stylingTips = "Use round brush to lift front roots and sweep hair across the scalp.",
            faceShapes = listOf("Round", "Oval", "Square"),
            hairLength = "Medium",
            maintenance = "High",
            imageRes = R.drawable.img_side_07,
            overlayAssetId = "overlay_m_fade",
            rating = 4.8f,
            tryOnCount = "37.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "High Fade", "Disconnected", "Modern", "Bold")
        ),

        // SIDE PART DESIGN 08 (img_side_08.jpg)
        HairstyleItem(
            id = "m_side_08",
            name = "Brushed Comb-Over Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Smooth brushed comb-over with subtle temple taper and uniform graduation.",
            stylingTips = "Brush horizontally across the crown after parting with a fine styling comb.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_side_08,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.8f,
            tryOnCount = "33.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Side Part", "Comb Over", "Brushed", "Subtle Fade", "Refined")
        ),

        // SIDE PART DESIGN 09 (img_side_09.jpg)
        HairstyleItem(
            id = "m_side_09",
            name = "Vintage Hollywood Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Golden age Hollywood side part with graceful side wave contour and polished mirror finish.",
            stylingTips = "Apply high-shine pomade and sculpt into place with a classic metal tail comb.",
            faceShapes = listOf("Oval", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_side_09,
            overlayAssetId = "overlay_m_pompadour",
            rating = 4.9f,
            tryOnCount = "46.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "Vintage", "Hollywood", "Retro", "Distinguished")
        ),

        // SIDE PART DESIGN 10 (img_side_10.jpg)
        HairstyleItem(
            id = "m_side_10",
            name = "Clean Ivy League Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Preppy Ivy League cut featuring short clippered sides and neat short-parted front crest.",
            stylingTips = "Towel dry and use fingertips with a touch of styling paste for all-day hold.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_side_10,
            overlayAssetId = "overlay_m_crop",
            rating = 4.8f,
            tryOnCount = "29.4k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Side Part", "Ivy League", "Preppy", "Short", "Neat")
        ),

        // SIDE PART DESIGN 11 (img_side_11.webp)
        HairstyleItem(
            id = "m_side_11",
            name = "Soft Wavy Side Part",
            category = "Side Part",
            gender = Gender.MEN,
            description = "Soft layered side part highlighting natural hair curls and relaxed side graduation.",
            stylingTips = "Scrunch in curl mousse while damp and air dry naturally.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_side_11,
            overlayAssetId = "overlay_m_curly",
            rating = 4.9f,
            tryOnCount = "42.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Side Part", "Wavy", "Soft Part", "Layered", "Charming")
        ),

        // ==========================================
        // WOMEN'S SHORT HAIRSTYLES (13 HD DESIGNS)
        // ==========================================

        // WOMEN SHORT DESIGN 01 (img_w_short_01.png)
        HairstyleItem(
            id = "w_short_01",
            name = "Modern Textured Pixie Cut",
            category = "Short",
            gender = Gender.WOMEN,
            description = "A chic, dimensional short pixie cut with soft textured crown volume and delicate sideburn framing.",
            stylingTips = "Warm a small pea-sized amount of texturizing paste and pinch through crown layers for airy definition.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_01,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "39.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Pixie", "Textured", "Chic", "Modern")
        ),

        // WOMEN SHORT DESIGN 02 (img_w_short_02.jpg)
        HairstyleItem(
            id = "w_short_02",
            name = "French Chin-Length Bob with Fringe",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Classic Parisian chin-grazing bob with subtle beveled ends and soft brow-skimming bangs.",
            stylingTips = "Blow-dry with a medium round brush rolling gently under, finishing with a light gloss serum.",
            faceShapes = listOf("Oval", "Heart", "Long", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_02,
            overlayAssetId = "overlay_w_bob",
            rating = 5.0f,
            tryOnCount = "48.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Bob", "French Bob", "Fringe", "Classic")
        ),

        // WOMEN SHORT DESIGN 03 (img_w_short_03.png)
        HairstyleItem(
            id = "w_short_03",
            name = "Soft Tapered Crop with Wispy Bangs",
            category = "Short",
            gender = Gender.WOMEN,
            description = "An ultra-feminine short crop with gracefully tapered nape and wispy face-framing fringe.",
            stylingTips = "Work lightweight mousse into damp roots and rough-dry using low heat for natural movement.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_03,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "33.1k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Short", "Crop", "Wispy", "Tapered", "Feminine")
        ),

        // WOMEN SHORT DESIGN 04 (img_w_short_04.webp)
        HairstyleItem(
            id = "w_short_04",
            name = "Tousled Wavy Short Bob",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Effortlessly undone wavy bob hitting just below the jawline with beachy texture and natural body.",
            stylingTips = "Mist with sea salt spray on towel-dried hair and scrunch gently while diffusing.",
            faceShapes = listOf("Round", "Oval", "Heart", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_04,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "41.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Wavy Bob", "Tousled", "Casual", "Beachy")
        ),

        // WOMEN SHORT DESIGN 05 (img_w_short_05.jpg)
        HairstyleItem(
            id = "w_short_05",
            name = "Sleek Asymmetrical Angled Bob",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Striking A-line angled bob cut shorter in the back and sweeping dramatically forward along the jaw.",
            stylingTips = "Flat iron small sections smoothly following the downward angle, finishing with anti-humidity shine spray.",
            faceShapes = listOf("Round", "Heart", "Oval"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_05,
            overlayAssetId = "overlay_w_bob",
            rating = 4.7f,
            tryOnCount = "29.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Angled Bob", "Asymmetrical", "Sleek", "Bold")
        ),

        // WOMEN SHORT DESIGN 06 (img_w_short_06.png)
        HairstyleItem(
            id = "w_short_06",
            name = "Layered Bixie Cut",
            category = "Short",
            gender = Gender.WOMEN,
            description = "The trending bob-meets-pixie hybrid featuring feathered crown volume and shaggy, soft ear-length layers.",
            stylingTips = "Apply styling cream to damp strands and finger-tousle while blow-drying on medium speed.",
            faceShapes = listOf("Oval", "Diamond", "Heart", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_06,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "36.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Bixie", "Layered", "Trending", "Modern")
        ),

        // WOMEN SHORT DESIGN 07 (img_w_short_07.jpg)
        HairstyleItem(
            id = "w_short_07",
            name = "Feathered Ear-Length Pixie",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Softly sculpted ear-length pixie cut with delicate feathered layers and sweeping side parted bangs.",
            stylingTips = "Blow-dry sideways with a vent brush to create a smooth, swooping side contour.",
            faceShapes = listOf("Oval", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_07,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "27.6k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Feathered", "Ear Length", "Side Part", "Classic")
        ),

        // WOMEN SHORT DESIGN 08 (img_w_short_08.webp)
        HairstyleItem(
            id = "w_short_08",
            name = "Voluminous Curly Short Crop",
            category = "Short",
            gender = Gender.WOMEN,
            description = "High-definition short cut tailored specifically for natural curls with bouncy rounded crown lift.",
            stylingTips = "Rake curl-defining cream through damp coils and air-dry or diffuse on low heat.",
            faceShapes = listOf("Oval", "Long", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_08,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "35.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Curly", "Coils", "Bouncy", "Natural")
        ),

        // WOMEN SHORT DESIGN 09 (img_w_short_09.webp)
        HairstyleItem(
            id = "w_short_09",
            name = "Minimalist Blunt Short Bob",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Razor-sharp blunt micro bob with zero layering for a clean, editorial architectural aesthetic.",
            stylingTips = "Use a thermal heat protectant and straightener to keep ends razor-sharp and pin-straight.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_09,
            overlayAssetId = "overlay_w_bob",
            rating = 4.7f,
            tryOnCount = "31.2k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Blunt Bob", "Minimalist", "Editorial", "Clean")
        ),

        // WOMEN SHORT DESIGN 10 (img_w_short_10.webp)
        HairstyleItem(
            id = "w_short_10",
            name = "Edgy Undercut Pixie with Lift",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Daring cropped undercut with closely buzzed nape and longer, textured side-swept top locks.",
            stylingTips = "Blow-dry top forward and upward using fingers, then define ends with strong matte clay.",
            faceShapes = listOf("Round", "Oval", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_10,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "24.5k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Short", "Undercut", "Edgy", "Pixie", "Statement")
        ),

        // WOMEN SHORT DESIGN 11 (img_w_short_11.png)
        HairstyleItem(
            id = "w_short_11",
            name = "Short Shag with Curtain Bangs",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Playful 70s-inspired short shag featuring razored layers, textured ends, and soft parted curtain bangs.",
            stylingTips = "Mist texture spray and scrunch with fingers while blow-drying bangs backward.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_11,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "44.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Shag", "Curtain Bangs", "Retro", "Razored")
        ),

        // WOMEN SHORT DESIGN 12 (img_w_short_12.jpg)
        HairstyleItem(
            id = "w_short_12",
            name = "Italian Micro Bob with Flip",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Sophisticated lip-length micro bob with soft weight line and subtle playful outward flicked ends.",
            stylingTips = "Use a mini round brush to flick ends slightly outward while blow-drying, then set with light hairspray.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_short_12,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "37.3k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Short", "Micro Bob", "Italian Bob", "Chic", "Retro")
        ),

        // WOMEN SHORT DESIGN 13 (img_w_short_13.jpg)
        HairstyleItem(
            id = "w_short_13",
            name = "Dimensional Sun-Kissed Short Crop",
            category = "Short",
            gender = Gender.WOMEN,
            description = "Natural cropped short haircut with feathered crown movement and warm dimensional contrast.",
            stylingTips = "Emulsify a touch of smoothing balm and run through ends for soft luminous separation.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_short_13,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "40.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Short", "Crop", "Dimensional", "Sun-Kissed", "Textured")
        ),

        // ==========================================
        // WOMEN'S BOB HAIRSTYLES (16 HD DESIGNS)
        // ==========================================

        // BOB DESIGN 01 (img_w_bob_01.jpg)
        HairstyleItem(
            id = "w_bob_01",
            name = "Classic French Chic Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Timeless blunt jawline bob with subtle bevelled ends for an elegant, Parisian silhouette.",
            stylingTips = "Blow-dry with a round ceramic brush bending the ends gently inward for clean symmetry.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_01,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "48.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "French Bob", "Classic", "Chic", "Sleek")
        ),

        // BOB DESIGN 02 (img_w_bob_02.jpg)
        HairstyleItem(
            id = "w_bob_02",
            name = "Textured Wavy Lob (Long Bob)",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Collarbone-grazing modern long bob with airy textured waves and undone effortless finish.",
            stylingTips = "Mist with sea salt texturizing spray and scrunch with fingers while diffusing.",
            faceShapes = listOf("Round", "Square", "Oval", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_02,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "42.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Lob", "Wavy", "Textured", "Effortless")
        ),

        // BOB DESIGN 03 (img_w_bob_03.jpg)
        HairstyleItem(
            id = "w_bob_03",
            name = "Sleek Glass Blunt Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Ultra-straight high-shine razor cut bob sitting crisply at the chin level.",
            stylingTips = "Apply heat protectant serum and flat-iron in clean horizontal sections for high-gloss glass shine.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_03,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "51.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Blunt Bob", "Glass Hair", "Sleek", "Straight")
        ),

        // BOB DESIGN 04 (img_w_bob_04.jpg)
        HairstyleItem(
            id = "w_bob_04",
            name = "Layered Shaggy Bob with Curtain Bangs",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Feathered retro-inspired shag bob featuring soft face-framing curtain bangs and movement.",
            stylingTips = "Blast roots with volumizing mousse and flick outer edges outward using a barrel brush.",
            faceShapes = listOf("Round", "Oval", "Square", "Oblong"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_04,
            overlayAssetId = "overlay_w_bob",
            rating = 4.7f,
            tryOnCount = "36.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Bob", "Shag", "Curtain Bangs", "Layered", "Retro")
        ),

        // BOB DESIGN 05 (img_w_bob_05.jpg)
        HairstyleItem(
            id = "w_bob_05",
            name = "Micro Bob with Baby Bangs",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Bold editorial short cheekbone-length bob paired with precise blunt micro fringe.",
            stylingTips = "Smooth down fringe with lightweight pomade and keep side lengths tucked cleanly behind ears.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "High",
            imageRes = R.drawable.img_w_bob_05,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "29.4k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Bob", "Micro Bob", "Baby Bangs", "Edgy", "Editorial")
        ),

        // BOB DESIGN 06 (img_w_bob_06.jpg)
        HairstyleItem(
            id = "w_bob_06",
            name = "Voluminous 90s Italian Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Chunky, bouncy chin-length Italian bob with thick ends and sumptuous old-money volume.",
            stylingTips = "Use large hot rollers at the crown for lasting gravity-defying roots and curved ends.",
            faceShapes = listOf("Round", "Square", "Oval", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_06,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "54.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Italian Bob", "90s", "Voluminous", "Bouncy")
        ),

        // BOB DESIGN 07 (img_w_bob_07.jpg)
        HairstyleItem(
            id = "w_bob_07",
            name = "Asymmetrical Angled A-Line Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Sleek graduated A-line bob shorter in the nape and cascading forward into angled points.",
            stylingTips = "Straighten forwards following the angle line and apply smoothing serum to prevent flyaways.",
            faceShapes = listOf("Round", "Oval", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_07,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "39.6k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Bob", "A-Line", "Asymmetrical", "Angled", "Sleek")
        ),

        // BOB DESIGN 08 (img_w_bob_08.jpg)
        HairstyleItem(
            id = "w_bob_08",
            name = "Tousled Beach Wave Lob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Sun-drenched shoulder-length bob cut with shattered ends and soft natural bends.",
            stylingTips = "Wrap sections loosely around a 1.25 inch curling wand leaving the bottom half-inch straight.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_08,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "45.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Lob", "Beach Waves", "Tousled", "Casual")
        ),

        // BOB DESIGN 09 (img_w_bob_09.jpg)
        HairstyleItem(
            id = "w_bob_09",
            name = "Graduated Stacked Inverted Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Precision stacked layers at the back providing dramatic lift with sleek curved front panels.",
            stylingTips = "Blow-dry nape area downwards flat while lifting crown layers with a round paddle brush.",
            faceShapes = listOf("Round", "Oval", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_09,
            overlayAssetId = "overlay_w_bob",
            rating = 4.7f,
            tryOnCount = "33.2k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Bob", "Stacked", "Inverted", "Graduated", "Precision")
        ),

        // BOB DESIGN 10 (img_w_bob_10.jpg)
        HairstyleItem(
            id = "w_bob_10",
            name = "Soft Whispy Curly Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Romantic chin-level bob tailored for natural curls and coils with rounded springy volume.",
            stylingTips = "Rake curl-defining cream through damp coils and air-dry or diffuse on low heat.",
            faceShapes = listOf("Oval", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_10,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "37.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Curly", "Natural Texture", "Bouncy", "Romantic")
        ),

        // BOB DESIGN 11 (img_w_bob_11.jpg)
        HairstyleItem(
            id = "w_bob_11",
            name = "Side-Parted Hollywood Glam Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Deep side-parted vintage bob with sculptured glossy S-waves and red-carpet sophistication.",
            stylingTips = "Set waves with styling clips while cooling, then brush out gently with boar bristle brush.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Oblong"),
            hairLength = "Short",
            maintenance = "High",
            imageRes = R.drawable.img_w_bob_11,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "49.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Vintage", "Hollywood Waves", "Glam", "Side Part")
        ),

        // BOB DESIGN 12 (img_w_bob_12.jpg)
        HairstyleItem(
            id = "w_bob_12",
            name = "Feathered French Crop Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Airy lightweight bob with sliced interior layers that frame cheekbones with effortless grace.",
            stylingTips = "Warm matte clay between palms and piece out face-framing strands for light definition.",
            faceShapes = listOf("Oval", "Round", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_12,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "38.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Bob", "French", "Feathered", "Lightweight", "Airy")
        ),

        // BOB DESIGN 13 (img_w_bob_13.jpg)
        HairstyleItem(
            id = "w_bob_13",
            name = "Modern Grunge Choppy Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Deconstructed jagged bob with razored ends for a stylish 90s indie-rock aesthetic.",
            stylingTips = "Spray dry shampoo at roots and scrunch flexible matte paste through mid-lengths.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_13,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "34.6k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Bob", "Choppy", "Grunge", "Edgy", "Razored")
        ),

        // BOB DESIGN 14 (img_w_bob_14.jpg)
        HairstyleItem(
            id = "w_bob_14",
            name = "Platinum Minimalist Box Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Crisp architectural blunt box bob cut straight across without graduation for a striking modern profile.",
            stylingTips = "Iron bone-straight and finish with anti-humidity shine mist for clean minimalist perfection.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_14,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "44.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Box Bob", "Architectural", "Blunt", "Minimalist")
        ),

        // BOB DESIGN 15 (img_w_bob_15.jpg)
        HairstyleItem(
            id = "w_bob_15",
            name = "Sun-Kissed Dimensional Balayage Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "Multi-tonal rounded bob with luminous highlights that catch the light with every turn.",
            stylingTips = "Style with soft bend at cheek level using an oval hot-air styler for maximum dimension.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_bob_15,
            overlayAssetId = "overlay_w_bob",
            rating = 4.9f,
            tryOnCount = "47.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Balayage", "Dimensional", "Highlighted", "Chic")
        ),

        // BOB DESIGN 16 (img_w_bob_16.jpg)
        HairstyleItem(
            id = "w_bob_16",
            name = "Textured Pixie Bob (Bixie)",
            category = "Bob",
            gender = Gender.WOMEN,
            description = "The ultimate hybrid cut blending the fullness of a bob with the tapered nape of a pixie.",
            stylingTips = "Tuck sides behind ears and spike crown texture lightly using styling paste.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_bob_16,
            overlayAssetId = "overlay_w_bob",
            rating = 4.8f,
            tryOnCount = "41.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Bob", "Bixie", "Pixie Bob", "Hybrid", "Textured")
        ),
        // Women's Medium Hairstyles (HD Collection)
        HairstyleItem(
            id = "w_med_01",
            name = "Effortless Collarbone Shag",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "A relaxed, chic medium-length cut grazing the collarbone with soft blended layers and face-framing texture.",
            stylingTips = "Rough dry with a diffuser and scrunch in sea-salt spray to enhance natural wave dynamics.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_01,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "43.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Shag", "Collarbone", "Layers", "Casual")
        ),
        HairstyleItem(
            id = "w_med_02",
            name = "Silky Glass Midi Cut",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Ultra-sleek shoulder-length cut with razor-sharp blunt ends and radiant high-shine mirror gloss.",
            stylingTips = "Apply heat protection serum, pass a flat iron downwards, and seal with a lightweight shine mist.",
            faceShapes = listOf("Oval", "Round", "Heart", "Oblong"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_02,
            overlayAssetId = "overlay_w_medium",
            rating = 4.8f,
            tryOnCount = "38.6k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Medium", "Sleek", "Glass Hair", "Straight", "Glossy")
        ),
        HairstyleItem(
            id = "w_med_03",
            name = "Soft Curtain Bangs & Mid Layers",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Timeless mid-length style featuring wispy curtain bangs swept outwards and airy cascading volume.",
            stylingTips = "Blow dry bangs using a round thermal brush directed away from the face for an open, airy lift.",
            faceShapes = listOf("Round", "Oval", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_03,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "51.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Curtain Bangs", "Layers", "Fringe", "Volume")
        ),
        HairstyleItem(
            id = "w_med_04",
            name = "Voluminous 90s Midi Blowout",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Full-bodied shoulder-length blowout reminiscent of classic supermodel glam with rounded ends.",
            stylingTips = "Set warm crown sections on large velcro rollers for 15 minutes before brushing out gently.",
            faceShapes = listOf("Oval", "Heart", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_04,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "47.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Blowout", "90s", "Glamour", "Full Body")
        ),
        HairstyleItem(
            id = "w_med_05",
            name = "Textured Beach Wave Lob",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Sun-kissed medium cut styled with loose tousled S-waves and lived-in undone root volume.",
            stylingTips = "Alternate wave directions with a 1.25-inch curling wand leaving the last inch of hair straight.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_med_05,
            overlayAssetId = "overlay_w_medium",
            rating = 4.8f,
            tryOnCount = "39.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Medium", "Beach Waves", "Textured", "Lob", "Balayage")
        ),
        HairstyleItem(
            id = "w_med_06",
            name = "Polished Butterfly Midi Cut",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Multi-tiered feathered cut designed to offer dramatic movement and weightless crown lift.",
            stylingTips = "Layer volumizing mousse into damp roots and blow dry with head inverted for maximum crown body.",
            faceShapes = listOf("Round", "Oval", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_06,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "54.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Butterfly Cut", "Feathered", "Layers", "Movement")
        ),
        HairstyleItem(
            id = "w_med_07",
            name = "Modern French Shoulder Crop",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Parisian elegance with shoulder-skimming length, piecey blunt fringe, and natural airy drape.",
            stylingTips = "Allow hair to air-dry with a touch of leave-in conditioning cream; finger-style the fringe.",
            faceShapes = listOf("Oval", "Oblong", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_med_07,
            overlayAssetId = "overlay_w_medium",
            rating = 4.7f,
            tryOnCount = "32.4k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Medium", "French", "Fringe", "Airy", "Chic")
        ),
        HairstyleItem(
            id = "w_med_08",
            name = "Dimensional Curly Midi Shag",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Defined bouncy medium curls sculpted with graduated internal layers for round, healthy contour.",
            stylingTips = "Apply curl defining custard to soaking wet strands, scrunch gently, and diffuse on low heat.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_08,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "42.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Curly", "Bouncy", "Curls", "Hydrated")
        ),
        HairstyleItem(
            id = "w_med_09",
            name = "Sleek Side-Part Midi Swag",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Sophisticated side-swept mid-length cut featuring tucked ear styling and bevelled glossy ends.",
            stylingTips = "Create a crisp deep side parting and smooth flyaways using an edge control wand or pomade.",
            faceShapes = listOf("Round", "Heart", "Oval", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_med_09,
            overlayAssetId = "overlay_w_medium",
            rating = 4.8f,
            tryOnCount = "35.7k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Medium", "Side Part", "Sleek", "Office", "Elegant")
        ),
        HairstyleItem(
            id = "w_med_10",
            name = "Sun-Kissed Golden Layered Midi",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Radiant warm golden medium cut enriched with face-framing balayage ribbons and soft flow.",
            stylingTips = "Finish with argan oil drops on the ends for reflective gleam and frizz suppression in any weather.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_10,
            overlayAssetId = "overlay_w_medium",
            rating = 4.8f,
            tryOnCount = "37.9k",
            isTrending = true,
            isPopular = false,
            tags = listOf("Medium", "Golden", "Balayage", "Layers", "Warm")
        ),
        HairstyleItem(
            id = "w_med_11",
            name = "Choppy Textured Midi Wolf Cut",
            category = "Medium",
            gender = Gender.WOMEN,
            description = "Bold, rebellious wolf cut adapted to medium length with heavy crown volume and tapered ends.",
            stylingTips = "Work matte texturizing clay through the crown layers and pull strands outward for defined separation.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_med_11,
            overlayAssetId = "overlay_w_medium",
            rating = 4.9f,
            tryOnCount = "49.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Medium", "Wolf Cut", "Choppy", "Edgy", "Modern")
        ),
        // WOMEN'S LONG HAIRSTYLES SERIES (USER UPLOADED - 13 HD LONG DESIGNS)
        // IMAGE 1 (LONG 01): Cascading Hollywood Waves
        HairstyleItem(
            id = "w_long_01",
            name = "Cascading Hollywood Waves",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Luxurious, flowing deep waves cascading over shoulders with ultra-glossy shine and timeless red-carpet elegance. Uploaded Women Long Image 1.",
            stylingTips = "Wrap large sections around a 1.5-inch curling iron in the same direction, brush gently with a boar bristle brush, and finish with gloss serum.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_01,
            overlayAssetId = "overlay_w_long",
            rating = 5.0f,
            tryOnCount = "58.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Hollywood Waves", "Glamorous", "Glossy")
        ),
        // IMAGE 2 (LONG 02): Sleek Ultra-Straight Glass Hair
        HairstyleItem(
            id = "w_long_02",
            name = "Sleek Ultra-Straight Glass Hair",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Pin-straight, mirror-like polished long strands with razor-sharp ends and a clean center part for a sharp contemporary look. Uploaded Women Long Image 2.",
            stylingTips = "Apply thermal heat protectant spray and glide a ceramic flat iron from roots to ends, followed by anti-frizz polishing oil.",
            faceShapes = listOf("Oval", "Heart", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_long_02,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "52.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Glass Hair", "Sleek", "Minimalist")
        ),
        // IMAGE 3 (LONG 03): 90s Butterfly Layered Blowout
        HairstyleItem(
            id = "w_long_03",
            name = "90s Butterfly Layered Blowout",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Bouncy, multi-tiered cascading butterfly layers framing the face with immense crown lift and airy outward flips. Uploaded Women Long Image 3.",
            stylingTips = "Blow dry with a round ceramic barrel brush lifting upward at roots, then set in Velcro rollers for 15 minutes.",
            faceShapes = listOf("Oval", "Square", "Rectangle", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_03,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "64.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Butterfly Layers", "90s Blowout", "Volume")
        ),
        // IMAGE 4 (LONG 04): Boho Beachy Mermaid Waves
        HairstyleItem(
            id = "w_long_04",
            name = "Boho Beachy Mermaid Waves",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Relaxed, undone textured mermaid waves with natural flow, dimensional highlights, and effortless breezy movement. Uploaded Women Long Image 4.",
            stylingTips = "Mist damp hair with sea salt spray and air-dry, or use a triple-barrel waver on alternating mid-lengths.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_long_04,
            overlayAssetId = "overlay_w_long",
            rating = 4.8f,
            tryOnCount = "47.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Mermaid Waves", "Boho", "Beach Waves")
        ),
        // IMAGE 5 (LONG 05): Curtain Bangs Long Shag
        HairstyleItem(
            id = "w_long_05",
            name = "Curtain Bangs Long Shag",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Feathered cheekbone-framing curtain bangs blending seamlessly into heavily textured long perimeter layers. Uploaded Women Long Image 5.",
            stylingTips = "Blow-dry bangs forward with a medium round brush, then roll backwards away from the face to create soft wings.",
            faceShapes = listOf("Square", "Oval", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_05,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "55.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Curtain Bangs", "Long Shag", "Face Framing")
        ),
        // IMAGE 6 (LONG 06): Voluminous V-Cut Flowing Layers
        HairstyleItem(
            id = "w_long_06",
            name = "Voluminous V-Cut Flowing Layers",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Sculpted V-shape back taper with cascading tiers that reduce bulk while preserving full length and maximum swing. Uploaded Women Long Image 6.",
            stylingTips = "Apply lightweight volumizing mousse to damp roots and diffuse or round-brush mid-shaft downward.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_06,
            overlayAssetId = "overlay_w_long",
            rating = 4.8f,
            tryOnCount = "43.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "V-Cut", "Flowing Layers", "Tapered")
        ),
        // IMAGE 7 (LONG 07): Wispy Fringe Long Romantic Waves
        HairstyleItem(
            id = "w_long_07",
            name = "Wispy Fringe Long Romantic Waves",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Delicate airy wispy bangs paired with soft romantic waves for a dreamy, youthful, and flattering silhouette. Uploaded Women Long Image 7.",
            stylingTips = "Lightly flat-iron the wispy fringe straight down and add light sea-mist wave texture to lengths.",
            faceShapes = listOf("Oval", "Heart", "Long/Oblong"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_07,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "51.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Wispy Bangs", "Romantic", "Soft Waves")
        ),
        // IMAGE 8 (LONG 08): Sun-Drenched Honey Balayage Curls
        HairstyleItem(
            id = "w_long_08",
            name = "Sun-Drenched Honey Balayage Curls",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Warm golden balayage ribbons accentuating deep, defined spiral curls that bounce with vitality and luster. Uploaded Women Long Image 8.",
            stylingTips = "Scrunch curl-defining cream into wet strands and diffuse on medium heat until 80% dry.",
            faceShapes = listOf("Oval", "Round", "Square"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_08,
            overlayAssetId = "overlay_w_long",
            rating = 5.0f,
            tryOnCount = "61.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Balayage", "Honey Blonde", "Defined Curls")
        ),
        // IMAGE 9 (LONG 09): Chic Side-Swept Cascading Locks
        HairstyleItem(
            id = "w_long_09",
            name = "Chic Side-Swept Cascading Locks",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Deep dramatic side-part with luxuriant waves swept over one shoulder, highlighting jawline contours and elegance. Uploaded Women Long Image 9.",
            stylingTips = "Create an extreme side part, pin the back underside securely with bobby pins, and mist strong-hold hairspray.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_long_09,
            overlayAssetId = "overlay_w_long",
            rating = 4.8f,
            tryOnCount = "39.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Side-Swept", "Evening Glam", "Asymmetric")
        ),
        // IMAGE 10 (LONG 10): Modern Long Wolf Cut with Micro-Bangs
        HairstyleItem(
            id = "w_long_10",
            name = "Modern Long Wolf Cut with Micro-Bangs",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Edgy fashion-forward long wolf silhouette featuring exaggerated crown choppy layers and feathered lengths. Uploaded Women Long Image 10.",
            stylingTips = "Emulsify a pea-sized drop of styling paste and piece out crown spikes and tapered shoulder ends.",
            faceShapes = listOf("Oval", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_10,
            overlayAssetId = "overlay_w_long",
            rating = 4.8f,
            tryOnCount = "42.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Wolf Cut", "Modern", "Edgy")
        ),
        // IMAGE 11 (LONG 11): Caramel Swirl Face-Framing Blowout
        HairstyleItem(
            id = "w_long_11",
            name = "Caramel Swirl Face-Framing Blowout",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Silky salon-finish blowout with warm caramel highlights curving inwards gently around collarbones and chest. Uploaded Women Long Image 11.",
            stylingTips = "Direct dryer air down the hair shaft with a round boar-bristle brush, curling ends inwards towards chin.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_11,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "53.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Caramel", "Face Framing", "Blowout")
        ),
        // IMAGE 12 (LONG 12): Ethereal Crown Braid Long Half-Up
        HairstyleItem(
            id = "w_long_12",
            name = "Ethereal Crown Braid Long Half-Up",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Intricate halo crown Dutch braid meeting at the back into flowing cascading waves for festival or bridal elegance. Uploaded Women Long Image 12.",
            stylingTips = "French or Dutch braid two side sections towards the back, pin securely with clear elastics, and gently pancake the braids.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_long_12,
            overlayAssetId = "overlay_w_long",
            rating = 5.0f,
            tryOnCount = "66.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Half-Up", "Braided", "Bridal", "Boho")
        ),
        // IMAGE 13 (LONG 13): Glossy Jet Black Feathered Lengths
        HairstyleItem(
            id = "w_long_13",
            name = "Glossy Jet Black Feathered Lengths",
            category = "Long Hair",
            gender = Gender.WOMEN,
            description = "Deep obsidian gloss locks featuring delicate feathered tips that create fluid movement and intense dimension. Uploaded Women Long Image 13.",
            stylingTips = "Finish with argan oil drops distributed evenly through the lengths to boost mirror gloss and prevent flyaways.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_long_13,
            overlayAssetId = "overlay_w_long",
            rating = 4.9f,
            tryOnCount = "48.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Long Hair", "Long", "Jet Black", "Glossy", "Feathered")
        ),

        // ==========================================
        // WOMEN'S CURLY HAIRSTYLES (USER UPLOADED - 12 HD CURLY DESIGNS)
        // ==========================================

        // CURLY DESIGN 01 (img_w_curly_01.jpg): Voluminous Brunette Spiral Curls
        HairstyleItem(
            id = "w_curly_01",
            name = "Voluminous Brunette Spiral Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Rich, full-bodied spiral curls cascading with defined ringlets and natural bounce. Uploaded Women Curly Image 1.",
            stylingTips = "Apply curl-defining cream on soaking wet hair and diffuse upside down on low heat for maximum crown lift.",
            faceShapes = listOf("Oval", "Heart", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_01,
            overlayAssetId = "overlay_w_curly",
            rating = 5.0f,
            tryOnCount = "58.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Spiral Curls", "Voluminous", "Natural", "Trending")
        ),

        // CURLY DESIGN 02 (img_w_curly_02.jpg): Soft Romantic Ringlets
        HairstyleItem(
            id = "w_curly_02",
            name = "Soft Romantic Ringlets",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Delicate, touchable ringlet curls framing the cheeks with gentle texture and luminous shine. Uploaded Women Curly Image 2.",
            stylingTips = "Twist damp curl clumps around fingers with leave-in conditioner and let air dry naturally.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_02,
            overlayAssetId = "overlay_w_curly",
            rating = 4.9f,
            tryOnCount = "47.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Ringlets", "Romantic", "Soft Curls", "Popular")
        ),

        // CURLY DESIGN 03 (img_w_curly_03.jpg): Bouncy Defined Curls with Bangs
        HairstyleItem(
            id = "w_curly_03",
            name = "Bouncy Defined Curls with Bangs",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Playful springy curls paired with soft curly bangs that accentuate the eyes and cheekbones. Uploaded Women Curly Image 3.",
            stylingTips = "Style bangs with a lightweight mousse and scrunch gently without disturbing the curl formation.",
            faceShapes = listOf("Oval", "Long", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_03,
            overlayAssetId = "overlay_w_curly",
            rating = 5.0f,
            tryOnCount = "64.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Curly Bangs", "Defined Curls", "Bouncy", "Trending")
        ),

        // CURLY DESIGN 04 (img_w_curly_04.jpg): Caramel Honey Curly Shag
        HairstyleItem(
            id = "w_curly_04",
            name = "Caramel Honey Curly Shag",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Modern layered curly shag haircut highlighting multidimensional caramel and honey ribbon tones. Uploaded Women Curly Image 4.",
            stylingTips = "Scrunch with sea salt curl spray and fluff the crown roots using an afro pick once dry.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Round"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_04,
            overlayAssetId = "overlay_w_curly",
            rating = 4.8f,
            tryOnCount = "51.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Shag", "Caramel", "Highlights", "Volume")
        ),

        // CURLY DESIGN 05 (img_w_curly_05.jpg): Cascading Golden Curly Lob
        HairstyleItem(
            id = "w_curly_05",
            name = "Cascading Golden Curly Lob",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Collarbone-grazing golden blonde curly long bob with soft bounce and effortless volume. Uploaded Women Curly Image 5.",
            stylingTips = "Use a microfiber towel to squeeze out excess moisture and apply curl-glaze gel to seal in hydration.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_curly_05,
            overlayAssetId = "overlay_w_curly",
            rating = 4.9f,
            tryOnCount = "53.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Lob", "Golden Blonde", "Bouncy", "Popular")
        ),

        // CURLY DESIGN 06 (img_w_curly_06.jpg): Classic Tight Corkscrew Curls
        HairstyleItem(
            id = "w_curly_06",
            name = "Classic Tight Corkscrew Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "High-definition corkscrew curls packed with dense spring and healthy moisture-rich shine. Uploaded Women Curly Image 6.",
            stylingTips = "Deep condition weekly and seal ends with lightweight argan oil to prevent frizz and hold shape.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_06,
            overlayAssetId = "overlay_w_curly",
            rating = 5.0f,
            tryOnCount = "68.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Corkscrew", "Tight Curls", "Hydrated", "Volume")
        ),

        // CURLY DESIGN 07 (img_w_curly_07.jpg): Side-Parted Sun-Kissed Curls
        HairstyleItem(
            id = "w_curly_07",
            name = "Side-Parted Sun-Kissed Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Deep side parting sweeps lush, sun-kissed curls over the shoulder for an asymmetrical, glamorous frame. Uploaded Women Curly Image 7.",
            stylingTips = "Part while wet, set with flexible hold curl spray, and clip the root for extra height during drying.",
            faceShapes = listOf("Round", "Oval", "Heart", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_07,
            overlayAssetId = "overlay_w_curly",
            rating = 4.9f,
            tryOnCount = "45.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Side Part", "Sun Kissed", "Natural Curls", "Glamour")
        ),

        // CURLY DESIGN 08 (img_w_curly_08.jpg): Tousled Curly Pixie Bob
        HairstyleItem(
            id = "w_curly_08",
            name = "Tousled Curly Pixie Bob",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Chic short curly crop combining pixie lightness with bobs curvature and bouncy crown curls. Uploaded Women Curly Image 8.",
            stylingTips = "Work lightweight curl foam through damp strands and gently scrunch with fingertips.",
            faceShapes = listOf("Oval", "Diamond", "Heart", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_curly_08,
            overlayAssetId = "overlay_w_curly",
            rating = 4.8f,
            tryOnCount = "42.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Pixie Bob", "Short Curly", "Textured", "Trending")
        ),

        // CURLY DESIGN 09 (img_w_curly_09.jpg): High-Volume Layered Curls
        HairstyleItem(
            id = "w_curly_09",
            name = "High-Volume Layered Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Cascading long layered curls designed to unleash maximum texture, fullness, and dynamic movement. Uploaded Women Curly Image 9.",
            stylingTips = "Flip hair forward, diffuse with warm air, and gently separate curls with oiled hands.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_09,
            overlayAssetId = "overlay_w_curly",
            rating = 5.0f,
            tryOnCount = "71.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Layers", "High Volume", "Glamour", "Popular")
        ),

        // CURLY DESIGN 10 (img_w_curly_10.jpg): Brunette Botticelli Curls
        HairstyleItem(
            id = "w_curly_10",
            name = "Brunette Botticelli Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Renaissance-inspired free-flowing ripples and curls with organic texture and earthy brunette tones. Uploaded Women Curly Image 10.",
            stylingTips = "Air-dry with a curl activator serum for glossy, soft-to-the-touch definition without crunch.",
            faceShapes = listOf("Oval", "Diamond", "Heart", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_curly_10,
            overlayAssetId = "overlay_w_curly",
            rating = 4.9f,
            tryOnCount = "50.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Botticelli", "Brunette", "Romantic Curls", "Trending")
        ),

        // CURLY DESIGN 11 (img_w_curly_11.jpg): Coily Textured Curly Afro
        HairstyleItem(
            id = "w_curly_11",
            name = "Coily Textured Curly Afro",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Stunning rounded afro silhouette celebrating tight, hydrated curls and glorious natural volume. Uploaded Women Curly Image 11.",
            stylingTips = "Layer leave-in conditioner with shea butter and pick gently from the roots outward for a rounded crown.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_curly_11,
            overlayAssetId = "overlay_w_curly",
            rating = 5.0f,
            tryOnCount = "63.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Coily", "Afro", "Texture", "Natural", "Popular")
        ),

        // CURLY DESIGN 12 (img_w_curly_12.png): Effortless Wavy-Curly Shoulder Flow
        HairstyleItem(
            id = "w_curly_12",
            name = "Effortless Wavy-Curly Shoulder Flow",
            category = "Curly",
            gender = Gender.WOMEN,
            description = "Relaxed shoulder-grazing wavy curls with effortless movement, crown fullness, and casual elegance. Uploaded Women Curly Image 12.",
            stylingTips = "Scrunch with a light texture spray and let air-dry for effortless, frizz-free everyday waves.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_curly_12,
            overlayAssetId = "overlay_w_curly",
            rating = 4.9f,
            tryOnCount = "56.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Curly", "Curls", "Wavy", "Shoulder Length", "Effortless", "Trending")
        ),

        // ==========================================
        // WOMEN'S LAYERS HAIRSTYLES (USER UPLOADED - 14 HD LAYERED DESIGNS)
        // ==========================================

        // LAYERS DESIGN 01 (img_w_layers_01.jpg): Butterfly Layered Blowout
        HairstyleItem(
            id = "w_layers_01",
            name = "Butterfly Layered Blowout",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Dramatic butterfly layers featuring feather-cut face-framing pieces and a voluminous salon blowout finish. Uploaded Women Layers Image 1.",
            stylingTips = "Blow dry using a large round ceramic brush curling outward, then set in velcro rollers for lasting lift.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_01,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "68.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Butterfly", "Blowout", "Volume", "Trending")
        ),

        // LAYERS DESIGN 02 (img_w_layers_02.jpg): Soft Feathered Face-Framing Layers
        HairstyleItem(
            id = "w_layers_02",
            name = "Soft Feathered Face-Framing Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Delicate feathered cascade highlighting jawline and cheek contours with seamless layered gradation. Uploaded Women Layers Image 2.",
            stylingTips = "Smooth with a lightweight thermal serum and flick ends backward with a flat iron.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Round"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_02,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "54.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Feathered", "Face-Framing", "Soft", "Popular")
        ),

        // LAYERS DESIGN 03 (img_w_layers_03.jpg): Mid-Length Shag Layers
        HairstyleItem(
            id = "w_layers_03",
            name = "Mid-Length Shag Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Effortless rocker-chic shag packed with choppy internal texture and wispy perimeter layers. Uploaded Women Layers Image 3.",
            stylingTips = "Mist with sea salt texturizing spray and scrunch with fingers for lived-in movement.",
            faceShapes = listOf("Oval", "Round", "Square", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_03,
            overlayAssetId = "overlay_w_layers",
            rating = 4.8f,
            tryOnCount = "46.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Shag", "Mid-Length", "Texture", "Effortless")
        ),

        // LAYERS DESIGN 04 (img_w_layers_04.jpg): Cascading V-Cut Layers
        HairstyleItem(
            id = "w_layers_04",
            name = "Cascading V-Cut Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Sculpted V-shaped layered back silhouette creating graceful, flowing length and weightless movement. Uploaded Women Layers Image 4.",
            stylingTips = "Apply smoothing lotion from mid-shaft to ends and brush gently to display the tapered silhouette.",
            faceShapes = listOf("Oval", "Heart", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_04,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "59.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "V-Cut", "Cascading", "Flowing", "Popular")
        ),

        // LAYERS DESIGN 05 (img_w_layers_05.jpg): Voluminous Curtain Bang Layers
        HairstyleItem(
            id = "w_layers_05",
            name = "Voluminous Curtain Bang Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Lush sweeping layers complemented by 70s-inspired parted curtain bangs that open up the face. Uploaded Women Layers Image 5.",
            stylingTips = "Direct curtain bangs back and up with a round brush for that iconic swooping cheekbone wing.",
            faceShapes = listOf("Oval", "Square", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_05,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "73.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Curtain Bangs", "Voluminous", "70s Flow", "Trending")
        ),

        // LAYERS DESIGN 06 (img_w_layers_06.jpg): Choppy Textured Modern Layers
        HairstyleItem(
            id = "w_layers_06",
            name = "Choppy Textured Modern Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Contemporary razor-accentuated layers creating dimensional piecey definition and modern edge. Uploaded Women Layers Image 6.",
            stylingTips = "Warm dry texturizing paste between fingers and pinch the ends to emphasize separate layers.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Round"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_06,
            overlayAssetId = "overlay_w_layers",
            rating = 4.8f,
            tryOnCount = "48.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Choppy", "Textured", "Modern", "Edgy")
        ),

        // LAYERS DESIGN 07 (img_w_layers_07.jpg): Wispy Seamless Layered Flow
        HairstyleItem(
            id = "w_layers_07",
            name = "Wispy Seamless Layered Flow",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Ultra-blended wispy layers that maintain thick-looking density while removing heavy bulk. Uploaded Women Layers Image 7.",
            stylingTips = "Lightly mist with shine gloss spray for a fluid, reflective mirror-like finish.",
            faceShapes = listOf("Round", "Oval", "Heart", "Diamond"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_07,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "52.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Wispy", "Seamless", "Fluid Flow", "Popular")
        ),

        // LAYERS DESIGN 08 (img_w_layers_08.jpg): Dimensional Balayage Long Layers
        HairstyleItem(
            id = "w_layers_08",
            name = "Dimensional Balayage Long Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Deep long layers designed specifically to accentuate multi-tonal balayage and sunlit highlights. Uploaded Women Layers Image 8.",
            stylingTips = "Wrap sections around a 1.25-inch curling iron and brush out with a wide-tooth comb.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_08,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "76.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Balayage", "Long", "Dimensional", "Trending")
        ),

        // LAYERS DESIGN 09 (img_w_layers_09.jpg): Sun-Kissed Tousled Layers
        HairstyleItem(
            id = "w_layers_09",
            name = "Sun-Kissed Tousled Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Relaxed beachy layers with airy separation and carefree golden lightness. Uploaded Women Layers Image 9.",
            stylingTips = "Braid loosely before bed or air dry with mousse for an effortless undone textured vibe.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_09,
            overlayAssetId = "overlay_w_layers",
            rating = 4.8f,
            tryOnCount = "44.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Tousled", "Sun-Kissed", "Relaxed", "Beach Wave")
        ),

        // LAYERS DESIGN 10 (img_w_layers_10.jpg): Feathered Shoulder-Grazing Lob Layers
        HairstyleItem(
            id = "w_layers_10",
            name = "Feathered Shoulder-Grazing Lob Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Sophisticated collarbone-skimming lob accented with tapered feathered ends that flatter any neckline. Uploaded Women Layers Image 10.",
            stylingTips = "Tuck one side behind ear and bevel ends inwards slightly with a paddle brush.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_10,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "51.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Lob", "Shoulder-Grazing", "Feathered", "Popular")
        ),

        // LAYERS DESIGN 11 (img_w_layers_11.jpg): Effortless Wavy Butterfly Layers
        HairstyleItem(
            id = "w_layers_11",
            name = "Effortless Wavy Butterfly Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Cascading two-tier butterfly cuts with effortless wave flow, maximum bounce, and crown volume. Uploaded Women Layers Image 11.",
            stylingTips = "Diffuse softly on medium heat with curl-boosting mousse for airy weightlessness.",
            faceShapes = listOf("Oval", "Heart", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_11,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "67.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Butterfly Layers", "Wavy", "Crown Volume", "Trending")
        ),

        // LAYERS DESIGN 12 (img_w_layers_12.webp): Sleek Polished Razor Cut Layers
        HairstyleItem(
            id = "w_layers_12",
            name = "Sleek Polished Razor Cut Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Glass-hair straight layers precision-cut with razor tapering for a sharp, ultra-clean modern drape. Uploaded Women Layers Image 12.",
            stylingTips = "Flat iron in thin horizontal sections and seal with heat protectant gloss oil.",
            faceShapes = listOf("Oval", "Diamond", "Round", "Square"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_12,
            overlayAssetId = "overlay_w_layers",
            rating = 4.9f,
            tryOnCount = "57.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Razor Cut", "Sleek", "Polished", "Glass Hair")
        ),

        // LAYERS DESIGN 13 (img_w_layers_13.jpg): Subtle Graduated Crown Layers
        HairstyleItem(
            id = "w_layers_13",
            name = "Subtle Graduated Crown Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Subtle internal layering focused on the crown to prevent flat roots while keeping lengths intact. Uploaded Women Layers Image 13.",
            stylingTips = "Dust volumizing powder at roots and massage gently with fingertips for instant lift.",
            faceShapes = listOf("Oval", "Round", "Heart", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_layers_13,
            overlayAssetId = "overlay_w_layers",
            rating = 4.8f,
            tryOnCount = "43.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "Crown Lift", "Volume", "Natural", "Popular")
        ),

        // LAYERS DESIGN 14 (img_w_layers_14.jpg): Classic 90s Bombshell Layers
        HairstyleItem(
            id = "w_layers_14",
            name = "Classic 90s Bombshell Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            description = "Iconic 90s supermodel layers boasting voluminous sweeping layers, dramatic body, and timeless glamour. Uploaded Women Layers Image 14.",
            stylingTips = "Flip head over, blast with cool air after drying, and spritz flexible-hold finishing spray.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_layers_14,
            overlayAssetId = "overlay_w_layers",
            rating = 5.0f,
            tryOnCount = "82.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Layers", "Layered", "90s Bombshell", "Supermodel", "Glamour", "Trending")
        ),

        // ==========================================
        // WOMEN'S PIXIE HAIRSTYLES (USER UPLOADED - 15 HD PIXIE DESIGNS)
        // ==========================================

        // PIXIE DESIGN 01 (img_w_pixie_01.jpg): Classic Whispy Pixie Cut
        HairstyleItem(
            id = "w_pixie_01",
            name = "Classic Whispy Pixie Cut",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Delicate feathered wisps contouring the brow and temples with a neat, tapered nape. Uploaded Women Pixie Image 1.",
            stylingTips = "Rub a pea-sized amount of matte paste between fingers and lightly piece out the crown and fringe.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_01,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "52.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Whispy", "Feathered", "Classic", "Trending")
        ),

        // PIXIE DESIGN 02 (img_w_pixie_02.jpg): Edgy Choppy Textured Pixie
        HairstyleItem(
            id = "w_pixie_02",
            name = "Edgy Choppy Textured Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Spunky multidimensional choppy layers offering high-impact texture, modern grit, and effortless volume. Uploaded Women Pixie Image 2.",
            stylingTips = "Work texturizing fiber through dry hair from roots to tips, tousling crown sections upward.",
            faceShapes = listOf("Round", "Oval", "Heart", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_02,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "48.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Choppy", "Textured", "Edgy", "Volume")
        ),

        // PIXIE DESIGN 03 (img_w_pixie_03.jpg): Side-Swept Bangs Pixie Crop
        HairstyleItem(
            id = "w_pixie_03",
            name = "Side-Swept Bangs Pixie Crop",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Chic modern pixie featuring an elongated dramatic side-swept fringe and cropped tapered sides. Uploaded Women Pixie Image 3.",
            stylingTips = "Blow dry fringe with a small paddle brush directing across forehead, setting with flexible hold hairspray.",
            faceShapes = listOf("Oval", "Round", "Square", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_03,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "61.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Side Swept", "Bangs", "Feminine", "Popular")
        ),

        // PIXIE DESIGN 04 (img_w_pixie_04.jpg): Tousled Messy French Pixie
        HairstyleItem(
            id = "w_pixie_04",
            name = "Tousled Messy French Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Effortless Parisian nonchalance with undone crown layers and soft face-softening contours. Uploaded Women Pixie Image 4.",
            stylingTips = "Apply sea salt spray to damp roots and air-dry while gently scrunching with fingers.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_04,
            overlayAssetId = "overlay_w_pixie",
            rating = 5.0f,
            tryOnCount = "73.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Messy", "French", "Tousled", "Effortless")
        ),

        // PIXIE DESIGN 05 (img_w_pixie_05.jpg): Ultra-Short Minimalist Buzz Pixie
        HairstyleItem(
            id = "w_pixie_05",
            name = "Ultra-Short Minimalist Buzz Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Bold, clean micro-cropped crop accentuating bone structure, eyes, and elegant neck lines. Uploaded Women Pixie Image 5.",
            stylingTips = "Maintain with weekly scalp hydration and a dab of leave-in shine pomade for healthy radiance.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_05,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.7f,
            tryOnCount = "39.5k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Minimalist", "Ultra Short", "Micro", "Bold")
        ),

        // PIXIE DESIGN 06 (img_w_pixie_06.webp): Voluminous Layered Pixie Bob
        HairstyleItem(
            id = "w_pixie_06",
            name = "Voluminous Layered Pixie Bob",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "The perfect transition cut combining pixie lightness with bixie fullness and lifted crown architecture. Uploaded Women Pixie Image 6.",
            stylingTips = "Use root-lifting foam before blow drying with a round brush focused on the back crown.",
            faceShapes = listOf("Round", "Oval", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_pixie_06,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "66.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Bixie", "Layered", "Volume", "Trending")
        ),

        // PIXIE DESIGN 07 (img_w_pixie_07.jpg): Sleek Undercut Asymmetric Pixie
        HairstyleItem(
            id = "w_pixie_07",
            name = "Sleek Undercut Asymmetric Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Contemporary dual-tone statement crop with clean clipped nape undercut and sweeping asymmetrical crown. Uploaded Women Pixie Image 7.",
            stylingTips = "Comb smooth using styling wax for a high-gloss glass sheen or rough up for rock-and-roll texture.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_pixie_07,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "54.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Undercut", "Asymmetric", "Sleek", "Modern")
        ),

        // PIXIE DESIGN 08 (img_w_pixie_08.jpg): Curly Textured Ringlet Pixie
        HairstyleItem(
            id = "w_pixie_08",
            name = "Curly Textured Ringlet Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Bouncy defined coils cropped short with voluminous top curls celebrating natural curly texture. Uploaded Women Pixie Image 8.",
            stylingTips = "Define damp curls with curl cream and diffuse on low heat, then fluff crown gently with fingertips.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_pixie_08,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "58.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Curly", "Coils", "Natural", "Texture")
        ),

        // PIXIE DESIGN 09 (img_w_pixie_09.webp): Feathered Shag Pixie with Curtain Bangs
        HairstyleItem(
            id = "w_pixie_09",
            name = "Feathered Shag Pixie with Curtain Bangs",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Retro-modern fusion with parted wispy curtain bangs and soft razor-feathered perimeter. Uploaded Women Pixie Image 9.",
            stylingTips = "Part bangs down the middle and flick outer wings backward using a mini straightener.",
            faceShapes = listOf("Heart", "Oval", "Round", "Square"),
            hairLength = "Short",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_pixie_09,
            overlayAssetId = "overlay_w_pixie",
            rating = 5.0f,
            tryOnCount = "79.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Shag", "Curtain Bangs", "Retro", "Feathered")
        ),

        // PIXIE DESIGN 10 (img_w_pixie_10.jpg): Blonde Textured Spiky Pixie
        HairstyleItem(
            id = "w_pixie_10",
            name = "Blonde Textured Spiky Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Daring platinum blonde short cut with pieced architectural spikes and sharp sideburn tapers. Uploaded Women Pixie Image 10.",
            stylingTips = "Pinch hair tips upwards with high-hold clay wax for sharp, defined separation that lasts all day.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_10,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "51.2k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Spiky", "Platinum", "Textured", "Statement")
        ),

        // PIXIE DESIGN 11 (img_w_pixie_11.webp): Romantic Soft Wavy Pixie
        HairstyleItem(
            id = "w_pixie_11",
            name = "Romantic Soft Wavy Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Gentle S-wave movement through longer top layers imparting feminine grace to a short silhouette. Uploaded Women Pixie Image 11.",
            stylingTips = "Enhance soft waves with mousse and air-dry, finishing with a drop of argan oil for luster.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_11,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "63.7k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Wavy", "Romantic", "Soft", "Feminine")
        ),

        // PIXIE DESIGN 12 (img_w_pixie_12.jpg): Grown-Out Tapered Pixie Crop
        HairstyleItem(
            id = "w_pixie_12",
            name = "Grown-Out Tapered Pixie Crop",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Chic relaxed crop with slightly longer ear tucking layers and a clean neck taper. Uploaded Women Pixie Image 12.",
            stylingTips = "Tuck one side behind ear and let the opposite side flow forward naturally.",
            faceShapes = listOf("Oval", "Round", "Heart", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_12,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.8f,
            tryOnCount = "47.8k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Tapered", "Grown Out", "Casual", "Popular")
        ),

        // PIXIE DESIGN 13 (img_w_pixie_13.jpg): Slicked-Back Red Carpet Pixie
        HairstyleItem(
            id = "w_pixie_13",
            name = "Slicked-Back Red Carpet Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "High-fashion editorial wet-look style combed back sleekly to highlight cheekbones and jawline. Uploaded Women Pixie Image 13.",
            stylingTips = "Apply strong-hold wet-finish gel and comb straight back from forehead with a fine-tooth comb.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_13,
            overlayAssetId = "overlay_w_pixie",
            rating = 5.0f,
            tryOnCount = "69.0k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Slicked Back", "Editorial", "Glamour", "Red Carpet")
        ),

        // PIXIE DESIGN 14 (img_w_pixie_14.jpg): Textured Micro-Fringe Pixie
        HairstyleItem(
            id = "w_pixie_14",
            name = "Textured Micro-Fringe Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Audrey Hepburn-inspired gamine charm with cute baby bangs and feather-light temple contours. Uploaded Women Pixie Image 14.",
            stylingTips = "Smooth baby bangs flat with a mini flat iron and spray with dry texturizer for a modern edge.",
            faceShapes = listOf("Oval", "Heart", "Round", "Diamond"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_14,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "56.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Micro Fringe", "Baby Bangs", "Vintage", "Gamine")
        ),

        // PIXIE DESIGN 15 (img_w_pixie_15.jpg): Airy Piecey Layered Pixie
        HairstyleItem(
            id = "w_pixie_15",
            name = "Airy Piecey Layered Pixie",
            category = "Pixie",
            gender = Gender.WOMEN,
            description = "Featherlight multidirectional layering creating dynamic motion and youthful casual appeal. Uploaded Women Pixie Image 15.",
            stylingTips = "Shake hands through roots with dry shampoo powder for instant airy volume and separation.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Square"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_pixie_15,
            overlayAssetId = "overlay_w_pixie",
            rating = 4.9f,
            tryOnCount = "60.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Pixie", "Short", "Airy", "Piecey", "Youthful", "Casual")
        ),

        // ==========================================
        // WOMEN'S WAVY HAIRSTYLES (USER UPLOADED - 9 HD WAVY DESIGNS)
        // ==========================================

        // WAVY DESIGN 01 (img_w_wavy_01.jpg): Sun-Kissed Beach Wave Lob
        HairstyleItem(
            id = "w_wavy_01",
            name = "Sun-Kissed Beach Wave Lob",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Relaxed mid-length lob featuring effortless beachy bends and sun-drenched warm blonde dimensions. Uploaded Women Wavy Image 1.",
            stylingTips = "Spritz sea salt texture mist on towel-dried hair and wrap random 1-inch sections around an open curling wand.",
            faceShapes = listOf("Oval", "Round", "Heart", "Square"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_wavy_01,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.9f,
            tryOnCount = "58.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Beach Waves", "Lob", "Blonde", "Trending")
        ),

        // WAVY DESIGN 02 (img_w_wavy_02.webp): Old Hollywood Glamour Mermaid Waves
        HairstyleItem(
            id = "w_wavy_02",
            name = "Old Hollywood Glamour Mermaid Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Luxurious, flowing deep S-curve mermaid waves cascading with mirror-finish shine and vintage red carpet glamour. Uploaded Women Wavy Image 2.",
            stylingTips = "Set with a large barrel triple-waver, let completely cool, then gently brush through with a wide boar bristle brush.",
            faceShapes = listOf("Oval", "Square", "Diamond", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_wavy_02,
            overlayAssetId = "overlay_w_wavy",
            rating = 5.0f,
            tryOnCount = "74.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Mermaid Waves", "Glamour", "Old Hollywood", "Long")
        ),

        // WAVY DESIGN 03 (img_w_wavy_03.jpg): Brunette French Girl Tousled Waves
        HairstyleItem(
            id = "w_wavy_03",
            name = "Brunette French Girl Tousled Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Undone lived-in waves with soft face-framing softness and natural effortless movement. Uploaded Women Wavy Image 3.",
            stylingTips = "Air-dry with light leave-in balm and bend mid-lengths with a flat iron in alternating forward and backward directions.",
            faceShapes = listOf("Oval", "Heart", "Round", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_wavy_03,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.9f,
            tryOnCount = "63.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "French Girl", "Tousled", "Brunette", "Popular")
        ),

        // WAVY DESIGN 04 (img_w_wavy_04.jpg): Textured Curtain Bangs Wavy Shag
        HairstyleItem(
            id = "w_wavy_04",
            name = "Textured Curtain Bangs Wavy Shag",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Modern wavy shag blending 70s-inspired curtain bangs with feathery crown waves and dimensional texture. Uploaded Women Wavy Image 4.",
            stylingTips = "Blow dry curtain bangs outwards with a round brush and use matte clay to separate wavy ends.",
            faceShapes = listOf("Round", "Oval", "Heart", "Square"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_wavy_04,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.8f,
            tryOnCount = "52.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Curtain Bangs", "Shag", "Textured", "Modern")
        ),

        // WAVY DESIGN 05 (img_w_wavy_05.jpg): Short Wavy French Bob with Fringe
        HairstyleItem(
            id = "w_wavy_05",
            name = "Short Wavy French Bob with Fringe",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Chic chin-length wavy crop featuring subtle crinkles, micro-fringe, and Parisian aesthetic charm. Uploaded Women Wavy Image 5.",
            stylingTips = "Scrunch with curl-enhancing mousse while air drying, finishing with dry texturizing spray at the roots.",
            faceShapes = listOf("Oval", "Diamond", "Heart", "Round"),
            hairLength = "Short",
            maintenance = "Low",
            imageRes = R.drawable.img_w_wavy_05,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.9f,
            tryOnCount = "49.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "French Bob", "Short", "Fringe", "Parisian")
        ),

        // WAVY DESIGN 06 (img_w_wavy_06.webp): Glossy Caramel Balayage S-Waves
        HairstyleItem(
            id = "w_wavy_06",
            name = "Glossy Caramel Balayage S-Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Warm caramel ribbons woven through glossy, fluid S-pattern waves that sculpt facial contours. Uploaded Women Wavy Image 6.",
            stylingTips = "Prep with heat protectant oil and clamp sections vertically with a ceramic curling iron for relaxed ribbons.",
            faceShapes = listOf("Oval", "Round", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_wavy_06,
            overlayAssetId = "overlay_w_wavy",
            rating = 5.0f,
            tryOnCount = "81.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Balayage", "Caramel", "S-Waves", "Glossy")
        ),

        // WAVY DESIGN 07 (img_w_wavy_07.webp): Soft Layered Romantic Undone Waves
        HairstyleItem(
            id = "w_wavy_07",
            name = "Soft Layered Romantic Undone Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Dreamy cascading soft waves layered for buoyant bounce, airy movement, and effortless grace. Uploaded Women Wavy Image 7.",
            stylingTips = "Wrap large 2-inch chunks around wand leaving 1 inch of ends straight for modern casual fluidity.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_wavy_07,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.9f,
            tryOnCount = "67.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Romantic", "Layered", "Undone", "Trending")
        ),

        // WAVY DESIGN 08 (img_w_wavy_08.webp): Side-Parted Voluminous Hollywood Waves
        HairstyleItem(
            id = "w_wavy_08",
            name = "Side-Parted Voluminous Hollywood Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Deep side-parted polished waves creating a sculpted regal sweep with elevated crown volume. Uploaded Women Wavy Image 8.",
            stylingTips = "Create deep side part, curl in uniform direction toward face, brush into one continuous wave pattern and mist strong hold spray.",
            faceShapes = listOf("Oval", "Square", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_wavy_08,
            overlayAssetId = "overlay_w_wavy",
            rating = 5.0f,
            tryOnCount = "76.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Side Part", "Volume", "Hollywood", "Statement")
        ),

        // WAVY DESIGN 09 (img_w_wavy_09.jpg): Golden Honey Layered Mid-Length Waves
        HairstyleItem(
            id = "w_wavy_09",
            name = "Golden Honey Layered Mid-Length Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            description = "Luminous honey-toned waves falling gracefully around collarbones with feathered movement and healthy luster. Uploaded Women Wavy Image 9.",
            stylingTips = "Diffuse damp hair with light curl cream to encourage natural wave crests, then smooth crown with shine serum.",
            faceShapes = listOf("Oval", "Round", "Heart", "Diamond"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_wavy_09,
            overlayAssetId = "overlay_w_wavy",
            rating = 4.8f,
            tryOnCount = "55.7k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Wavy", "Waves", "Honey Blonde", "Collarbone", "Luminous", "Popular")
        ),

        // ==========================================
        // WOMEN'S BRAIDS HAIRSTYLES (USER UPLOADED - 10 HD BRAIDS DESIGNS)
        // ==========================================

        // BRAIDS DESIGN 01 (img_w_braids_01.jpg): Knotless Waist-Length Box Braids
        HairstyleItem(
            id = "w_braids_01",
            name = "Knotless Waist-Length Box Braids",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Sleek, pain-free knotless box braids flowing effortlessly down to the waist with clean geometric parts and lightweight movement. Uploaded Women Braids Image 1.",
            stylingTips = "Apply scalp oil along parted rows and wrap in a satin scarf overnight to maintain pristine edges.",
            faceShapes = listOf("Oval", "Heart", "Square", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_01,
            overlayAssetId = "overlay_w_braids",
            rating = 5.0f,
            tryOnCount = "94.2k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Box Braids", "Knotless", "Long", "Protective Style", "Trending")
        ),

        // BRAIDS DESIGN 02 (img_w_braids_02.jpg): Bohemian Goddess Box Braids
        HairstyleItem(
            id = "w_braids_02",
            name = "Bohemian Goddess Box Braids",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Romantic goddess braids blending crisp knotless plaits with soft curly tendrils unfurling throughout the lengths. Uploaded Women Braids Image 2.",
            stylingTips = "Spritz curl-defining mousse on the wavy tendrils and gently finger-comb to avoid tangling.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_braids_02,
            overlayAssetId = "overlay_w_braids",
            rating = 4.9f,
            tryOnCount = "88.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Goddess Braids", "Boho", "Curls", "Textured", "Popular")
        ),

        // BRAIDS DESIGN 03 (img_w_braids_03.jpg): Crown Dutch Halo Braid Updo
        HairstyleItem(
            id = "w_braids_03",
            name = "Crown Dutch Halo Braid Updo",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "An ethereal halo Dutch braid wrapped securely around the crown with soft wispy face-framing strands. Uploaded Women Braids Image 3.",
            stylingTips = "Gently pancake braid loops outward before pinning for enhanced volume and a dreamy romantic finish.",
            faceShapes = listOf("Oval", "Heart", "Square", "Round"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_03,
            overlayAssetId = "overlay_w_braids",
            rating = 4.8f,
            tryOnCount = "67.4k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Braids", "Crown Braid", "Halo", "Updo", "Bridal", "Romantic")
        ),

        // BRAIDS DESIGN 04 (img_w_braids_04.webp): Fulani Stitch Braids with Beads
        HairstyleItem(
            id = "w_braids_04",
            name = "Fulani Stitch Braids with Beads",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Intricately patterned Fulani stitch braids cascading from a sharp center part, adorned with artisan wooden beads and metallic cuffs. Uploaded Women Braids Image 4.",
            stylingTips = "Mist with braid sheen spray daily and edge control for neat, razor-sharp cornrow stitch lines.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_04,
            overlayAssetId = "overlay_w_braids",
            rating = 5.0f,
            tryOnCount = "91.8k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Fulani", "Stitch Braids", "Cornrows", "Accessories", "Beads")
        ),

        // BRAIDS DESIGN 05 (img_w_braids_05.webp): Chunky French Plait High Ponytail
        HairstyleItem(
            id = "w_braids_05",
            name = "Chunky French Plait High Ponytail",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Bold, sculptured French inverted braids feeding into a sleek snatched high ponytail with bouncy ends. Uploaded Women Braids Image 5.",
            stylingTips = "Use a strong-hold pomade along the hairline and wrap the ponytail base with a slim braid for seamless polish.",
            faceShapes = listOf("Oval", "Heart", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_braids_05,
            overlayAssetId = "overlay_w_braids",
            rating = 4.9f,
            tryOnCount = "79.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Ponytail", "French Braid", "Snatched", "High Ponytail", "Sporty Chic")
        ),

        // BRAIDS DESIGN 06 (img_w_braids_06.jpg): Textured Fishtail Mermaid Braid
        HairstyleItem(
            id = "w_braids_06",
            name = "Textured Fishtail Mermaid Braid",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "A relaxed, dimensional fishtail side braid featuring micro-weave textures and a softly loosened silhouette. Uploaded Women Braids Image 6.",
            stylingTips = "Dust dry texturizing powder along the braid before gently tugging sections to build full-bodied bohemian texture.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_06,
            overlayAssetId = "overlay_w_braids",
            rating = 4.7f,
            tryOnCount = "62.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Braids", "Fishtail", "Mermaid", "Boho", "Casual", "Side Braid")
        ),

        // BRAIDS DESIGN 07 (img_w_braids_07.jpg): Double Feed-In Boxer Dutch Braids
        HairstyleItem(
            id = "w_braids_07",
            name = "Double Feed-In Boxer Dutch Braids",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Twin symmetrical Dutch boxer braids engineered with the feed-in method for a flat, natural scalp transition. Uploaded Women Braids Image 7.",
            stylingTips = "Apply edge gel with a soft bristle brush and sleep with a silk durag or wrap to preserve flyaway-free sleekness.",
            faceShapes = listOf("Oval", "Diamond", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_07,
            overlayAssetId = "overlay_w_braids",
            rating = 4.9f,
            tryOnCount = "84.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Dutch Braids", "Boxer Braids", "Feed-In", "Athletic", "Trending")
        ),

        // BRAIDS DESIGN 08 (img_w_braids_08.webp): Jumbo Triangle Part Goddess Braids
        HairstyleItem(
            id = "w_braids_08",
            name = "Jumbo Triangle Part Goddess Braids",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Striking jumbo-sized braids featuring sharp geometric triangle parting and plush, glossy spiral tips. Uploaded Women Braids Image 8.",
            stylingTips = "Dip braid ends in hot water for silkiness and use lightweight scalp tonic between triangle sections.",
            faceShapes = listOf("Oval", "Heart", "Round", "Diamond"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_braids_08,
            overlayAssetId = "overlay_w_braids",
            rating = 5.0f,
            tryOnCount = "97.4k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Jumbo Braids", "Triangle Parts", "Statement", "Protective", "Popular")
        ),

        // BRAIDS DESIGN 09 (img_w_braids_09.jpg): Half-Up Waterfall Lace Braid
        HairstyleItem(
            id = "w_braids_09",
            name = "Half-Up Waterfall Lace Braid",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "Delicate cascading waterfall braid weaving horizontally across the crown, letting loose waves drape freely beneath. Uploaded Women Braids Image 9.",
            stylingTips = "Drop the bottom strand on each pass to achieve the true waterfall cascade, then add light curling wand bends to the free hair.",
            faceShapes = listOf("Oval", "Round", "Square", "Heart"),
            hairLength = "Medium",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_braids_09,
            overlayAssetId = "overlay_w_braids",
            rating = 4.8f,
            tryOnCount = "71.9k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Braids", "Waterfall Braid", "Half-Up", "Lace Braid", "Feminine", "Wedding")
        ),

        // BRAIDS DESIGN 10 (img_w_braids_10.webp): Braided Top Knot Bun with Tendrils
        HairstyleItem(
            id = "w_braids_10",
            name = "Braided Top Knot Bun with Tendrils",
            category = "Braids",
            gender = Gender.WOMEN,
            description = "High sculpted top knot bun constructed with intertwined micro-plaits and curly face-framing baby hair accents. Uploaded Women Braids Image 10.",
            stylingTips = "Secure high ponytail firmly with bungee hair elastic, twist braids around the base, and pin securely with U-pins.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Square"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_braids_10,
            overlayAssetId = "overlay_w_braids",
            rating = 4.9f,
            tryOnCount = "86.1k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Braids", "Top Knot", "Braided Bun", "Updo", "Glamour", "Trending")
        ),

        // ==========================================
        // WOMEN'S PONYTAIL HAIRSTYLES (USER UPLOADED - 5 HD PONYTAIL DESIGNS)
        // ==========================================

        // PONYTAIL DESIGN 01 (img_w_ponytail_01.jpg): Sleek Ultra-High Runway Ponytail
        HairstyleItem(
            id = "w_ponytail_01",
            name = "Sleek Ultra-High Runway Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            description = "An impeccably polished high ponytail pulled taut at the crown, with a seamless wrapped hair base and razor-straight glossy length. Uploaded Women Ponytail Image 1.",
            stylingTips = "Brush backwards with boar bristle brush and strong hold gel, secure tightly with a hook bungee elastic, and wrap a strand around the base to conceal.",
            faceShapes = listOf("Oval", "Heart", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_ponytail_01,
            overlayAssetId = "overlay_w_ponytail",
            rating = 5.0f,
            tryOnCount = "92.5k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Ponytail", "Sleek", "High Ponytail", "Runway", "Polished", "Trending")
        ),

        // PONYTAIL DESIGN 02 (img_w_ponytail_02.jpg): Voluminous Textured Glamour Ponytail
        HairstyleItem(
            id = "w_ponytail_02",
            name = "Voluminous Textured Glamour Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            description = "A full-bodied high ponytail with teasing at the base, lush cascading wave texture, and soft romantic tendrils framing the temples. Uploaded Women Ponytail Image 2.",
            stylingTips = "Tease the ponytail interior in two horizontal tiers with texturizing spray, then curl ends outward with a 1.25-inch barrel.",
            faceShapes = listOf("Oval", "Round", "Diamond", "Heart"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_ponytail_02,
            overlayAssetId = "overlay_w_ponytail",
            rating = 4.9f,
            tryOnCount = "87.3k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Ponytail", "Volume", "Textured", "Waves", "Glamour", "Popular")
        ),

        // PONYTAIL DESIGN 03 (img_w_ponytail_03.jpg): Low Effortless French Girl Ponytail
        HairstyleItem(
            id = "w_ponytail_03",
            name = "Low Effortless French Girl Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            description = "A relaxed low-slung ponytail gathered at the nape of the neck with center-parted curtain bangs and airy, undone movement. Uploaded Women Ponytail Image 3.",
            stylingTips = "Keep natural parting loose, gather gently at the nape without pulling tight, and pull out whispy tendrils in front of each ear.",
            faceShapes = listOf("Oval", "Square", "Round", "Heart"),
            hairLength = "Medium",
            maintenance = "Low",
            imageRes = R.drawable.img_w_ponytail_03,
            overlayAssetId = "overlay_w_ponytail",
            rating = 4.8f,
            tryOnCount = "74.1k",
            isTrending = false,
            isPopular = true,
            tags = listOf("Ponytail", "Low Ponytail", "French Girl", "Casual", "Curtain Bangs", "Effortless")
        ),

        // PONYTAIL DESIGN 04 (img_w_ponytail_04.jpg): Bubble Y2K Segmented Ponytail
        HairstyleItem(
            id = "w_ponytail_04",
            name = "Bubble Y2K Segmented Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            description = "A playful and chic bubble ponytail segmented with clear mini-elastics, creating evenly puffed spheres down the length. Uploaded Women Ponytail Image 4.",
            stylingTips = "Space clear elastics two inches apart down the ponytail length, then gently pinch and pull each sphere outward for round fullness.",
            faceShapes = listOf("Oval", "Heart", "Diamond", "Round"),
            hairLength = "Long",
            maintenance = "Low",
            imageRes = R.drawable.img_w_ponytail_04,
            overlayAssetId = "overlay_w_ponytail",
            rating = 4.9f,
            tryOnCount = "83.6k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Ponytail", "Bubble Braid", "Y2K", "Fun", "Segmented", "Trending")
        ),

        // PONYTAIL DESIGN 05 (img_w_ponytail_05.jpg): Romantic Braided Side-Sweep Ponytail
        HairstyleItem(
            id = "w_ponytail_05",
            name = "Romantic Braided Side-Sweep Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            description = "An intricate crown Dutch braid feeding diagonally into a side-swept mid ponytail with soft tumbling waves. Uploaded Women Ponytail Image 5.",
            stylingTips = "Braid loosely from temple along the ear toward the opposite nape, secure into ponytail, and loosen plait loops for soft dimension.",
            faceShapes = listOf("Oval", "Round", "Square", "Diamond"),
            hairLength = "Long",
            maintenance = "Medium",
            imageRes = R.drawable.img_w_ponytail_05,
            overlayAssetId = "overlay_w_ponytail",
            rating = 4.9f,
            tryOnCount = "78.9k",
            isTrending = true,
            isPopular = true,
            tags = listOf("Ponytail", "Braided Ponytail", "Romantic", "Side-Sweep", "Boho", "Statement")
        )
    )

    fun getAllHairstyles(): List<HairstyleItem> = allHairstyles

    fun getHairstylesByGender(gender: Gender): List<HairstyleItem> {
        return allHairstyles.filter { it.gender == gender }
    }

    fun getTrending(): List<HairstyleItem> {
        return allHairstyles.filter { it.isTrending }
    }

    fun getTrendingByGender(gender: Gender): List<HairstyleItem> {
        return allHairstyles.filter { it.gender == gender && it.isTrending }
    }

    fun getPopular(): List<HairstyleItem> {
        return allHairstyles.filter { it.isPopular }
    }

    fun getById(id: String): HairstyleItem? {
        val exact = allHairstyles.find { it.id == id }
        if (exact != null) return exact
        for (parent in allHairstyles) {
            val variation = parent.variations.find { "${parent.id}_${it.id}" == id }
            if (variation != null) {
                return parent.toVariationItem(variation)
            }
        }
        return null
    }

    fun filterHome(
        gender: Gender,
        category: String,
        query: String?
    ): List<HairstyleItem> {
        return allHairstyles.filter { item ->
            if (item.gender != gender) return@filter false

            val matchCategory = when (category.lowercase()) {
                "all" -> true
                "fade" -> item.category.contains("Fade", ignoreCase = true) || item.tags.any { it.contains("Fade", ignoreCase = true) }
                "short" -> item.hairLength.equals("Short", ignoreCase = true) || item.category.equals("Short", ignoreCase = true) || item.tags.any { it.equals("Short", ignoreCase = true) }
                "medium" -> item.hairLength.equals("Medium", ignoreCase = true) || item.category.equals("Medium", ignoreCase = true) || item.tags.any { it.equals("Medium", ignoreCase = true) }
                "long", "long hair" -> item.hairLength.equals("Long", ignoreCase = true) || item.category.contains("Long", ignoreCase = true) || item.tags.any { it.contains("Long", ignoreCase = true) }
                "curly" -> item.category.contains("Curly", ignoreCase = true) || item.tags.any { it.contains("Curl", ignoreCase = true) }
                "buzz", "buzz cut" -> item.category.contains("Buzz", ignoreCase = true) || item.tags.any { it.contains("Buzz", ignoreCase = true) }
                "quiff" -> item.category.contains("Quiff", ignoreCase = true) || item.tags.any { it.contains("Quiff", ignoreCase = true) }
                "side part", "side" -> item.category.contains("Side", ignoreCase = true) || item.tags.any { it.contains("Side", ignoreCase = true) }
                "bob" -> item.category.contains("Bob", ignoreCase = true) || item.tags.any { it.contains("Bob", ignoreCase = true) }
                "pixie" -> item.category.contains("Pixie", ignoreCase = true) || item.tags.any { it.contains("Pixie", ignoreCase = true) }
                "layers", "layer" -> item.category.contains("Layer", ignoreCase = true) || item.tags.any { it.contains("Layer", ignoreCase = true) }
                "wavy" -> item.category.contains("Wavy", ignoreCase = true) || item.tags.any { it.contains("Wave", ignoreCase = true) }
                "braids" -> item.category.contains("Braid", ignoreCase = true) || item.tags.any { it.contains("Braid", ignoreCase = true) }
                "ponytail", "ponytails" -> item.category.contains("Ponytail", ignoreCase = true) || item.tags.any { it.contains("Ponytail", ignoreCase = true) }
                else -> item.category.equals(category, ignoreCase = true)
            }

            val matchQuery = query.isNullOrBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.hairLength.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.tags.any { it.contains(query, ignoreCase = true) }

            matchCategory && matchQuery
        }
    }

    fun filter(
        gender: Gender?,
        category: String?,
        query: String?
    ): List<HairstyleItem> {
        return allHairstyles.filter { item ->
            val matchGender = gender == null || item.gender == gender
            val matchCategory = category == null || category == "All" ||
                    if (category.equals("Short", ignoreCase = true)) {
                        item.hairLength.equals("Short", ignoreCase = true) || item.category.equals("Short", ignoreCase = true) || item.tags.any { it.equals("Short", ignoreCase = true) }
                    } else if (category.equals("Medium", ignoreCase = true)) {
                        item.hairLength.equals("Medium", ignoreCase = true) || item.category.equals("Medium", ignoreCase = true) || item.tags.any { it.equals("Medium", ignoreCase = true) }
                    } else if (category.equals("Long", ignoreCase = true) || category.equals("Long Hair", ignoreCase = true)) {
                        item.hairLength.equals("Long", ignoreCase = true) || item.category.contains("Long", ignoreCase = true) || item.tags.any { it.contains("Long", ignoreCase = true) }
                    } else if (category.equals("Buzz Cut", ignoreCase = true) || category.equals("Buzz", ignoreCase = true)) {
                        item.category.contains("Buzz", ignoreCase = true) || item.tags.any { it.contains("Buzz", ignoreCase = true) }
                    } else if (category.equals("Quiff", ignoreCase = true)) {
                        item.category.contains("Quiff", ignoreCase = true) || item.tags.any { it.contains("Quiff", ignoreCase = true) }
                    } else if (category.equals("Side Part", ignoreCase = true) || category.equals("Side", ignoreCase = true)) {
                        item.category.contains("Side", ignoreCase = true) || item.tags.any { it.contains("Side", ignoreCase = true) }
                    } else if (category.equals("Bob", ignoreCase = true)) {
                        item.category.contains("Bob", ignoreCase = true) || item.tags.any { it.contains("Bob", ignoreCase = true) }
                    } else if (category.equals("Pixie", ignoreCase = true)) {
                        item.category.contains("Pixie", ignoreCase = true) || item.tags.any { it.contains("Pixie", ignoreCase = true) }
                    } else if (category.equals("Curly", ignoreCase = true)) {
                        item.category.contains("Curly", ignoreCase = true) || item.tags.any { it.contains("Curl", ignoreCase = true) }
                    } else if (category.equals("Layers", ignoreCase = true) || category.equals("Layer", ignoreCase = true)) {
                        item.category.contains("Layer", ignoreCase = true) || item.tags.any { it.contains("Layer", ignoreCase = true) }
                    } else if (category.equals("Wavy", ignoreCase = true) || category.equals("Waves", ignoreCase = true)) {
                        item.category.contains("Wavy", ignoreCase = true) || item.tags.any { it.contains("Wave", ignoreCase = true) }
                    } else if (category.equals("Braids", ignoreCase = true) || category.equals("Braid", ignoreCase = true)) {
                        item.category.contains("Braid", ignoreCase = true) || item.tags.any { it.contains("Braid", ignoreCase = true) }
                    } else if (category.equals("Ponytail", ignoreCase = true) || category.equals("Ponytails", ignoreCase = true)) {
                        item.category.contains("Ponytail", ignoreCase = true) || item.tags.any { it.contains("Ponytail", ignoreCase = true) }
                    } else {
                        item.category.equals(category, ignoreCase = true) || item.tags.any { it.equals(category, ignoreCase = true) }
                    }
            val matchQuery = query.isNullOrBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.hairLength.contains(query, ignoreCase = true) ||
                    item.tags.any { it.contains(query, ignoreCase = true) }
            matchGender && matchCategory && matchQuery
        }
    }

    val sampleHistory = listOf(
        TryOnHistoryItem(
            id = "hist_1",
            hairstyleName = "Textured French Crop",
            category = "Men • Textured Crop",
            date = "Yesterday, 4:15 PM",
            matchScore = 98,
            imageRes = R.drawable.img_tryon_result
        ),
        TryOnHistoryItem(
            id = "hist_2",
            hairstyleName = "90s Butterfly Layers",
            category = "Women • Layers",
            date = "Sep 22, 2:30 PM",
            matchScore = 96,
            imageRes = R.drawable.img_women_hairstyles
        ),
        TryOnHistoryItem(
            id = "hist_3",
            hairstyleName = "Textured Medium Crop",
            category = "Men • Medium",
            date = "Sep 18, 6:00 PM",
            matchScore = 95,
            imageRes = R.drawable.img_medium_1
        )
    )
}
