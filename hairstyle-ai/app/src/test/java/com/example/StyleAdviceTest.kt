package com.example

import com.example.analysis.HairColorAdvisor
import com.example.analysis.StyleAdviceBuilder
import com.example.data.model.Gender
import com.example.data.repository.HairstyleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Unit tests for the AI Face Scan "Personalized Style Advice" logic.
 */
class StyleAdviceTest {

    @Test
    fun rgbToLab_matchesReferenceWhite() {
        val (l, a, b) = HairColorAdvisor.rgbToLab(255, 255, 255)
        assertTrue("White L* should be ~100, was $l", abs(l - 100f) < 0.5f)
        assertTrue(abs(a) < 0.5f)
        assertTrue(abs(b) < 0.5f)
    }

    @Test
    fun classify_fairGoldenSkin_isLightWarm() {
        val tone = HairColorAdvisor.classify(241, 214, 180)
        assertEquals(HairColorAdvisor.SkinDepth.LIGHT, tone.depth)
        assertEquals(HairColorAdvisor.Undertone.WARM, tone.undertone)
    }

    @Test
    fun classify_rosySkin_isCool() {
        val tone = HairColorAdvisor.classify(190, 140, 130)
        assertEquals(HairColorAdvisor.Undertone.COOL, tone.undertone)
    }

    @Test
    fun classify_deepSkin_isDeep() {
        val tone = HairColorAdvisor.classify(90, 55, 40)
        assertEquals(HairColorAdvisor.SkinDepth.DEEP, tone.depth)
    }

    @Test
    fun recommend_alwaysReturnsFiveSwatchesAndFourTips() {
        val tones = listOf(null) + HairColorAdvisor.Undertone.entries.flatMap { u ->
            listOf(
                HairColorAdvisor.classify(241, 214, 180).copy(undertone = u, depth = HairColorAdvisor.SkinDepth.LIGHT),
                HairColorAdvisor.classify(198, 145, 110).copy(undertone = u, depth = HairColorAdvisor.SkinDepth.MEDIUM),
                HairColorAdvisor.classify(90, 55, 40).copy(undertone = u, depth = HairColorAdvisor.SkinDepth.DEEP)
            )
        }
        tones.forEach { tone ->
            val analysis = HairColorAdvisor.recommend(tone)
            assertEquals(5, analysis.swatches.size)
            assertEquals(4, analysis.tips.size)
        }
    }

    @Test
    fun recommendStyles_prefersFaceShapeMatchesForGender() {
        val catalog = HairstyleRepository.getAllHairstyles()
        val styles = StyleAdviceBuilder.recommendStyles(
            faceShape = "Diamond",
            suitableTypes = listOf("Textured Crop", "Side Swept Fringe"),
            gender = Gender.MEN,
            catalog = catalog
        )
        assertTrue(styles.size <= StyleAdviceBuilder.RECOMMENDATION_COUNT)
        assertTrue(styles.isNotEmpty())
        assertTrue(styles.all { it.gender == Gender.MEN })
        assertEquals("No duplicate thumbnails", styles.size, styles.map { it.imageRes }.distinct().size)

        val diamondCount = catalog.count { item ->
            item.gender == Gender.MEN && item.faceShapes.any { it.equals("Diamond", ignoreCase = true) }
        }
        if (diamondCount > 0) {
            assertTrue(styles.first().faceShapes.any { it.equals("Diamond", ignoreCase = true) })
        }
    }
}
