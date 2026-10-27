package com.example

import com.example.analysis.HairColorAdvisor
import com.example.analysis.StyleScoreCalculator
import com.example.data.api.LifestyleScene
import com.example.data.api.SceneRenderStyle
import com.example.data.api.ScenePromptBuilder
import com.example.data.model.Gender
import com.example.data.repository.HairstyleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the Style Score and lifestyle scene prompts.
 */
class StyleScoreTest {

    private val catalog = HairstyleRepository.getAllHairstyles()
    private val style = catalog.first { it.faceShapes.isNotEmpty() }
    private val matchingShape = style.faceShapes.first()
    private val otherShape = listOf("Oval", "Round", "Square", "Heart", "Oblong", "Diamond")
        .firstOrNull { shape -> style.faceShapes.none { it.equals(shape, ignoreCase = true) } }

    @Test
    fun score_isAlwaysWithinRange() {
        catalog.forEach { item ->
            listOf(null, "Oval", "Diamond").forEach { shape ->
                listOf(null, *HairColorAdvisor.Undertone.entries.toTypedArray()).forEach { tone ->
                    listOf(null, "Natural Black", "Honey Blonde", "Ash Grey", "Burgundy").forEach { tint ->
                        val score = StyleScoreCalculator.calculate(item, shape, tone, tint).score
                        assertTrue("Score $score out of range", score in StyleScoreCalculator.MIN_SCORE..StyleScoreCalculator.MAX_SCORE)
                    }
                }
            }
        }
    }

    @Test
    fun score_isDeterministic() {
        val a = StyleScoreCalculator.calculate(style, matchingShape, HairColorAdvisor.Undertone.WARM, "Honey Blonde")
        val b = StyleScoreCalculator.calculate(style, matchingShape, HairColorAdvisor.Undertone.WARM, "Honey Blonde")
        assertEquals(a, b)
    }

    @Test
    fun faceShapeMatch_scoresHigherThanMismatch() {
        val mismatch = otherShape ?: return
        val match = StyleScoreCalculator.calculate(style, matchingShape, null, null).score
        val noMatch = StyleScoreCalculator.calculate(style, mismatch, null, null).score
        assertTrue("match=$match should beat mismatch=$noMatch", match > noMatch)
    }

    @Test
    fun colorMatchingUndertone_scoresHigherThanClash() {
        val warmOnWarm = StyleScoreCalculator.calculate(style, matchingShape, HairColorAdvisor.Undertone.WARM, "Honey Blonde")
        val coolOnWarm = StyleScoreCalculator.calculate(style, matchingShape, HairColorAdvisor.Undertone.WARM, "Ash Grey")
        assertTrue(warmOnWarm.score > coolOnWarm.score)
        assertEquals(1f, warmOnWarm.factors.first { it.label == "Color harmony" }.value)
    }

    @Test
    fun breakdown_hasFourFactorsWithinUnitRange() {
        val score = StyleScoreCalculator.calculate(style, matchingShape, HairColorAdvisor.Undertone.COOL, "Burgundy")
        assertEquals(4, score.factors.size)
        assertTrue(score.factors.all { it.value in 0f..1f })
    }

    @Test
    fun headline_followsScoreBands() {
        assertEquals("Iconic look", StyleScoreCalculator.headline(99))
        assertEquals("Stunning match", StyleScoreCalculator.headline(92))
        assertEquals("Bold choice", StyleScoreCalculator.headline(70))
    }

    @Test
    fun scenePrompt_preservesIdentityAndHairstyle_andUsesGenderOutfit() {
        val woman = catalog.first { it.gender == Gender.WOMEN }
        val prompt = ScenePromptBuilder.buildScenePrompt(woman, LifestyleScene.BEACH, SceneRenderStyle.ANIMATED)
        assertTrue(prompt.contains(woman.name))
        assertTrue(prompt.contains("SAME person"))
        assertTrue(prompt.contains(LifestyleScene.BEACH.womenOutfit))
        assertTrue(prompt.contains("3D animated"))
    }
}
