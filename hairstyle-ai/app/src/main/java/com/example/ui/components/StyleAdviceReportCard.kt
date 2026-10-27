package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analysis.HairColorAdvisor
import com.example.analysis.StyleAdviceBuilder

// Fixed "printed report" palette so the card looks the same (and shares well) in light and dark themes
private val ReportCream = Color(0xFFFAF6EF)
private val ReportPanel = Color(0xFFFFFDF9)
private val ReportBorder = Color(0xFFE9E1D3)
private val ReportInk = Color(0xFF2B2622)
private val ReportInkMuted = Color(0xFF6B6259)
private val ReportPill = Color(0xFFF1EBE0)

/**
 * "Personalized Style Advice" report: Hair Analysis (recommended cut thumbnails + tips)
 * and Color Analysis (hair color swatches + tips). Designed to be captured as an image and shared.
 */
@Composable
fun StyleAdviceReportCard(
    report: StyleAdviceBuilder.StyleAdviceReport,
    onStyleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ReportCream)
            .padding(horizontal = 16.dp, vertical = 22.dp)
            .testTag("style_advice_report"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Personalized\nStyle Advice",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            color = ReportInk,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.width(48.dp),
            thickness = 1.dp,
            color = ReportInkMuted.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${report.faceShape} face · ${report.confidencePercent}% match · ${report.colorAnalysis.summary}",
            fontSize = 12.sp,
            color = ReportInkMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReportPanel {
            SectionPill("Hair Analysis")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                report.recommendedStyles.forEach { style ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStyleClick(style.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = style.imageRes),
                            contentDescription = style.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.82f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = style.name,
                            fontSize = 9.sp,
                            lineHeight = 11.sp,
                            color = ReportInkMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            BulletList(report.hairTips)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = ReportBorder)
            Spacer(modifier = Modifier.height(16.dp))

            SectionPill("Color Analysis")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                report.colorAnalysis.swatches.forEach { swatch ->
                    HairSwatch(swatch, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            BulletList(report.colorAnalysis.tips)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "HairLook AI · Styling estimate from your photo, analyzed on-device. Lighting can affect color results.",
            fontSize = 10.sp,
            lineHeight = 13.sp,
            color = ReportInkMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReportPanel(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(ReportPanel)
            .border(1.dp, ReportBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun SectionPill(title: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(ReportPill)
            .padding(horizontal = 18.dp, vertical = 5.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = ReportInk
        )
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(ReportInkMuted.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = ReportInk
                )
            }
        }
    }
}

@Composable
private fun HairSwatch(swatch: HairColorAdvisor.HairColorSwatch, modifier: Modifier = Modifier) {
    val base = Color(swatch.argb)
    // Radial highlight gives the flat color a glossy "hair sample" look
    val gloss = Brush.radialGradient(
        colors = listOf(lerp(base, Color.White, 0.35f), base, lerp(base, Color.Black, 0.35f))
    )
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(gloss)
                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = swatch.name,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            color = ReportInkMuted,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}
