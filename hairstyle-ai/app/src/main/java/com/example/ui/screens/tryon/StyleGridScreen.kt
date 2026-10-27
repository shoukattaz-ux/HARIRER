package com.example.ui.screens.tryon

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.AiTryOnConfig
import com.example.data.model.GridTileState
import com.example.data.model.HairstyleItem
import com.example.data.model.StyleGridState
import com.example.data.model.UserPhotoSource
import com.example.engine.HairstyleOverlayEngine
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
import com.example.util.StyleGridComposer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

private const val THUMBNAIL_MAX_DIMENSION = 720

/**
 * "My 4 AI Looks" — the user's photo restyled with four recommended hairstyles in a 2x2 grid.
 * Tiles fill in as each generation finishes; finished looks open in the regular result screen
 * and can be shared together as one collage.
 */
@Composable
fun StyleGridScreen(
    gridState: StyleGridState,
    userPhoto: UserPhotoSource?,
    isPaidVersion: Boolean,
    remainingFreeQuota: Int,
    maxDailyQuota: Int,
    onOpenResult: (Int) -> Unit,
    onRetryTile: (Int) -> Unit,
    onUnlockLocked: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSharing by remember { mutableStateOf(false) }
    var shareMessage by remember { mutableStateOf<String?>(null) }

    // Original photo shown behind tiles that are not ready yet
    val originalPhoto by produceState<Bitmap?>(initialValue = null, userPhoto) {
        value = userPhoto?.let { source ->
            withContext(Dispatchers.IO) {
                HairstyleOverlayEngine.resolvePhotoToBitmap(context, source)?.let { thumbnail(it) }
            }
        }
    }

    fun shareLooks() {
        if (isSharing) return
        val looks = gridState.tiles.mapIndexedNotNull { i, tile ->
            (tile as? GridTileState.Ready)?.let { it.bitmap to gridState.styles[i].name }
        }
        scope.launch {
            isSharing = true
            shareMessage = null
            try {
                val collage = withContext(Dispatchers.Default) { StyleGridComposer.compose(looks) }
                val uri = collage?.let {
                    withContext(Dispatchers.IO) {
                        GallerySaver.saveBitmapToGallery(context, it, "HairLook_4Looks_${System.currentTimeMillis()}")
                    }
                }
                if (uri != null) {
                    GallerySaver.shareImage(context, uri)
                } else {
                    shareMessage = "Couldn't save your looks. Please check storage access and try again."
                }
            } catch (e: Exception) {
                shareMessage = "Couldn't create the image. Please try again."
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
                    .testTag("style_grid_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "My 4 AI Looks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isPaidVersion) "Premium · all looks unlocked"
                    else "Free previews left today: $remainingFreeQuota/$maxDailyQuota",
                    style = MaterialTheme.typography.bodySmall,
                    color = HairLookPurpleGradientEnd
                )
            }
        }

        if (gridState.styles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Run an AI Face Scan first to pick the four styles that suit you best.",
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
            val total = gridState.tiles.size
            val finished = gridState.tiles.count { it is GridTileState.Ready || it is GridTileState.Failed || it is GridTileState.Locked }
            Text(
                text = if (gridState.isRunning) "Styling your looks… ${gridState.readyCount} of $total ready"
                else "${gridState.readyCount} of $total looks ready · tap a look to open it",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (gridState.isRunning) {
                LinearProgressIndicator(
                    progress = { if (total == 0) 0f else finished.toFloat() / total },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ElectricVioletSoft,
                    trackColor = HairLookCardBorder
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            gridState.tiles.indices.chunked(2).forEach { rowIndices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowIndices.forEach { index ->
                        StyleGridTile(
                            style = gridState.styles[index],
                            tile = gridState.tiles[index],
                            originalPhoto = originalPhoto,
                            canRetry = !gridState.isRunning,
                            onOpen = { onOpenResult(index) },
                            onRetry = { onRetryTile(index) },
                            onUnlock = onUnlockLocked,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("style_grid_tile_$index")
                        )
                    }
                    if (rowIndices.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (gridState.lockedCount > 0 && !gridState.isRunning) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HairLookCardBg)
                        .border(1.dp, HairLookPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "${gridState.lockedCount} look${if (gridState.lockedCount > 1) "s" else ""} locked",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPaidVersion) "Generate the remaining looks now."
                        else "The free plan includes $maxDailyQuota AI previews a day. Upgrade to see all four looks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onUnlockLocked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("style_grid_unlock_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isPaidVersion) "Generate Remaining Looks" else "Unlock All Looks")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = { shareLooks() },
                enabled = gridState.readyCount > 0 && !isSharing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("style_grid_share_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricVioletSoft)
            ) {
                if (isSharing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share My Looks")
            }
            shareMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextMuted, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = AiTryOnConfig.HAIRSTYLE_DISCLAIMER,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StyleGridTile(
    style: HairstyleItem,
    tile: GridTileState,
    originalPhoto: Bitmap?,
    canRetry: Boolean,
    onOpen: () -> Unit,
    onRetry: () -> Unit,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(shape)
            .background(Color(0xFF1A1D2B))
            .border(
                1.dp,
                if (tile is GridTileState.Ready) ElectricVioletSoft.copy(alpha = 0.7f) else HairLookCardBorder,
                shape
            )
            .clickable(enabled = tile is GridTileState.Ready, onClick = onOpen)
    ) {
        val readyBitmap = (tile as? GridTileState.Ready)?.bitmap
        Crossfade(targetState = readyBitmap, animationSpec = tween(500), label = "grid_tile_image") { result ->
            val shown = result ?: originalPhoto
            if (shown != null) {
                val image = remember(shown) { shown.asImageBitmap() }
                Image(
                    bitmap = image,
                    contentDescription = if (result != null) "${style.name} look" else "Your photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (result != null) 1f else 0.4f)
                )
            }
        }

        when (tile) {
            GridTileState.Queued -> TileCenterMessage(text = "In queue")
            GridTileState.Generating -> {
                ShimmerSweep()
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Styling…", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
            is GridTileState.Failed -> Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tile.error.title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onRetry,
                    enabled = canRetry,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retry", fontSize = 12.sp)
                }
            }
            GridTileState.Locked -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(onClick = onUnlock),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Premium look", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
            is GridTileState.Ready -> if (tile.isAiGenerated) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricVioletSoft, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("AI", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Style name label
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = style.name,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TileCenterMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ShimmerSweep() {
    val transition = rememberInfiniteTransition(label = "grid_shimmer")
    val progress by transition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "grid_shimmer_progress"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.Transparent, ElectricVioletSoft.copy(alpha = 0.28f), Color.Transparent),
                    start = Offset(progress * 600f - 200f, progress * 800f - 260f),
                    end = Offset(progress * 600f + 200f, progress * 800f + 260f)
                )
            )
    )
}

private fun thumbnail(bitmap: Bitmap): Bitmap {
    val largest = max(bitmap.width, bitmap.height)
    if (largest <= THUMBNAIL_MAX_DIMENSION) return bitmap
    val ratio = THUMBNAIL_MAX_DIMENSION.toFloat() / largest
    return Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * ratio).toInt().coerceAtLeast(1),
        (bitmap.height * ratio).toInt().coerceAtLeast(1),
        true
    )
}
