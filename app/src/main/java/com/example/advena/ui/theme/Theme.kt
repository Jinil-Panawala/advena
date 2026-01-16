package com.example.advena.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    primary = Pink,
    secondary = DarkPurple,
    tertiary = Orange,
    background = Black,
    surface = Black,
    surfaceVariant = LightGreyDark,
    surfaceDim = DarkGreyDark,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = Pink,
    secondary = DarkPurple,
    tertiary = Orange,
    background = White,
    surface = White,
    surfaceVariant = LightGrey,
    surfaceDim = DarkGrey,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black,


)



@Composable
fun ADVENATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}