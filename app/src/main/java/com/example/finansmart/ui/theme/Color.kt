package com.example.finansmart.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Cores extraidas do prototipo FinanSmart.
 *
 * Nomes iguais aos do prototipo, para voce conseguir comparar lado a lado:
 *   bg          fundo da tela
 *   surface     cards e campos
 *   line        bordas
 *   ink         texto principal      ink2 secundario     ink3 desabilitado
 *   panel       card escuro do saldo
 *   primary     cor da marca (roxo)
 *   pos / neg   receitas / despesas
 *
 * Em Kotlin a cor e escrita como 0xAARRGGBB:
 *   AA = transparencia (FF = opaco, 00 = invisivel)
 *   RR GG BB = vermelho, verde, azul  -- os mesmos do CSS
 */

/* ---------------------- TEMA CLARO ---------------------- */

val LightBg          = Color(0xFFF8FAFC)
val LightSurface     = Color(0xFFFFFFFF)
val LightSurface2    = Color(0xFFF3F4F6)
val LightLine        = Color(0xFFE5E7EB)

val LightInk         = Color(0xFF111827)
val LightInk2        = Color(0xFF6B7280)
val LightInk3        = Color(0xFF9CA3AF)

val LightPanel       = Color(0xFF1B2140)
val LightPanel2      = Color(0x1FFFFFFF) // branco 12%
val LightPanelSub    = Color(0xFFA4BCFD)

val LightPrimary     = Color(0xFF5469F5)
val LightPrimaryFill = Color(0xFF5469F5)
val LightPrimaryInk  = Color(0xFF3D4BEA)
val LightPrimarySoft = Color(0xFFEEF2FF)

val LightPos         = Color(0xFF15803D)
val LightPosSoft     = Color(0xFFDCFCE7)
val LightNeg         = Color(0xFFDC2626)
val LightNegSoft     = Color(0xFFFEE2E2)
val LightWarn        = Color(0xFFF59E0B)

val LightTrack       = Color(0xFFEEF2FF)
val LightDots        = Color(0xFFD8DCE5)

/* ---------------------- TEMA ESCURO ---------------------- */

val DarkBg          = Color(0xFF11162A)
val DarkSurface     = Color(0xFF1B2140)
val DarkSurface2    = Color(0xFF232A4D)
val DarkLine        = Color(0x17FFFFFF) // branco 9%

val DarkInk         = Color(0xFFF3F4F6)
val DarkInk2        = Color(0xFFA1A8C3)
val DarkInk3        = Color(0xFF7D86AD)

val DarkPanel       = Color(0xFF232A4D)
val DarkPanel2      = Color(0x1AFFFFFF) // branco 10%
val DarkPanelSub    = Color(0xFFA4BCFD)

val DarkPrimary     = Color(0xFF7B95FB)
val DarkPrimaryFill = Color(0xFF4757E0)
val DarkPrimaryInk  = Color(0xFFA4BCFD)
val DarkPrimarySoft = Color(0x297B95FB) // roxo claro 16%

val DarkPos         = Color(0xFF4ADE80)
val DarkPosSoft     = Color(0x294ADE80)
val DarkNeg         = Color(0xFFF87171)
val DarkNegSoft     = Color(0x29F87171)
val DarkWarn        = Color(0xFFFACC15)

val DarkTrack       = Color(0x17FFFFFF)
val DarkDots        = Color(0x2EFFFFFF) // branco 18%

/* ------------- CORES DAS CATEGORIAS -------------
 * Nao mudam entre claro e escuro: sao a identidade
 * de cada categoria nos graficos e nas legendas.     */

val CatMoradia     = Color(0xFFF59E0B)
val CatAlimentacao = Color(0xFF5469F5)
val CatTransporte  = Color(0xFF06B6D4)
val CatSaude       = Color(0xFFEC4899)
val CatEducacao    = Color(0xFF8B5CF6)
val CatLazer       = Color(0xFF22C55E)
val CatOutros      = Color(0xFFCBD5E1)
