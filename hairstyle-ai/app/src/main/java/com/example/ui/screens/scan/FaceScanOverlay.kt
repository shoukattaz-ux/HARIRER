package com.example.ui.screens.scan

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricVioletSoft
import kotlin.math.max

/**
 * Face shapes shown on the scanner rail, in the order they are cycled.
 * Names match [com.example.analysis.FaceShapeAnalyzer] results.
 */
enum class ScanFaceShape(val label: String) {
    DIAMOND("Diamond"),
    SQUARE("Square"),
    OBLONG("Oblong"),
    OVAL("Oval"),
    ROUND("Round"),
    HEART("Heart");

    companion object {
        fun fromLabel(label: String?): ScanFaceShape? =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) }
    }
}

private val OvalStroke = Color(0xFFFFFDF2)
private val RailChipBg = Color(0xCC9CA0A8)
private val RailIconColor = Color(0xFFF5F5F5)

/**
 * Maps points from bitmap pixel space into the on-screen space of an image drawn with
 * ContentScale.Crop centered in a [canvasSize] box.
 */
internal fun cropMapper(bitmapWidth: Int, bitmapHeight: Int, canvasSize: Size): (Offset) -> Offset {
    val scale = max(canvasSize.width / bitmapWidth, canvasSize.height / bitmapHeight)
    val dx = (canvasSize.width - bitmapWidth * scale) / 2f
    val dy = (canvasSize.height - bitmapHeight * scale) / 2f
    return { p -> Offset(p.x * scale + dx, p.y * scale + dy) }
}

/**
 * Builds a smooth closed path through [points] using midpoint quadratic curves.
 */
internal fun smoothClosedPath(points: List<Offset>): Path {
    val path = Path()
    if (points.size < 3) return path
    fun mid(a: Offset, b: Offset) = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)
    val start = mid(points.last(), points.first())
    path.moveTo(start.x, start.y)
    for (i in points.indices) {
        val p = points[i]
        val next = points[(i + 1) % points.size]
        val m = mid(p, next)
        path.quadraticBezierTo(p.x, p.y, m.x, m.y)
    }
    path.close()
    return path
}

/**
 * Draws the scanning effects over the photo:
 *  - a sweeping scan line while the face is being located ([isSearching])
 *  - the face contour oval traced progressively ([traceProgress] 0..1) with a glowing head dot
 *
 * [contour] is in bitmap pixel coordinates; when empty an oval derived from [faceBounds] is used.
 */
@Composable
fun FaceScanCanvas(
    bitmapWidth: Int,
    bitmapHeight: Int,
    contour: List<Offset>,
    faceBounds: androidx.compose.ui.geometry.Rect?,
    traceProgress: Float,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "scan_line")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "scan_sweep"
    )
    val measure = remember { PathMeasure() }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (isSearching) {
            val y = size.height * sweep
            val band = size.height * 0.08f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, ElectricVioletSoft.copy(alpha = 0.35f), Color.Transparent),
                    startY = y - band,
                    endY = y + band
                ),
                topLeft = Offset(0f, y - band),
                size = Size(size.width, band * 2)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        if (traceProgress <= 0f || bitmapWidth <= 0 || bitmapHeight <= 0) return@Canvas
        val map = cropMapper(bitmapWidth, bitmapHeight, size)

        val fullPath = if (contour.size >= 3) {
            smoothClosedPath(contour.map(map))
        } else if (faceBounds != null) {
            val tl = map(faceBounds.topLeft)
            val br = map(faceBounds.bottomRight)
            Path().apply { addOval(androidx.compose.ui.geometry.Rect(tl, br)) }
        } else {
            return@Canvas
        }

        measure.setPath(fullPath, false)
        val length = measure.length
        if (length <= 0f) return@Canvas
        val traced = Path()
        val end = length * traceProgress.coerceIn(0f, 1f)
        measure.getSegment(0f, end, traced, true)

        // Soft glow + crisp line, like the reference scan
        drawPath(traced, Color.White.copy(alpha = 0.28f), style = Stroke(width = 10.dp.toPx()))
        drawPath(traced, OvalStroke, style = Stroke(width = 3.5.dp.toPx()))

        if (traceProgress < 1f) {
            val head = measure.getPosition(end)
            drawCircle(ElectricVioletSoft.copy(alpha = 0.45f), radius = 10.dp.toPx(), center = head)
            drawCircle(Color.White, radius = 4.dp.toPx(), center = head)
        }
    }
}

/**
 * Vertical rail of face shape chips. [highlighted] is the chip currently lit;
 * [locked] makes the highlight pop to indicate the final detected shape.
 */
@Composable
fun FaceShapeRail(
    highlighted: ScanFaceShape?,
    locked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ScanFaceShape.entries.forEach { shape ->
            val isActive = shape == highlighted
            val bg by animateColorAsState(
                targetValue = if (isActive) ElectricVioletSoft else RailChipBg,
                animationSpec = tween(140),
                label = "rail_bg"
            )
            val scale by animateFloatAsState(
                targetValue = if (isActive && locked) 1.18f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "rail_scale"
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(bg)
                    .border(
                        width = if (isActive && locked) 2.dp else 0.dp,
                        color = if (isActive && locked) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .semantics { contentDescription = "${shape.label} face shape" },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(22.dp)) {
                    drawShapeIcon(shape, RailIconColor, Stroke(width = 2.dp.toPx()))
                }
            }
        }
    }
}

private fun DrawScope.drawShapeIcon(shape: ScanFaceShape, color: Color, stroke: Stroke) {
    val w = size.width
    val h = size.height
    when (shape) {
        ScanFaceShape.OVAL -> drawOval(
            color = color,
            topLeft = Offset(w * 0.22f, h * 0.04f),
            size = Size(w * 0.56f, h * 0.92f),
            style = stroke
        )
        ScanFaceShape.ROUND -> drawCircle(color = color, radius = w * 0.44f, style = stroke)
        ScanFaceShape.SQUARE -> drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.84f, h * 0.84f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke
        )
        ScanFaceShape.OBLONG -> drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.24f, h * 0.02f),
            size = Size(w * 0.52f, h * 0.96f),
            cornerRadius = CornerRadius(w * 0.06f),
            style = stroke
        )
        ScanFaceShape.DIAMOND -> drawPath(
            Path().apply {
                moveTo(w * 0.5f, h * 0.02f)
                lineTo(w * 0.96f, h * 0.5f)
                lineTo(w * 0.5f, h * 0.98f)
                lineTo(w * 0.04f, h * 0.5f)
                close()
            },
            color = color,
            style = stroke
        )
        ScanFaceShape.HEART -> drawPath(
            Path().apply {
                moveTo(w * 0.5f, h * 0.92f)
                cubicTo(w * 0.12f, h * 0.64f, w * 0.0f, h * 0.36f, w * 0.24f, h * 0.18f)
                cubicTo(w * 0.38f, h * 0.08f, w * 0.5f, h * 0.2f, w * 0.5f, h * 0.3f)
                cubicTo(w * 0.5f, h * 0.2f, w * 0.62f, h * 0.08f, w * 0.76f, h * 0.18f)
                cubicTo(w * 1.0f, h * 0.36f, w * 0.88f, h * 0.64f, w * 0.5f, h * 0.92f)
                close()
            },
            color = color,
            style = stroke
        )
    }
}
