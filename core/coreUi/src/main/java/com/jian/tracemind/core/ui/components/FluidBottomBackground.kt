package com.jian.tracemind.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun FluidBottomBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fluid_transition")

    // We use normalized values from -1f to 1f or 0f to 1f to represent screen percentage
    // Blob 1: Blue (#2b58f9)
    val nx1 by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1_x"
    )
    val ny1 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = -0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1_y"
    )
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1_scale"
    )

    // Blob 2: Purple (#9b2cfa)
    val nx2 by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = -0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2_x"
    )
    val ny2 by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2_y"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2_scale"
    )

    // Blob 3: Cyan (#00d2ff)
    val nx3 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob3_x"
    )
    val ny3 by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob3_y"
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob3_scale"
    )

    // Blob 4: Magenta (#ff007f)
    val nx4 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = -0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob4_x"
    )
    val ny4 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = -0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob4_y"
    )
    val scale4 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob4_scale"
    )

    Canvas(
        modifier = modifier
            .graphicsLayer {
                // Allows us to apply the BlendMode.DstIn mask
                alpha = 1f
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        // Blob 1: Blue
        val r1 = 180.dp.toPx() * scale1
        val center1 = Offset(size.width * nx1, size.height + (size.height * ny1))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2B58F9).copy(alpha = 0.8f), Color.Transparent),
                center = center1,
                radius = r1
            ),
            radius = r1,
            center = center1,
            blendMode = BlendMode.Screen
        )

        // Blob 2: Purple
        val r2 = 160.dp.toPx() * scale2
        val center2 = Offset(size.width * nx2, size.height + (size.height * ny2))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF9B2CFA).copy(alpha = 0.8f), Color.Transparent),
                center = center2,
                radius = r2
            ),
            radius = r2,
            center = center2,
            blendMode = BlendMode.Screen
        )

        // Blob 3: Cyan
        val r3 = 140.dp.toPx() * scale3
        val center3 = Offset(size.width * nx3, size.height + (size.height * ny3))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00D2FF).copy(alpha = 0.8f), Color.Transparent),
                center = center3,
                radius = r3
            ),
            radius = r3,
            center = center3,
            blendMode = BlendMode.Screen
        )

        // Blob 4: Magenta
        val r4 = 150.dp.toPx() * scale4
        val center4 = Offset(size.width * nx4, size.height + (size.height * ny4))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF007F).copy(alpha = 0.8f), Color.Transparent),
                center = center4,
                radius = r4
            ),
            radius = r4,
            center = center4,
            blendMode = BlendMode.Screen
        )

        // Draw the top fade-out mask (CSS: mask-image: linear-gradient(to top, rgba(0,0,0,1) 10%, rgba(0,0,0,0) 100%))
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startY = 0f,
                endY = size.height * 0.9f
            ),
            blendMode = BlendMode.DstIn
        )
    }
}
