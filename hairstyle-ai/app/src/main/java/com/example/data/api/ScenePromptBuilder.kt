package com.example.data.api

import com.example.data.model.Gender
import com.example.data.model.HairstyleItem

/**
 * Lifestyle settings for Style Score scenes. Outfits differ for men's and women's collections.
 */
enum class LifestyleScene(
    val label: String,
    val setting: String,
    val menOutfit: String,
    val womenOutfit: String
) {
    OFFICE(
        label = "Office",
        setting = "a bright modern office with floor-to-ceiling windows and a city skyline, softly blurred colleagues in the background",
        menOutfit = "a tailored charcoal three-piece suit with an open-collar white shirt and a classic wristwatch",
        womenOutfit = "a tailored blazer over a silk blouse with elegant minimal jewelry"
    ),
    BEACH(
        label = "Beach",
        setting = "a wooden boardwalk by the ocean at golden-hour sunset with palm trees and warm light",
        menOutfit = "a relaxed white linen shirt with rolled sleeves and light beige linen trousers",
        womenOutfit = "a flowing light summer dress with delicate gold accessories"
    ),
    EVENING(
        label = "Evening",
        setting = "a rooftop garden lounge at dusk with warm string lights, flowers and a softly lit city view",
        menOutfit = "a cream henley shirt with a leather bracelet and a minimalist watch",
        womenOutfit = "an elegant evening top with statement earrings"
    );

    fun outfitFor(gender: Gender): String = if (gender == Gender.WOMEN) womenOutfit else menOutfit
}

enum class SceneRenderStyle(val label: String) {
    PHOTO("Photo"),
    ANIMATED("3D Animated")
}

object ScenePromptBuilder {

    /**
     * Builds the instruction that places the person from the try-on result into a lifestyle scene,
     * keeping their face and the new hairstyle exactly as they are.
     */
    fun buildScenePrompt(
        hairstyle: HairstyleItem,
        scene: LifestyleScene,
        renderStyle: SceneRenderStyle
    ): String = buildString {
        appendLine("You are a professional lifestyle photographer and portrait artist.")
        appendLine("Using the provided portrait, create a new half-body image of the SAME person in ${scene.setting}.")
        appendLine()
        appendLine("REQUIREMENTS:")
        appendLine("1. IDENTITY: Keep the person recognizably identical: same face, facial structure, eyes, nose, mouth, skin tone, age and expression style. Do not change their ethnicity or proportions.")
        appendLine("2. HAIRSTYLE: Keep their exact current hairstyle (\"${hairstyle.name}\"): same cut, length, texture, volume and hair color. Do not restyle the hair.")
        appendLine("3. OUTFIT: Dress them in ${scene.outfitFor(hairstyle.gender)}.")
        appendLine("4. POSE: Natural, confident, relaxed pose with a friendly smile, looking towards the camera, framed from the waist up.")
        appendLine("5. LIGHTING: Match the lighting of the setting so the person looks naturally part of the scene.")
        when (renderStyle) {
            SceneRenderStyle.PHOTO ->
                appendLine("6. STYLE: Photorealistic, high-end lifestyle photography with shallow depth of field and natural skin texture.")
            SceneRenderStyle.ANIMATED ->
                appendLine("6. STYLE: High-quality 3D animated feature-film character style with soft cinematic lighting, while keeping the person clearly recognizable and the hairstyle faithful.")
        }
        appendLine("7. OUTPUT: A single vertical portrait image. No text, logos or watermarks.")
    }
}
