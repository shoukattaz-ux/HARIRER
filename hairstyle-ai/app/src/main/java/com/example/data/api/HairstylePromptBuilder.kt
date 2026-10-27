package com.example.data.api

import com.example.data.model.Gender
import com.example.data.model.HairstyleItem

object HairstylePromptBuilder {

    /**
     * Builds a detailed, dynamic AI instruction to transform the user's hairstyle
     * while rigorously preserving their facial identity, skin, clothing, and background.
     *
     * Adheres to Step 4 requirements:
     * - Preserves face identity, structure, skin, eyes, nose, mouth, expression, clothing, background.
     * - Strictly transforms only the hairstyle/hair appearance.
     * - Works dynamically for all hairstyles across Men and Women collections.
     * - Ensures hair matches head position, lighting, texture, edges, and proportions.
     */
    fun buildTransformationPrompt(
        hairstyle: HairstyleItem,
        colorTint: String? = null
    ): String {
        val genderTitle = if (hairstyle.gender == Gender.MEN) "men's haircut" else "women's hairstyle"
        val faceShapesText = hairstyle.faceShapes.joinToString(", ")
        val colorInstruction = if (!colorTint.isNullOrBlank() && colorTint != "Natural Black") {
            "- Desired Color Tone: Apply a natural, salon-quality $colorTint shade to the hair."
        } else {
            "- Desired Color Tone: Maintain the subject's natural organic hair color matching their eyebrows."
        }

        return buildString {
            appendLine("You are an elite master barber, high-end celebrity salon stylist, and photorealistic portrait retouch artist.")
            appendLine("PRIMARY OBJECTIVE: In the provided portrait photograph, REPLACE and TRANSFORM only the person's hairstyle to the specified $genderTitle: \"${hairstyle.name}\" (${hairstyle.category}).")
            appendLine()
            appendLine("HAIRSTYLE SPECIFICATIONS:")
            appendLine("- Hairstyle Name: ${hairstyle.name}")
            appendLine("- Category: ${hairstyle.category}")
            appendLine("- Gender Collection: ${hairstyle.gender.displayName}'s styles (strictly adhere to this gender classification)")
            appendLine("- Hair Length: ${hairstyle.hairLength}")
            appendLine("- Cut, Texture & Volume: ${hairstyle.description}")
            appendLine("- Styling Instructions: ${hairstyle.stylingTips}")
            if (hairstyle.tags.isNotEmpty()) {
                appendLine("- Key Aesthetic Tags: ${hairstyle.tags.joinToString(", ")}")
            }
            appendLine("- Ideal Facial Architecture: Tailored for $faceShapesText face shapes")
            appendLine(colorInstruction)
            appendLine()
            appendLine("STRICT PRESERVATION AND IDENTITY INTEGRITY MANDATES:")
            appendLine("1. UNCOMPROMISED IDENTITY PRESERVATION: The person in the generated image MUST be recognizably the exact same human being as in the input photo. Keep all facial features completely identical and unaltered: eyes, eye shape, eye color, eyebrows, nose, mouth, lips, smile/facial expression, skin tone, skin texture, facial bone structure, cheeks, jawline, ears, and age. Do NOT generate a different person, alter their ethnicity, or change their facial proportions.")
            appendLine("2. PRECISE SCALP & HAIRLINE FIT: Attach the new ${hairstyle.name} naturally to the person's actual head structure. Follow their natural forehead contour, temples, and sideburns so the haircut looks genuinely grown from and attached to their scalp.")
            appendLine("3. LIGHTING & COLOR HARMONY: Match the angle of environmental lighting, highlights, shadows, and color temperature from the existing photo so the new hair looks organically photographed in the exact same setting.")
            appendLine("4. APPAREL & BACKGROUND PRESERVATION: Keep the person's neck, shoulders, clothing, shirt collar, and background environment intact and unchanged.")
            appendLine("5. FLAWLESS PHOTOREALISM: Generate realistic individual hair strands, natural hairline gradient, realistic hair texture, and natural edges around the face without obvious AI blurring or digital artifacts.")
            appendLine("6. OUTPUT: Produce a single photorealistic portrait photograph of the SAME person wearing their new ${hairstyle.name} hairstyle.")
        }
    }
}
