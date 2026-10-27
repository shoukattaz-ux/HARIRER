package com.example.ui.screens.tryon

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analysis.StyleScoreCalculator
import com.example.data.api.AiTryOnConfig
import com.example.data.api.LifestyleScene
import com.example.data.api.SceneRenderStyle
import com.example.data.model.GridTileState
import com.example.data.model.HairstyleItem
import com.example.data.model.SceneStudioState
import com.example.ui.theme.ElectricVioletSoft
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GallerySaver
import com.example.util.StyleStoryComposer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ScoreGradient = Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFFA855F7)))
private val ScoreTextShadow = Shadow(color = Color.Black.copy(alpha = 0.55f), offset = Offset(0f, 3f), blurRadius = 10f)

/**
 * Style Score — an explainable 70–99 rating of the current look, plus AI lifestyle scenes
 * (Office / Beach / Evening, photo or 3D animated) and a shareable 9:16 story image.
 */
@Composable
fun StyleScoreScreen(
    hairstyle: HairstyleItem?,
    lookBitmap: Bitmap?,
    styleScore: StyleScoreCalculator.StyleScore?,
    sceneStudio: SceneStudioState,
    isPaidVersion: Boolean,
    remainingFreeQuota: Int,
    maxDailyQuota: Int,
    onGenerateScene: (LifestyleScene, SceneRenderStyle) -> Unit,
    onUpgrade: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedScene by remember { mutableStateOf<LifestyleScene?>(null) }
    var renderStyle by remember { mutableStateOf(SceneRenderStyle.PHOTO) }
    var isSharing by remember { mutableStateOf(false) }
    var shareMessage by remember { mutableStateOf<String?>(null) }

    val sceneTile: GridTileState? = selectedScene?.let { sceneStudio.scenes[SceneStudioState.key(it, renderStyle)] }
    val heroBitmap = (sceneTile as? GridTileState.Ready)?.bitmap ?: lookBitmap

    fun shareStory() {
        val image = heroBitmap ?: return
        val score = styleScore ?: return
        if (isSharing) return
        scope.launch {
            isSharing = true
            shareMessage = null
            try {
                val sceneLabel = selectedScene?.takeIf { sceneTile is GridTileState.Ready }?.label
                val story = withContext(Dispatchers.Default) {
                    StyleStoryComposer.compose(image, score.score, hairstyle?.name ?: "My new look", sceneLabel)
                }
                val uri = withContext(Dispatchers.IO) {
                    GallerySaver.saveBitmapToGallery(context, story, "HairLook_StyleScore_${System.currentTimeMillis()}")
                }
                if (uri != null) {
                    GallerySaver.shareImage(context, uri)
                } else {
                    shareMessage = "Couldn't save your story. Please check storage access and try again."
                }
            } catch (e: Exception) {
                shareMessage = "Couldn't create the story image. Please try again."
            } finally {
                isSharing = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(HairLookCardBg)
                    .border(1.dp, HairLookCardBorder, CircleShape)
                    .testTag("style_score_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Style Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = hairstyle?.name ?: "Your look",
                    style = MaterialTheme.typography.bodySmall,
                    color = HairLookPurpleGradientEnd
                )
            }
        }

        if (lookBitmap == null || styleScore == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create an AI try-on first, then come back to see your Style Score.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Hero: look or scene with the animated score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 13f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A1D2B))
                    .testTag("style_score_hero")
            ) {
                Crossfade(targetState = heroBitmap, animationSpec = tween(600), label = "score_hero") { bmp ->
                    if (bmp != null) {
                        val image = remember(bmp) { bmp.asImageBitmap() }
                        Image(
                            bitmap = image,
                            contentDescription = hairstyle?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)))
                )
                if (sceneTile == GridTileState.Generating) {
                    SceneGeneratingOverlay(selectedScene?.label.orEmpty())
                }
                AnimatedScoreBadge(
                    score = styleScore.score,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 14.dp, end = 16.dp)
                )
                Text(
                    text = listOfNotNull(
                        hairstyle?.name,
                        selectedScene?.takeIf { sceneTile is GridTileState.Ready }?.label
                    ).joinToString(" · "),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "See yourself in…",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreChip("Original", selectedScene == null) { selectedScene = null }
                LifestyleScene.entries.forEach { scene ->
                    ScoreChip(scene.label, selectedScene == scene) { selectedScene = scene }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SceneRenderStyle.entries.forEach { style ->
                    ScoreChip(style.label, renderStyle == style) { renderStyle = style }
                }
            }

            selectedScene?.let { scene ->
                Spacer(modifier = Modifier.height(8.dp))
                SceneAction(
                    scene = scene,
                    renderStyle = renderStyle,
                    tile = sceneTile,
                    isBusy = sceneStudio.isGenerating,
                    isPaidVersion = isPaidVersion,
                    remainingFreeQuota = remainingFreeQuota,
                    maxDailyQuota = maxDailyQuota,
                    onGenerate = { onGenerateScene(scene, renderStyle) },
                    onUpgrade = onUpgrade
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            ScoreBreakdownCard(styleScore)

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { shareStory() },
                enabled = !isSharing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("style_score_share_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricVioletSoft)
            ) {
                if (isSharing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share My Style Story", fontWeight = FontWeight.Bold)
            }
            shareMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextMuted, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Style Score is a fun styling estimate based on face shape fit, color harmony, trends and upkeep. " +
                        AiTryOnConfig.HAIRSTYLE_DISCLAIMER,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnimatedScoreBadge(score: Int, modifier: Modifier = Modifier) {
    val count = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(score) {
        count.snapTo(0f)
        count.animateTo(score.toFloat(), tween(1300, easing = FastOutSlowInEasing))
        pop.snapTo(1.18f)
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        val labelStyle = TextStyle(
            color = Color.White,
            fontSize = 26.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            shadow = ScoreTextShadow
        )
        Text("STYLE", style = labelStyle)
        Text("SCORE", style = labelStyle)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (count.value >= score) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = count.value.toInt().toString(),
                modifier = Modifier
                    .scale(pop.value)
                    .testTag("style_score_value"),
                style = TextStyle(
                    brush = ScoreGradient,
                    fontSize = 72.sp,
                    lineHeight = 76.sp,
                    fontWeight = FontWeight.Black,
                    shadow = ScoreTextShadow
                )
            )
        }
    }
}

@Composable
private fun SceneGeneratingOverlay(sceneLabel: String) {
    val transition = rememberInfiniteTransition(label = "scene_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "scene_shimmer_alpha"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElectricVioletSoft.copy(alpha = alpha * 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Creating your $sceneLabel scene…",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SceneAction(
    scene: LifestyleScene,
    renderStyle: SceneRenderStyle,
    tile: GridTileState?,
    isBusy: Boolean,
    isPaidVersion: Boolean,
    remainingFreeQuota: Int,
    maxDailyQuota: Int,
    onGenerate: () -> Unit,
    onUpgrade: () -> Unit
) {
    val costNote = "Uses 1 AI preview ($remainingFreeQuota/$maxDailyQuota left today${if (isPaidVersion) " · Premium" else ""})"
    when (tile) {
        null -> Column {
            Button(
                onClick = onGenerate,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("style_score_generate_scene_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create ${scene.label} Scene (${renderStyle.label})")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(costNote, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        GridTileState.Generating, GridTileState.Queued -> Text(
            text = "Creating your ${scene.label.lowercase()} scene. This can take up to a minute.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        is GridTileState.Failed -> Column {
            Text(tile.message, style = MaterialTheme.typography.bodySmall, color = Color(0xFFF87171))
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onGenerate,
                enabled = !isBusy,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Try Again")
            }
        }
        GridTileState.Locked -> Column {
            Text(
                text = if (isPaidVersion) "You've used today's $maxDailyQuota Premium AI previews. They reset at midnight."
                else "You've used today's $maxDailyQuota free AI previews. Premium includes ${AiTryOnConfig.PREMIUM_DAILY_TRY_LIMIT} a day.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = if (isPaidVersion) onGenerate else onUpgrade,
                enabled = !isBusy,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isPaidVersion) "Try Again" else "Upgrade to Premium")
            }
        }
        is GridTileState.Ready -> Unit
    }
}

@Composable
private fun ScoreBreakdownCard(styleScore: StyleScoreCalculator.StyleScore) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(HairLookCardBg)
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = styleScore.headline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${styleScore.score}/${StyleScoreCalculator.MAX_SCORE}",
                style = TextStyle(brush = ScoreGradient, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        styleScore.factors.forEach { factor ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = factor.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(factor.value * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { factor.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ElectricVioletSoft,
                trackColor = HairLookCardBorder
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(factor.detail, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ScoreChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = HairLookPurple,
            selectedLabelColor = Color.White,
            labelColor = TextSecondary
        )
    )
}
