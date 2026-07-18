package com.jian.tracemind.feature.editor.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val CloseBold: ImageVector
  get() {
    if (_close != null) {
      return _close!!
    }
    _close =
      ImageVector.Builder(
          name = "CloseBold",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(12.03f, 13.85f)
            lineToRelative(-4.78f, 4.8f)
            quadToRelative(-0.4f, 0.4f, -0.95f, 0.39f)
            quadTo(5.75f, 19.02f, 5.35f, 18.63f)
            quadTo(4.98f, 18.23f, 4.99f, 17.7f)
            quadTo(5f, 17.18f, 5.38f, 16.77f)
            lineTo(10.13f, 12f)
            lineTo(5.35f, 7.18f)
            quadTo(4.98f, 6.77f, 4.98f, 6.25f)
            quadToRelative(0f, -0.53f, 0.38f, -0.93f)
            quadTo(5.75f, 4.93f, 6.3f, 4.91f)
            reflectiveQuadTo(7.25f, 5.3f)
            lineToRelative(4.78f, 4.8f)
            lineTo(16.75f, 5.3f)
            quadTo(17.15f, 4.9f, 17.7f, 4.91f)
            quadToRelative(0.55f, 0.01f, 0.95f, 0.41f)
            quadToRelative(0.38f, 0.4f, 0.36f, 0.93f)
            quadTo(19f, 6.77f, 18.63f, 7.18f)
            lineTo(13.88f, 12f)
            lineToRelative(4.75f, 4.77f)
            quadToRelative(0.38f, 0.38f, 0.39f, 0.91f)
            quadToRelative(0.01f, 0.54f, -0.36f, 0.94f)
            quadToRelative(-0.4f, 0.4f, -0.95f, 0.41f)
            reflectiveQuadTo(16.75f, 18.65f)
            lineToRelative(-4.72f, -4.8f)
            close()
          }
        }
        .build()
    return _close!!
  }

private var _close: ImageVector? = null
