package com.tecsup.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val Claro = lightColorScheme(
    primary = Color(0xFF206350),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDAEEE2),
    onPrimaryContainer = Color(0xFF174A3B),
    secondary = Color(0xFF59624B),
    secondaryContainer = Color(0xFFE4EBDC),
    onSecondaryContainer = Color(0xFF293823),
    background = Color(0xFFF8F6EF),
    onBackground = Color(0xFF1C2A24),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF1C2A24),
    surfaceVariant = Color(0xFFEDF0E7),
    onSurfaceVariant = Color(0xFF4B5E54),
    surfaceTint = Color(0xFF206350),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF5F5ED),
    surfaceContainer = Color(0xFFEEF2E8),
    surfaceContainerHigh = Color(0xFFE8EEE4),
    surfaceContainerHighest = Color(0xFFE1E8DB),
    outline = Color(0xFF718076),
    outlineVariant = Color(0xFFD8DED3),
    inverseSurface = Color(0xFF173D32),
    inverseOnSurface = Color(0xFFF1FAF3),
    error = Color(0xFFA33430),
    errorContainer = Color(0xFFFBE5E1),
    onErrorContainer = Color(0xFF7B2625)
)

private val Oscuro = darkColorScheme(
    primary = Color(0xFFA3D6BC),
    onPrimary = Color(0xFF0B3829),
    primaryContainer = Color(0xFF244B3C),
    onPrimaryContainer = Color(0xFFD7F4E3),
    secondary = Color(0xFFBFCAB2),
    secondaryContainer = Color(0xFF374936),
    onSecondaryContainer = Color(0xFFDDECD8),
    background = Color(0xFF141D18),
    onBackground = Color(0xFFE6EDE3),
    surface = Color(0xFF202D26),
    onSurface = Color(0xFFE6EDE3),
    surfaceVariant = Color(0xFF293A31),
    onSurfaceVariant = Color(0xFFBBCBC0),
    surfaceTint = Color(0xFFA3D6BC),
    surfaceContainerLowest = Color(0xFF111A15),
    surfaceContainerLow = Color(0xFF1B271F),
    surfaceContainer = Color(0xFF253128),
    surfaceContainerHigh = Color(0xFF2E3D32),
    surfaceContainerHighest = Color(0xFF39483D),
    outline = Color(0xFF8FA197),
    outlineVariant = Color(0xFF46584D),
    inverseSurface = Color(0xFF264C40),
    inverseOnSurface = Color(0xFFF1FAF3),
    error = Color(0xFFFFB4AA),
    errorContainer = Color(0xFF622A27),
    onErrorContainer = Color(0xFFFFDAD4)
)

private val Base = Typography()
private val Tipografia = Typography(
    displaySmall = Base.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineMedium = Base.headlineMedium.copy(fontFamily = FontFamily.Serif),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Bold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold)
)

@Composable
fun MuroCampusTheme(oscuro: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) Oscuro else Claro,
        typography = Tipografia,
        content = content
    )
}
