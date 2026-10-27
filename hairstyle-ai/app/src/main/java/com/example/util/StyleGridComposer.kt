package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Renders the "4 AI Looks" results into a single shareable 2x2 collage with style name labels.
 */
object StyleGridComposer {

    private const val CELL_WIDTH = 540
    private const val CELL_HEIGHT = 720
    private const val GAP = 8
    private const val FOOTER_HEIGHT = 72

    fun compose(looks: List<Pair<Bitmap, String>>): Bitmap? {
        if (looks.isEmpty()) return null
        val cells = looks.take(4)
        val columns = if (cells.size == 1) 1 else 2
        val rows = (cells.size + columns - 1) / columns
        val width = columns * CELL_WIDTH + (columns + 1) * GAP
        val height = rows * CELL_HEIGHT + (rows + 1) * GAP + FOOTER_HEIGHT

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawColor(Color.parseColor("#0C0E17"))

        val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        cells.forEachIndexed { i, (bitmap, name) ->
            val left = GAP + (i % columns) * (CELL_WIDTH + GAP)
            val top = GAP + (i / columns) * (CELL_HEIGHT + GAP)
            val dst = Rect(left, top, left + CELL_WIDTH, top + CELL_HEIGHT)
            canvas.drawBitmap(bitmap, centerCropSource(bitmap, CELL_WIDTH, CELL_HEIGHT), dst, imagePaint)

            // Bottom scrim + label
            val scrimTop = (dst.bottom - CELL_HEIGHT * 0.22f)
            val scrim = Paint().apply {
                shader = LinearGradient(
                    0f, scrimTop, 0f, dst.bottom.toFloat(),
                    Color.TRANSPARENT, Color.argb(190, 0, 0, 0), Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(RectF(dst.left.toFloat(), scrimTop, dst.right.toFloat(), dst.bottom.toFloat()), scrim)
            canvas.drawText(ellipsize(name, labelPaint, CELL_WIDTH - 40f), dst.left + 20f, dst.bottom - 24f, labelPaint)
        }

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#C084FC")
            textSize = 30f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("My 4 AI Looks · HairLook", width / 2f, height - FOOTER_HEIGHT / 2f + 10f, footerPaint)
        return output
    }

    /** Source rect that center-crops [bitmap] to the target aspect ratio. */
    internal fun centerCropSource(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Rect =
        centerCropRect(bitmap.width, bitmap.height, targetWidth, targetHeight)

    internal fun centerCropRect(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Rect {
        val scale = max(targetWidth.toFloat() / srcWidth, targetHeight.toFloat() / srcHeight)
        val cropW = (targetWidth / scale).roundToInt().coerceAtMost(srcWidth)
        val cropH = (targetHeight / scale).roundToInt().coerceAtMost(srcHeight)
        val left = (srcWidth - cropW) / 2
        val top = (srcHeight - cropH) / 2
        return Rect(left, top, left + cropW, top + cropH)
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + "…") > maxWidth) end--
        return text.substring(0, end) + "…"
    }
}
