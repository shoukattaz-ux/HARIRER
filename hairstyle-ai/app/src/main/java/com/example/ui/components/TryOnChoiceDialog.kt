package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.api.AiTryOnConfig
import com.example.data.model.HairstyleItem
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Bottom Sheet displaying Two Distinct Try-On Options:
 * 1. Live Camera Try-On
 * 2. Upload Photo (Auto-fit & Finger Controls)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryOnChoiceBottomSheet(
    hairstyle: HairstyleItem?,
    onDismissRequest: () -> Unit,
    onSelectLiveCamera: () -> Unit = {},
    onSelectUploadPhoto: () -> Unit = {},
    isPaidSubscribed: Boolean = false,
    onSelectFree: () -> Unit = onSelectLiveCamera,
    onSelectPaid: () -> Unit = onSelectUploadPhoto
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(TextMuted.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Choose Try-On Mode",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select your preferred try-on experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            if (hairstyle != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ChampagneGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selected Style: ${hairstyle.name} (${hairstyle.category})",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Option 1: Live Camera Try-On Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onDismissRequest()
                        onSelectLiveCamera()
                    }
                    .testTag("choice_live_camera_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ElectricViolet.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, ElectricViolet),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ElectricViolet,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Camera Try-On",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ElectricViolet.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "LIVE AR",
                                    color = ElectricViolet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Real-time front camera AR face tracking with hairline alignment",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Option 2: Upload Photo Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ChampagneGold.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, ChampagneGold.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onDismissRequest()
                        onSelectUploadPhoto()
                    }
                    .testTag("choice_upload_photo_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ChampagneGold.copy(alpha = 0.20f),
                        border = BorderStroke(1.5.dp, ChampagneGold),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Upload Photo",
                                style = MaterialTheme.typography.titleMedium,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ChampagneGold
                            ) {
                                Text(
                                    text = "AUTO-FIT + TOUCH",
                                    color = ObsidianBackground,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Auto-fitted onto portrait with drag, scale & rotate touch controls",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * HairLook Premium upgrade dialog. Price and billing period come from Google Play (localized);
 * the payment itself happens in Google Play's own purchase sheet.
 */
@Composable
fun PaidSubscriptionDialog(
    priceLabel: String?,
    periodLabel: String?,
    isBillingReady: Boolean,
    onDismissRequest: () -> Unit,
    onPurchase: () -> Unit,
    onRestorePurchases: () -> Unit
) {
    val canPurchase = isBillingReady && priceLabel != null
    val priceText = priceLabel ?: "…"

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .border(2.dp, ChampagneGold.copy(alpha = 0.8f), RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crown Badge
                Surface(
                    shape = CircleShape,
                    color = ChampagneGold.copy(alpha = 0.18f),
                    border = BorderStroke(2.dp, ChampagneGold),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "👑",
                            fontSize = 36.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "HairLook Premium",
                    style = MaterialTheme.typography.titleLarge,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "More AI looks, scenes and previews every day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Price Card (localized price from Google Play)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.5.dp, ChampagneGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = priceText,
                                color = ChampagneGold,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (periodLabel != null) {
                                Text(
                                    text = " / $periodLabel",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                        Text(
                            text = if (periodLabel != null) "Cancel anytime in Google Play" else "One-time purchase",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Benefits
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VipBenefitRow(
                        title = "${AiTryOnConfig.PREMIUM_DAILY_TRY_LIMIT} AI previews every day",
                        subtitle = "Instead of ${AiTryOnConfig.FREE_DAILY_TRY_LIMIT} on the free plan"
                    )
                    VipBenefitRow(
                        title = "All four looks in My 4 AI Looks",
                        subtitle = "See every recommended style on your own photo"
                    )
                    VipBenefitRow(
                        title = "Style Score scenes",
                        subtitle = "Office, Beach and Evening, as photos or 3D animated"
                    )
                    VipBenefitRow(
                        title = "Secure checkout with Google Play",
                        subtitle = "Payment is handled by Google; HairLook never sees your card"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPurchase,
                    enabled = canPurchase,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("pay_two_dollars_btn")
                ) {
                    if (canPurchase) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google Play",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        CircularProgressIndicator(
                            color = ObsidianBackground,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Connecting to Google Play…",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (periodLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Renews automatically at $priceText per $periodLabel until you cancel. " +
                            "Cancel at least 24 hours before renewal in Google Play → Subscriptions.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onRestorePurchases) {
                        Text(
                            text = "Restore Purchase",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = "Maybe Later",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VipBenefitRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = SuccessGreen.copy(alpha = 0.18f),
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        }
    }
}
