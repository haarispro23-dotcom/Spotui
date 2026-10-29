
package com.music.spotui.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

private data class Snowflake(
    val x: Float,
    val size: Float,
    val speed: Int,
    val delay: Int,
    val drift: Float
)

@Composable
fun ChristmasSnow(
    modifier: Modifier = Modifier
) {
    val flakes = remember {
        List(65) { index ->
            Snowflake(
                x = ((index * 47 + 13) % 100) / 100f,
                size = 1.5f + ((index * 7) % 20) / 10f,
                speed = 3500 + (index % 5) * 1200,
                delay = (index * 170) % 4000,
                drift = ((index % 7) - 3) * 12f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "christmas_snow")

    val progress = flakes.mapIndexed { index, flake ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = flake.speed,
                    delayMillis = flake.delay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "snow_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        flakes.forEachIndexed { index, flake ->
            val p = progress[index].value
            val x = flake.x * size.width +
                flake.drift * p
            val y = p * size.height

            drawCircle(
                color = Color.White.copy(
                    alpha = 0.45f + (flake.size / 10f)
                ),
                radius = flake.size.dp.toPx(),
                center = Offset(
                    x = x % size.width,
                    y = y
                )
            )
        }
    }
}
