package com.jian.tracemind.feature.editor.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IosShareBold: ImageVector
  get() {
    if (_ios_share != null) {
      return _ios_share!!
    }
    _ios_share =
      ImageVector.Builder(
          name = "IosShareBold",
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
            moveTo(5.85f, 22.8f)
            quadToRelative(-1.09f, 0f, -1.87f, -0.78f)
            reflectiveQuadTo(3.2f, 20.15f)
            verticalLineTo(10.8f)
            quadTo(3.2f, 9.71f, 3.98f, 8.93f)
            reflectiveQuadTo(5.85f, 8.15f)
            horizontalLineToRelative(1.5f)
            quadToRelative(0.55f, 0f, 0.94f, 0.39f)
            quadTo(8.68f, 8.92f, 8.68f, 9.48f)
            quadToRelative(0f, 0.55f, -0.39f, 0.94f)
            reflectiveQuadTo(7.35f, 10.8f)
            horizontalLineTo(5.85f)
            verticalLineToRelative(9.35f)
            horizontalLineToRelative(12.3f)
            verticalLineTo(10.8f)
            horizontalLineToRelative(-1.5f)
            quadToRelative(-0.55f, 0f, -0.94f, -0.39f)
            quadTo(15.33f, 10.02f, 15.33f, 9.48f)
            quadToRelative(0f, -0.55f, 0.39f, -0.94f)
            reflectiveQuadTo(16.65f, 8.15f)
            horizontalLineToRelative(1.5f)
            quadToRelative(1.09f, 0f, 1.87f, 0.78f)
            reflectiveQuadTo(20.8f, 10.8f)
            verticalLineToRelative(9.35f)
            quadToRelative(0f, 1.09f, -0.78f, 1.87f)
            reflectiveQuadTo(18.15f, 22.8f)
            horizontalLineTo(5.85f)
            close()
            moveToRelative(5.21f, -6.14f)
            quadTo(10.68f, 16.27f, 10.68f, 15.73f)
            verticalLineTo(5.07f)
            lineTo(10.25f, 5.5f)
            quadTo(9.85f, 5.9f, 9.33f, 5.89f)
            reflectiveQuadTo(8.4f, 5.47f)
            reflectiveQuadTo(8f, 4.54f)
            reflectiveQuadTo(8.4f, 3.6f)
            lineTo(11.08f, 0.92f)
            quadTo(11.27f, 0.72f, 11.5f, 0.64f)
            reflectiveQuadTo(12f, 0.55f)
            quadToRelative(0.28f, 0f, 0.5f, 0.09f)
            reflectiveQuadToRelative(0.42f, 0.29f)
            lineToRelative(2.7f, 2.7f)
            quadToRelative(0.4f, 0.4f, 0.4f, 0.92f)
            reflectiveQuadToRelative(-0.4f, 0.93f)
            quadToRelative(-0.39f, 0.4f, -0.93f, 0.4f)
            reflectiveQuadTo(13.75f, 5.47f)
            lineTo(13.33f, 5.07f)
            verticalLineTo(15.73f)
            quadToRelative(0f, 0.55f, -0.39f, 0.94f)
            reflectiveQuadTo(12f, 17.05f)
            reflectiveQuadTo(11.06f, 16.66f)
            close()
          }
        }
        .build()
    return _ios_share!!
  }

private var _ios_share: ImageVector? = null
