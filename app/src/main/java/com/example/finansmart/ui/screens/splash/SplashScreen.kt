package com.example.finansmart.ui.screens.splash

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.compose.material3.Text
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme
import androidx.compose.runtime.DisposableEffect

/*
 * TELA 01 - SPLASH
 *
 * Primeira tela do app. Mostra a marca enquanto a barra de progresso
 * enche; ao terminar, chama onTerminou() para ir ao Login.
 */

// Trilho da barra de progresso: branco a 14%, so usado aqui.
// Por ser exclusivo desta tela, fica local em vez de ir para Color.kt.
private val TrilhoProgresso = Color(0x24FFFFFF)

// Quanto tempo a barra leva para encher, em milissegundos.
private const val DURACAO_MS = 2200

@Composable
fun SplashScreen(
    onTerminou: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    /*
     * A splash tem fundo escuro nos DOIS temas. Sem este ajuste, no tema
     * claro o Android desenharia o relogio e a bateria em preto -- que
     * sumiriam no fundo escuro. Aqui forcamos os icones em branco.
     */
    ForcarIconesClarosNaStatusBar()

    /*
     * Animatable guarda um numero que muda com o tempo. Comeca em 0f
     * (barra vazia) e vai ate 1f (cheia).
     */
    val progresso = remember { Animatable(0f) }

    /*
     * LaunchedEffect roda uma vez quando a tela aparece. As linhas dentro
     * dele esperam a animacao terminar antes de seguir -- por isso o
     * onTerminou() so e chamado quando a barra chega ao fim.
     */
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
            .background(c.panel)          // fundo escuro cobre a tela toda
            .safeDrawingPadding()         // conteudo nao fica sob o relogio
            .padding(horizontal = Dimens.ScreenPaddingSplash),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        /* ---- marca, centralizada no espaco que sobra ---- */
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoFinanSmart()

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

        /* ---- barra de progresso, no rodape ---- */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 56.dp),
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
                        .fillMaxWidth(progresso.value)   // <- a animacao entra aqui
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
}

/*
 * O logo: um quadrado roxo arredondado com um losango branco vazado
 * dentro. O losango e so um quadrado menor girado 45 graus.
 */
@Composable
private fun LogoFinanSmart() {
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
        Box(
            Modifier
                .size(34.dp)
                .rotate(45f)
                .border(4.dp, Color.White, RoundedCornerShape(10.dp))
        )
    }
}

/* Deixa relogio, bateria e sinal em branco enquanto esta tela esta visivel. */
/* Deixa relogio, bateria e sinal em branco enquanto esta tela esta visivel,
   e devolve ao padrao do tema quando a tela sai. */
@Composable
private fun ForcarIconesClarosNaStatusBar() {
    val view = LocalView.current
    val temaEscuro = FinanSmartTheme.colors.isDark
    if (view.isInEditMode) return

    DisposableEffect(temaEscuro) {
        val janela = view.context.acharActivity()?.window
        val controle = janela?.let { WindowCompat.getInsetsController(it, view) }

        controle?.isAppearanceLightStatusBars = false

        onDispose {
            // Tema claro -> ícones escuros. Tema escuro -> ícones claros.
            controle?.isAppearanceLightStatusBars = !temaEscuro
        }
    }
}

private tailrec fun Context.acharActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.acharActivity()
    else -> null
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
