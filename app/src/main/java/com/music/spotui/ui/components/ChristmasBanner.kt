
package com.music.spotui.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChristmasBanner(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "christmasSnow")

    val snowOffset = transition.animateFloat(
        initialValue = -12f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "snowOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .height(142.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF124B35),
                        Color(0xFF173D31),
                        Color(0xFF8D1830)
                    )
                )
            )
    ) {
        Text(
            text = "❄     ✦       ❄      ✧",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 14.dp,
                    top = (8 + snowOffset.value).dp
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "🎄 CHRISTMAS SPECIAL",
                color = Color(0xFFFFD98A),
                fontSize = 12.sp
            )

            Text(
                text = "Feel the festive music",
                color = Color.White,
                fontSize = 21.sp
            )

            Text(
                text = "Celebrate the season with PURITY ✨",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("❄", color = Color.White, fontSize = 17.sp)
            Text("🎁", fontSize = 17.sp)
        }
    }
}
