package com.jian.tracemind.core.ui.extensions

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.traceShadow(
    color: Color = Color(0x0D000000), // 5% black by default
    borderRadius: Dp = 24.dp,
    blurRadius: Dp = 16.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp,
) = this.drawBehind {
    this.drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = color.toArgb()
        
        if (blurRadius != 0.dp) {
            frameworkPaint.maskFilter = BlurMaskFilter(
                blurRadius.toPx(),
                BlurMaskFilter.Blur.NORMAL
            )
        }
        
        val left = offsetX.toPx() - spread.toPx()
        val top = offsetY.toPx() - spread.toPx()
        val right = size.width + offsetX.toPx() + spread.toPx()
        val bottom = size.height + offsetY.toPx() + spread.toPx()
        
        canvas.drawRoundRect(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )
    }
}
