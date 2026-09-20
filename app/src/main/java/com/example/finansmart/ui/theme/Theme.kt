package com.example.finansmart.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * O TEMA DO FINANSMART
 *
 * O Material Design (padrao do Android) tem lugares prontos para umas
 * poucas cores: primary, background, surface... Mas o nosso prototipo tem
 * 21 cores com papeis proprios -- panel, ink2, pos, negSoft e por ai vai.
 * Elas nao cabem nos lugares do Material.
 *
 * Por isso criamos nossa propria "caixa de cores" (FinanSmartColors) e a
 * entregamos para o app inteiro de uma vez. Assim, em qualquer tela, voce
 * escreve:
 *
 *     FinanSmartTheme.colors.primary
 *
 * e recebe o roxo certo -- o do tema claro OU o do escuro, conforme o
 * celular estiver. Voce nunca precisa perguntar qual tema esta ativo.
 */

@Immutable
data class FinanSmartColors(
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val line: Color,
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val panel: Color,
    val panel2: Color,
    val panelSub: Color,
    val primary: Color,
    val primaryFill: Color,
    val primaryInk: Color,
    val primarySoft: Color,
    val pos: Color,
    val posSoft: Color,
    val neg: Color,
    val negSoft: Color,
    val warn: Color,
    val track: Color,
    val dots: Color,
    val isDark: Boolean,
)

private val LightColors = FinanSmartColors(
    bg = LightBg, surface = LightSurface, surface2 = LightSurface2, line = LightLine,
    ink = LightInk, ink2 = LightInk2, ink3 = LightInk3,
    panel = LightPanel, panel2 = LightPanel2, panelSub = LightPanelSub,
    primary = LightPrimary, primaryFill = LightPrimaryFill,
    primaryInk = LightPrimaryInk, primarySoft = LightPrimarySoft,
    pos = LightPos, posSoft = LightPosSoft, neg = LightNeg, negSoft = LightNegSoft,
    warn = LightWarn, track = LightTrack, dots = LightDots,
    isDark = false,
)

private val DarkColors = FinanSmartColors(
    bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2, line = DarkLine,
    ink = DarkInk, ink2 = DarkInk2, ink3 = DarkInk3,
    panel = DarkPanel, panel2 = DarkPanel2, panelSub = DarkPanelSub,
    primary = DarkPrimary, primaryFill = DarkPrimaryFill,
    primaryInk = DarkPrimaryInk, primarySoft = DarkPrimarySoft,
    pos = DarkPos, posSoft = DarkPosSoft, neg = DarkNeg, negSoft = DarkNegSoft,
    warn = DarkWarn, track = DarkTrack, dots = DarkDots,
    isDark = true,
)

/*
 * "CompositionLocal" e um cano invisivel que leva um valor do topo do app
 * ate qualquer tela la embaixo, sem precisar passar de mao em mao.
 * E assim que MaterialTheme.colorScheme funciona -- estamos fazendo o mesmo.
 */
val LocalFinanSmartColors = staticCompositionLocalOf { LightColors }

/*
 * Atalho de leitura. Em vez de LocalFinanSmartColors.current, as telas
 * escrevem FinanSmartTheme.colors -- mais curto e mais claro.
 */
object FinanSmartTheme {
    val colors: FinanSmartColors
        @Composable get() = LocalFinanSmartColors.current

    val type get() = FinanSmartType
    val dimens get() = Dimens
}

/*
 * Envolve o app inteiro. Fica no MainActivity, por fora de tudo.
 *
 * darkTheme = isSystemInDarkTheme() -> segue o modo escuro do celular
 * automaticamente. Na tela 09 (Configuracoes) vamos poder forcar um dos
 * dois, passando true ou false aqui.
 */
@Composable
fun FinanSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    // Espelhamos algumas cores nos lugares do Material, para que os
    // componentes prontos (Button, TextField...) ja nascam na cor certa.
    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primaryFill, onPrimary = Color.White,
            background = colors.bg, onBackground = colors.ink,
            surface = colors.surface, onSurface = colors.ink,
            error = colors.neg,
        )
    } else {
        lightColorScheme(
            primary = colors.primaryFill, onPrimary = Color.White,
            background = colors.bg, onBackground = colors.ink,
            surface = colors.surface, onSurface = colors.ink,
            error = colors.neg,
        )
    }

    CompositionLocalProvider(LocalFinanSmartColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
