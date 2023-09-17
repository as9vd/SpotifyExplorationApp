package com.asadshamsiev.spotifyexplorationapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val LightColorsGreen = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_light_scrim,
)

private val DarkColorsGreen = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.green.md_theme_dark_scrim,
)

private val LightColorsRed = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_light_scrim,
)

private val DarkColorsRed = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.red.md_theme_dark_scrim,
)

private val LightColorsOrange = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_light_scrim,
)

private val DarkColorsOrange = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.orange.md_theme_dark_scrim,
)

private val LightColorsBlue = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_light_scrim,
)

private val DarkColorsBlue = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.blue.md_theme_dark_scrim,
)

private val LightColorsPurple = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_light_scrim,
)

private val DarkColorsPurple = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.purple.md_theme_dark_scrim,
)

private val LightColorsBlack = lightColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_light_scrim,
)

private val DarkColorsBlack = darkColorScheme(
    primary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_primary,
    onPrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onPrimary,
    primaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onPrimaryContainer,
    secondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_secondary,
    onSecondary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onSecondary,
    secondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onSecondaryContainer,
    tertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_tertiary,
    onTertiary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onTertiary,
    tertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onTertiaryContainer,
    error = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_error,
    errorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_errorContainer,
    onError = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onError,
    onErrorContainer = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onErrorContainer,
    background = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_background,
    onBackground = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onBackground,
    surface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_surface,
    onSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onSurface,
    surfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_onSurfaceVariant,
    outline = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_outline,
    inverseOnSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_inverseOnSurface,
    inverseSurface = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_inverseSurface,
    inversePrimary = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_inversePrimary,
    surfaceTint = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_surfaceTint,
    outlineVariant = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_outlineVariant,
    scrim = com.asadshamsiev.spotifyexplorationapplication.ui.theme.black.md_theme_dark_scrim,
)

@Composable
fun AppTheme(
    colourIndex: Int,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (colourIndex == 0) {
        if (!useDarkTheme) {
            LightColorsBlue
        } else {
            DarkColorsBlue
        }
    } else if (colourIndex == 1) {
        if (!useDarkTheme) {
            LightColorsRed
        } else {
            DarkColorsRed
        }
    } else if (colourIndex == 2) {
        if (!useDarkTheme) {
            LightColorsGreen
        } else {
            DarkColorsGreen
        }
    } else if (colourIndex == 3) {
        if (!useDarkTheme) {
            LightColorsOrange
        } else {
            DarkColorsOrange
        }
    } else if (colourIndex == 4) {
        if (!useDarkTheme) {
            LightColorsPurple
        } else {
            DarkColorsPurple
        }
    } else {
        if (!useDarkTheme) {
            LightColorsBlack
        } else {
            DarkColorsBlack
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}