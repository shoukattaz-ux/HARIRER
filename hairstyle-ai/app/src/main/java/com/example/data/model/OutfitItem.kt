package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Audience category for outfits: Men, Women, or Baby/Kids.
 */
enum class OutfitAudience {
    MEN,
    WOMEN,
    BABY
}

/**
 * Model representing a wearable outfit or clothing item in the Virtual Dressing Room.
 */
data class OutfitItem(
    val id: String,
    val name: String,
    val category: String, // "Formal Suits", "Blazers", "Jackets", "Traditional", "Casual", "Party Wear"
    val audience: OutfitAudience,
    @DrawableRes val drawableRes: Int,
    val colorName: String,
    val accentColorHex: Long,
    val description: String,
    val tag: String = "Popular"
)

object OutfitRepository {
    val outfits: List<OutfitItem> = listOf(
        // 3D REALISTIC MANNEQUINS (Matching User Uploaded 3D Models)
        OutfitItem(
            id = "outfit_mannequin_grey_db_suit",
            name = "Charcoal Double-Breasted 3D Mannequin",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_mannequin_grey_db_suit,
            colorName = "Charcoal Grey & Burgundy",
            accentColorHex = 0xFF475569,
            description = "High-definition 3D studio male mannequin wearing a tailored 6-button double-breasted charcoal suit with peak lapels, silk tie, pocket square, and oxfords.",
            tag = "3D Studio Mannequin"
        ),
        OutfitItem(
            id = "outfit_mannequin_navy_bespoke_suit",
            name = "Royal Navy Tailored 3D Mannequin",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_mannequin_navy_bespoke_suit,
            colorName = "Royal Navy & Italian Brown",
            accentColorHex = 0xFF1E3A8A,
            description = "Bespoke 3D tailor mannequin in a sharp royal navy 2-button jacket, crisp open-collar white shirt, white pocket square, and burnished brown oxfords.",
            tag = "3D Studio Mannequin"
        ),

        // REAL FULL MEN OUTFITS (Shirt + Pants + Tie)
        OutfitItem(
            id = "outfit_real_white_suit",
            name = "White Shirt & Burgundy Tie + Charcoal Pants",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_real_suit_white,
            colorName = "White, Burgundy & Charcoal",
            accentColorHex = 0xFFBE123C,
            description = "Complete luxury outfit: Crisp Egyptian white shirt, pure silk burgundy tie, tailored charcoal wool pants with pressed crease and leather belt.",
            tag = "Full Real Outfit"
        ),
        OutfitItem(
            id = "outfit_real_navy_suit",
            name = "Royal Navy 3-Piece & Gold Tie + Navy Pants",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_real_suit_navy,
            colorName = "Navy & Gold Accent",
            accentColorHex = 0xFF1E40AF,
            description = "Complete royal ensemble: Tailored navy vest, crisp white dress shirt, patterned silk tie with gold clip, and matching navy formal trousers.",
            tag = "Full Real Outfit"
        ),
        OutfitItem(
            id = "outfit_real_blue_suit",
            name = "Sky Blue Shirt & Black Tie + Slim Pants",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_real_suit_blue,
            colorName = "Sky Blue & Obsidian Black",
            accentColorHex = 0xFF38BDF8,
            description = "Modern executive look: Premium Oxford sky blue shirt, jet black woven silk tie with silver bar, and slim-fit black formal trousers.",
            tag = "Full Real Outfit"
        ),
        OutfitItem(
            id = "outfit_real_black_suit",
            name = "Black Tuxedo Blazer & Bow Tie + Pants",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_real_suit_black,
            colorName = "Obsidian Black & White",
            accentColorHex = 0xFF18181B,
            description = "Classic red-carpet tuxedo: Midnight black jacket with satin lapels, white tuxedo shirt with black studs, black satin bow tie, and satin-striped trousers.",
            tag = "Full Real Outfit"
        ),

        // MEN CLASSICS
        OutfitItem(
            id = "outfit_suit_navy",
            name = "Royal Navy 3-Piece Suit",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_suit_navy,
            colorName = "Navy Blue",
            accentColorHex = 0xFF1E3A8A,
            description = "Bespoke Italian tailored navy wool suit with burgundy silk tie and crisp white shirt.",
            tag = "Trending"
        ),
        OutfitItem(
            id = "outfit_suit_black",
            name = "Midnight Black Dinner Tuxedo",
            category = "Formal Suits",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_suit_black,
            colorName = "Obsidian Black",
            accentColorHex = 0xFF111827,
            description = "Classic red-carpet tuxedo with satin shawl lapels and velvet bow tie.",
            tag = "Black Tie"
        ),
        OutfitItem(
            id = "outfit_blazer_grey",
            name = "Executive Charcoal Blazer",
            category = "Blazers",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_blazer_grey,
            colorName = "Charcoal Grey",
            accentColorHex = 0xFF4B5563,
            description = "Versatile heather wool blazer paired with an Oxford sky blue button-down shirt.",
            tag = "Business"
        ),
        OutfitItem(
            id = "outfit_kurta_royal",
            name = "Royal Maroon Embroidered Kurta",
            category = "Traditional",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_kurta_royal,
            colorName = "Maroon & Gold",
            accentColorHex = 0xFF881337,
            description = "Festive ethnic kurta with intricate golden zari threadwork and mandarin collar.",
            tag = "Festive"
        ),
        OutfitItem(
            id = "outfit_jacket_leather",
            name = "Moto Biker Leather Jacket",
            category = "Jackets",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_jacket_leather,
            colorName = "Matte Black",
            accentColorHex = 0xFF18181B,
            description = "Edgy cafe racer leather jacket with silver asymmetric zippers and snap lapels.",
            tag = "Streetwear"
        ),
        OutfitItem(
            id = "outfit_jacket_denim",
            name = "Vintage Indigo Denim Jacket",
            category = "Jackets",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_jacket_denim,
            colorName = "Indigo Blue",
            accentColorHex = 0xFF1D4ED8,
            description = "Classic vintage washed trucker denim jacket with brass buttons and twin chest pockets.",
            tag = "Casual"
        ),
        OutfitItem(
            id = "outfit_hoodie_streetwear",
            name = "Urban Drop-Shoulder Hoodie",
            category = "Casual",
            audience = OutfitAudience.MEN,
            drawableRes = R.drawable.overlay_hoodie_streetwear,
            colorName = "Slate Grey",
            accentColorHex = 0xFF334155,
            description = "Heavyweight 450gsm minimalist streetwear hoodie with relaxed kangaroo pocket.",
            tag = "Comfort"
        ),

        // WOMEN
        OutfitItem(
            id = "outfit_mannequin_vintage_dress",
            name = "Vintage Midnight Navy Satin Gown 3D",
            category = "Dresses",
            audience = OutfitAudience.WOMEN,
            drawableRes = R.drawable.overlay_mannequin_vintage_dress,
            colorName = "Midnight Navy Satin",
            accentColorHex = 0xFF1C2541,
            description = "Haute couture 3D dressmaker mannequin draped in a vintage midnight navy satin ballgown with halter bodice and sweeping lustrous train.",
            tag = "3D Studio Mannequin"
        ),
        OutfitItem(
            id = "outfit_women_blazer",
            name = "Tailored Ivory Power Blazer",
            category = "Blazers",
            audience = OutfitAudience.WOMEN,
            drawableRes = R.drawable.overlay_women_blazer,
            colorName = "Ivory Cream",
            accentColorHex = 0xFFE2E8F0,
            description = "High-fashion structured feminine blazer with peaked lapels and gold statement buttons.",
            tag = "Executive"
        ),
        OutfitItem(
            id = "outfit_women_dress",
            name = "Emerald Satin Evening Gown",
            category = "Dresses",
            audience = OutfitAudience.WOMEN,
            drawableRes = R.drawable.overlay_women_dress,
            colorName = "Emerald Green",
            accentColorHex = 0xFF065F46,
            description = "Glamorous floor-length satin evening gown with sweetheart neckline and crystal brooch.",
            tag = "Party Wear"
        ),
        OutfitItem(
            id = "outfit_women_denim",
            name = "Chic Cropped Denim Jacket",
            category = "Jackets",
            audience = OutfitAudience.WOMEN,
            drawableRes = R.drawable.overlay_jacket_denim,
            colorName = "Vintage Indigo",
            accentColorHex = 0xFF2563EB,
            description = "Trendy cropped boyfriend denim jacket with brass buttons and soft washed wash.",
            tag = "Casual Chic"
        ),

        // BABY / KIDS
        OutfitItem(
            id = "outfit_baby_suit",
            name = "Toddler Prince Mini Tuxedo",
            category = "Party Wear",
            audience = OutfitAudience.BABY,
            drawableRes = R.drawable.overlay_baby_suit,
            colorName = "Navy & Red Bow",
            accentColorHex = 0xFFDC2626,
            description = "Adorable mini tuxedo blazer with red silk bow tie, pleated shirt and gold buttons.",
            tag = "Cute & Party"
        ),
        OutfitItem(
            id = "outfit_baby_romper",
            name = "Little Star Pastel Romper",
            category = "Casual",
            audience = OutfitAudience.BABY,
            drawableRes = R.drawable.overlay_baby_romper,
            colorName = "Pastel Sky Blue",
            accentColorHex = 0xFF38BDF8,
            description = "Ultra-soft cotton romper with Peter Pan collar and cute teddy bear pocket.",
            tag = "Daily Comfort"
        ),
        OutfitItem(
            id = "outfit_baby_kurta",
            name = "Baby Royal Festive Kurta",
            category = "Traditional",
            audience = OutfitAudience.BABY,
            drawableRes = R.drawable.overlay_baby_kurta,
            colorName = "Gold & Maroon",
            accentColorHex = 0xFFF59E0B,
            description = "Festive little kurta with royal maroon ban collar and zari embroidery for weddings.",
            tag = "Festive"
        )
    )

    val categories: List<String> = listOf("All", "Formal Suits", "Blazers", "Jackets", "Traditional", "Casual", "Dresses", "Party Wear")

    fun getOutfitsForAudience(audience: OutfitAudience): List<OutfitItem> {
        return outfits.filter { it.audience == audience }
    }

    fun getCategoriesForAudience(audience: OutfitAudience): List<String> {
        val cats = outfits.filter { it.audience == audience }.map { it.category }.distinct()
        return listOf("All") + cats
    }

    fun filterOutfits(audience: OutfitAudience, category: String, query: String): List<OutfitItem> {
        return outfits.filter { outfit ->
            val matchesAudience = outfit.audience == audience
            val matchesCat = if (category == "All") true else outfit.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                outfit.name.contains(query, ignoreCase = true) ||
                outfit.category.contains(query, ignoreCase = true) ||
                outfit.colorName.contains(query, ignoreCase = true) ||
                outfit.description.contains(query, ignoreCase = true)
            }
            matchesAudience && matchesCat && matchesQuery
        }
    }

    fun getById(id: String): OutfitItem? {
        return outfits.firstOrNull { it.id == id }
    }
}
