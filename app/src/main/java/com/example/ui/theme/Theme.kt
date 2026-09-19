package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.material3.OutlinedTextFieldDefaults

// Crisp Navy Blue and White light scheme
private val LightColorScheme = lightColorScheme(
  primary = Navy900,
  onPrimary = Color.White,
  primaryContainer = Blue50,
  onPrimaryContainer = Navy900,
  secondary = Blue600,
  onSecondary = Color.White,
  secondaryContainer = Blue100,
  onSecondaryContainer = Navy900,
  tertiary = GoldAccent,
  onTertiary = NavyDark,
  tertiaryContainer = GoldLight,
  onTertiaryContainer = Navy900,
  background = CanvasBackground,
  onBackground = TextPrimary,
  surface = SurfaceCard,
  onSurface = TextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondary,
  outline = SurfaceBorder
)

// Deep Navy scheme guaranteeing dark readable text on cards and white text on dark headers
private val DarkColorScheme = lightColorScheme(
  primary = Navy900,
  onPrimary = Color.White,
  primaryContainer = Blue50,
  onPrimaryContainer = Navy900,
  secondary = Blue600,
  onSecondary = Color.White,
  secondaryContainer = Blue100,
  onSecondaryContainer = Navy900,
  tertiary = GoldAccent,
  onTertiary = NavyDark,
  tertiaryContainer = GoldLight,
  onTertiaryContainer = Navy900,
  background = CanvasBackground,
  onBackground = TextPrimary,
  surface = SurfaceCard,
  onSurface = TextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondary,
  outline = SurfaceBorder
)

@Composable
fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = Navy900,
  unfocusedTextColor = Navy900,
  focusedContainerColor = SurfaceCard,
  unfocusedContainerColor = SurfaceCard,
  focusedBorderColor = Blue600,
  unfocusedBorderColor = SurfaceBorder,
  focusedLabelColor = Blue600,
  unfocusedLabelColor = TextSecondary,
  focusedPlaceholderColor = TextMuted,
  unfocusedPlaceholderColor = TextMuted,
  cursorColor = Navy900
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force consistent Navy Blue and White identity
  dynamicColor: Boolean = false, // Preserve bespoke Navy Blue and White identity
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = Navy900.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
