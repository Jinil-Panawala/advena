package com.example.advena.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EventSeat: ImageVector
    get() {
        if (_EventSeat != null) return _EventSeat!!

        _EventSeat = ImageVector.Builder(
            name = "Event_seat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(160f, 840f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(640f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-160f)
                horizontalLineTo(240f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(20f, -280f)
                quadToRelative(-25f, 0f, -42.5f, -17.5f)
                reflectiveQuadTo(120f, 500f)
                reflectiveQuadToRelative(17.5f, -42.5f)
                reflectiveQuadTo(180f, 440f)
                reflectiveQuadToRelative(42.5f, 17.5f)
                reflectiveQuadTo(240f, 500f)
                reflectiveQuadToRelative(-17.5f, 42.5f)
                reflectiveQuadTo(180f, 560f)
                moveToRelative(100f, 0f)
                verticalLineToRelative(-360f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(360f, 120f)
                horizontalLineToRelative(240f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(680f, 200f)
                verticalLineToRelative(360f)
                close()
                moveToRelative(500f, 0f)
                quadToRelative(-25f, 0f, -42.5f, -17.5f)
                reflectiveQuadTo(720f, 500f)
                reflectiveQuadToRelative(17.5f, -42.5f)
                reflectiveQuadTo(780f, 440f)
                reflectiveQuadToRelative(42.5f, 17.5f)
                reflectiveQuadTo(840f, 500f)
                reflectiveQuadToRelative(-17.5f, 42.5f)
                reflectiveQuadTo(780f, 560f)
                moveToRelative(-420f, -80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(-280f)
                horizontalLineTo(360f)
                close()
                moveToRelative(0f, 0f)
                horizontalLineToRelative(240f)
                close()
            }
        }.build()

        return _EventSeat!!
    }

private var _EventSeat: ImageVector? = null

