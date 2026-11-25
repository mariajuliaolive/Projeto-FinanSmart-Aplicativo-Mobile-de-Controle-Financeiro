package com.example.finansmart.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.components.ForcarIconesClarosNaStatusBar
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

private const val DURACAO_MS = 2200
private val TrilhoProgresso = Color(0x24FFFFFF)

@Composable
fun SplashScreen(
    onTerminou: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // O fundo é escuro nos dois temas, então o relógio e a bateria
    // precisam ser brancos — no tema claro o Android os deixaria pretos.
    ForcarIconesClarosNaStatusBar()

    val progresso = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progresso.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = DURACAO_MS, easing = LinearEasing)
        )
        onTerminou()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.panel)
            .safeDrawingPadding()
            .padding(horizontal = Dimens.ScreenPaddingSplash),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Marca(modifier = Modifier.weight(1f))
        Carregando(progresso = progresso.value)
    }
}

@Composable
private fun Marca(modifier: Modifier = Modifier) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Logo()

        Spacer(Modifier.height(Dimens.Space22))

        Text(
            text = "FinanSmart",
            style = t.SplashTitle,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Seu dinheiro organizado,\ndo jeito simples.",
            style = t.Body.copy(lineHeight = 22.4.sp),
            color = c.panelSub,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Carregando(progresso: Float) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    Column(
        modifier = Modifier.padding(bottom = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .width(120.dp)
                .height(Dimens.ProgressTrack)
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(TrilhoProgresso)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progresso)
                    .fillMaxHeight()
                    .background(c.primaryFill)
            )
        }

        Spacer(Modifier.height(Dimens.Space20))

        Text(
            text = "CARREGANDO…",
            style = t.MonoLabel,
            color = c.panelSub,
        )
    }
}

@Composable
private fun Logo() {
    val c = FinanSmartTheme.colors

    Box(
        modifier = Modifier
            .size(Dimens.LogoLarge)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(Dimens.RadiusLogoLarge),
                ambientColor = c.primary,
                spotColor = c.primary,
            )
            .clip(RoundedCornerShape(Dimens.RadiusLogoLarge))
            .background(c.primaryFill),
        contentAlignment = Alignment.Center,
    ) {
        // O losango é um quadrado menor girado 45 graus.
        Box(
            Modifier
                .size(34.dp)
                .rotate(45f)
                .border(4.dp, Color.White, RoundedCornerShape(10.dp))
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashClaroPreview() {
    FinanSmartTheme(darkTheme = false) { SplashScreen() }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashEscuroPreview() {
    FinanSmartTheme(darkTheme = true) { SplashScreen() }
}
