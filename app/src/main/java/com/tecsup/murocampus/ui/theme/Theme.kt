package com.tecsup.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Azul = Color(0xFF1565C0)
private val AzulClaro = Color(0xFF90CAF9)

private val Claro = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF2E7D32)
)

private val Oscuro = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color(0xFF0D2E5C),
    primaryContainer = Color(0xFF1E3A66),
    secondary = Color(0xFF81C784)
)

@Composable
fun MuroCampusTheme(oscuro: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (oscuro) Oscuro else Claro, content = content)
}
