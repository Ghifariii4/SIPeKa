package com.smkn8jkt.sipeka.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.smkn8jkt.sipeka.R
import com.smkn8jkt.sipeka.ui.theme.BgDarkEspresso
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.OutlineWarm
import com.smkn8jkt.sipeka.ui.theme.PlaceholderWarm
import com.smkn8jkt.sipeka.ui.theme.TextMuted

/**
 * Komponen Lottie Animasi untuk Status Kosong (Empty State)
 * Mengikuti Aturan UI & UX Master SIPeKa:
 * - 8pt Grid (multiples of 8/4dp)
 * - Empathetic copy dengan minimum font size 14.sp
 * - Actionable CTA dengan minimum touch target 48x48.dp
 */
@Composable
fun SipekaLottieEmptyState(
    title: String = "Tidak Ada Data",
    description: String = "",
    modifier: Modifier = Modifier,
    rawResId: Int = R.raw.lottie_empty,
    animationRes: Int = rawResId,
    message: String = description,
    actionButtonText: String? = null,
    ctaText: String? = actionButtonText,
    onActionClick: (() -> Unit)? = null,
    onCtaClick: (() -> Unit)? = onActionClick
) {
    val finalRes = if (animationRes != R.raw.lottie_empty) animationRes else rawResId
    val finalDescription = if (description.isNotBlank()) description else message
    val finalButtonText = actionButtonText ?: ctaText
    val finalOnClick = onActionClick ?: onCtaClick

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(finalRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardCreamWhite,
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(BgWarmTan.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BgDarkEspresso,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = finalDescription,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            if (!finalButtonText.isNullOrBlank() && finalOnClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = finalOnClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BtnDarkChocolate,
                        contentColor = BtnCreamWhite
                    ),
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(
                        text = finalButtonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BtnCreamWhite
                    )
                }
            }
        }
    }
}

/**
 * Komponen Shimmer / Skeleton Loader untuk Kartu Produk
 * Memenuhi Aturan UX Constraint 3 (Perceived Performance & Skeleton Loaders):
 * "Display blank, greyed-out rounded rectangles holding the shape of the data while isLoading == true"
 */
@Composable
fun SipekaProductSkeletonCard(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "ShimmerSkeleton")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skeleton Image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlaceholderWarm.copy(alpha = alphaAnim))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Skeleton Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlaceholderWarm.copy(alpha = alphaAnim))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton Subtitle / Category
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlaceholderWarm.copy(alpha = alphaAnim))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton Price & Badge
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PlaceholderWarm.copy(alpha = alphaAnim))
                    )
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PlaceholderWarm.copy(alpha = alphaAnim))
                    )
                }
            }
        }
    }
}
