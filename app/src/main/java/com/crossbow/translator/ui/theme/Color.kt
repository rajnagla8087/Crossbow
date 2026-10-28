package com.crossbow.translator.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core Colors
val BrandIndigo = Color(0xFF6366F1)
val BrandIndigoDark = Color(0xFF4F46E5)
val BrandViolet = Color(0xFF8B5CF6)
val BrandCyan = Color(0xFF06B6D4)
val BrandAmber = Color(0xFFF59E0B)
val BrandEmerald = Color(0xFF10B981)
val BrandRose = Color(0xFFF43F5E)

// Dark Palette
val DarkBackground = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF131B2E)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)
val DarkBorder = Color(0xFF2E384D)

// Light Palette
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)
val LightBorder = Color(0xFFE2E8F0)

// Gradients
val CrossbowPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(BrandIndigo, BrandViolet, BrandCyan)
)

val CrossbowAccentGradient = Brush.linearGradient(
    colors = listOf(BrandViolet, BrandCyan)
)

val GoldAccentGradient = Brush.linearGradient(
    colors = listOf(BrandAmber, Color(0xFFFBBF24))
)

val DarkGlassBackground = Color(0xCC131B2E)
val LightGlassBackground = Color(0xEEFFFFFF)
