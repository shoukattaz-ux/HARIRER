package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface

/**
 * Renders a 9:16 "Style Score" story image (1080x1920) for Instagram/WhatsApp/TikTok stories:
 * full-bleed look or scene, big gradient score in the top-right corner, style name at the bottom.
 */
object StyleStoryComposer {

    private const val WIDTH = 1080
    private const val HEIGHT = 1920

    fun compose(image: Bitmap, score: Int, styleName: String, sceneLabel: String?): Bitmap {
        val output = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawColor(Color.BLACK)

        val src = StyleGridComposer.centerCropRect(image.width, image.height, WIDTH, HEIGHT)
        canvas.drawBitmap(image, src, Rect(0, 0, WIDTH, HEIGHT), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))

        // Scrims keep text readable on any background
        drawVerticalScrim(canvas, 0f, 560f, Color.argb(150, 0, 0, 0), Color.TRANSPARENT)
        drawVerticalScrim(canvas, HEIGHT - 520f, HEIGHT.toFloat(), Color.TRANSPARENT, Color.argb(200, 0, 0, 0))

        val right = WIDTH - 72f
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 84f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(12f, 0f, 4f, Color.argb(160, 0, 0, 0))
        }
        canvas.drawText("STYLE", right, 190f, labelPaint)
        canvas.drawText("SCORE", right, 280f, labelPaint)

        val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 260f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            shader = LinearGradient(
                right - 320f, 300f, right, 520f,
                Color.parseColor("#60A5FA"), Color.parseColor("#A855F7"),
                Shader.TileMode.CLAMP
            )
            setShadowLayer(24f, 0f, 6f, Color.argb(170, 40, 20, 90))
        }
        canvas.drawText(score.toString(), right, 520f, scorePaint)

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 64f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(220, 255, 255, 255)
            textSize = 40f
        }
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#C084FC")
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(styleName, 72f, HEIGHT - 260f, namePaint)
        sceneLabel?.let { canvas.drawText("$it look", 72f, HEIGHT - 196f, subPaint) }
        canvas.drawText("Rated by HairLook AI", 72f, HEIGHT - 110f, brandPaint)
        return output
    }

    private fun drawVerticalScrim(canvas: Canvas, top: Float, bottom: Float, topColor: Int, bottomColor: Int) {
        val paint = Paint().apply {
            shader = LinearGradient(0f, top, 0f, bottom, topColor, bottomColor, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, top, WIDTH.toFloat(), bottom, paint)
    }
}
