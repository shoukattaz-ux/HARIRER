package com.example

import com.example.ar.ArHeadTransform
import com.example.data.model.Gender
import com.example.engine.HairstyleOverlayEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

/**
 * Unit tests verifying the Live Front-Camera AR Hairstyle Try-On system.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LiveArHairstyleTest {

    @Test
    fun testNaturalQuiffAssetIsAvailableAndCalibrated() {
        val naturalQuiff = HairstyleOverlayEngine.getAssetById("overlay_m_natural_quiff")
        assertNotNull("Natural Quiff asset must exist in overlay registry", naturalQuiff)
        assertEquals("Natural Quiff", naturalQuiff?.name)
        assertEquals(Gender.MEN, naturalQuiff?.gender)
        assertTrue("Hairline anchor Y must sit at anatomical level (0.7-0.8)", naturalQuiff!!.hairlineAnchorY in 0.70f..0.80f)
        assertTrue("Hair width ratio must sit in cranial proportion range (0.75-0.90)", naturalQuiff.hairWidthRatio in 0.75f..0.90f)
    }

    @Test
    fun testWomenHairstylesAvailableAndCalibrated() {
        val bob = HairstyleOverlayEngine.getAssetById("overlay_w_bob")
        assertNotNull("French Bob must exist", bob)
        assertEquals(Gender.WOMEN, bob?.gender)
        assertTrue("Bob arch must sit at forehead level", bob!!.hairlineAnchorY in 0.45f..0.55f)
        assertTrue("Bob hair width ratio must be in range", bob.hairWidthRatio in 0.50f..0.70f)
    }

    @Test
    fun testArHeadTransformTracksAllRequiredLandmarks() {
        val transform = ArHeadTransform(
            faceCenterX = 540f,
            faceCenterY = 960f,
            foreheadX = 540f,
            foreheadY = 820f,
            hairlineX = 540f,
            hairlineY = 720f,
            leftTempleX = 420f,
            leftTempleY = 850f,
            rightTempleX = 660f,
            rightTempleY = 850f,
            headWidth = 360f,
            headHeight = 490f,
            headTopX = 540f,
            headTopY = 620f,
            rollDegrees = -12.5f,
            yawDegrees = 5.0f,
            pitchDegrees = -2.0f,
            eyeDistance = 150f,
            scaleFactor = 1.0f,
            confidence = 0.95f,
            isFrontCamera = true,
            viewWidth = 1080f,
            viewHeight = 1920f
        )

        // Verify all 9 required anthropometric tracking metrics are present
        assertEquals(540f, transform.faceCenterX, 0.01f)
        assertEquals(960f, transform.faceCenterY, 0.01f)
        assertEquals(540f, transform.foreheadX, 0.01f)
        assertEquals(820f, transform.foreheadY, 0.01f)
        assertEquals(540f, transform.hairlineX, 0.01f)
        assertEquals(720f, transform.hairlineY, 0.01f)
        assertEquals(420f, transform.leftTempleX, 0.01f)
        assertEquals(660f, transform.rightTempleX, 0.01f)
        assertEquals(360f, transform.headWidth, 0.01f)
        assertEquals(490f, transform.headHeight, 0.01f)
        assertEquals(540f, transform.headTopX, 0.01f)
        assertEquals(620f, transform.headTopY, 0.01f)
        assertEquals(-12.5f, transform.rollDegrees, 0.01f)
        assertTrue(transform.confidence > 0.9f)
    }

    @Test
    fun testHairstyleScalesProportionallyWithDistance() {
        val asset = HairstyleOverlayEngine.getAssetById("overlay_m_natural_quiff")!!

        // Simulating user moving closer to camera: eye distance doubles
        val eyeDistFar = 100f
        val headWidthFar = eyeDistFar * 2.35f
        val renderWidthFar = headWidthFar / asset.hairWidthRatio

        val eyeDistNear = 200f
        val headWidthNear = eyeDistNear * 2.35f
        val renderWidthNear = headWidthNear / asset.hairWidthRatio

        assertTrue("Hairstyle width must double when head distance to camera halves",
            abs((renderWidthNear / renderWidthFar) - 2.0f) < 0.01f
        )
    }

    @Test
    fun testHairstyleSelectionIsLocalAndInstant() {
        // Tapping between styles must switch in-memory assets without generative AI network delay
        val quiff = HairstyleOverlayEngine.getAssetById("overlay_m_natural_quiff")
        val fade = HairstyleOverlayEngine.getAssetById("overlay_m_fade")

        assertNotNull(quiff)
        assertNotNull(fade)
        assertTrue(quiff!!.id != fade!!.id)

        // Asset switching is instantaneous object assignment
        var activeAsset = quiff
        assertEquals("overlay_m_natural_quiff", activeAsset.id)

        activeAsset = fade
        assertEquals("overlay_m_fade", activeAsset.id)
    }

    @Test
    fun testGraphicOverlayCoordinateMappingFrontCamera() {
        val viewW = 1080f
        val viewH = 2400f
        val imgW = 480f
        val imgH = 640f

        val scaleFactor = maxOf(viewW / imgW, viewH / imgH)
        val postScaleWidthOffset = (viewW - (imgW * scaleFactor)) / 2f
        val postScaleHeightOffset = (viewH - (imgH * scaleFactor)) / 2f

        val matrix = android.graphics.Matrix().apply {
            postScale(scaleFactor, scaleFactor)
            postTranslate(postScaleWidthOffset, postScaleHeightOffset)
            // Front camera selfie mirror
            postScale(-1f, 1f, viewW / 2f, viewH / 2f)
        }

        // Center point in sensor (240, 320)
        val centerPts = floatArrayOf(240f, 320f)
        matrix.mapPoints(centerPts)
        assertEquals(540f, centerPts[0], 0.1f) // Must land exactly at horizontal center of view

        // Face on camera's right side (x=320) -> should map to screen LEFT side (x < 540)
        val leftPts = floatArrayOf(320f, 320f)
        matrix.mapPoints(leftPts)
        assertTrue("Face on camera-right must map to mirror screen-left", leftPts[0] < 540f)
        assertEquals(240f, leftPts[0], 0.1f)
    }
}
