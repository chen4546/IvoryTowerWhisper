package com.chen.ivorytowerwhisper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),
    secondary = Color(0xFF607D8B),
    tertiary = Color(0xFF795548),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    primaryContainer = Color(0xFFE8EAF6),
    secondaryContainer = Color(0xFFE0F2F1)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7986CB),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFBCAAA4),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
    primaryContainer = Color(0xFF283593),
    secondaryContainer = Color(0xFF004D40)
)

val Blue40 = Color(0xFF2196F3)
val Blue80 = Color(0xFF64B5F6)
val Green40 = Color(0xFF4CAF50)
val Green80 = Color(0xFF81C784)

val Red40 = Color(0xFFF44336)
val Red80 = Color(0xFFE57373)

@Composable
fun IvoryTowerWhisperTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}