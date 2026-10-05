package com.example.finansmart.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.finansmart.ui.theme.FinanSmartTheme

/**
 * Deixa o relógio, a bateria e o sinal em BRANCO enquanto a tela estiver
 * visível, e devolve ao padrão do tema quando ela sair.
 *
 * Serve para as telas que têm fundo escuro nos dois temas: a Splash e o
 * topo do detalhe da transação. Sem isto, no tema claro o Android desenha
 * esses ícones em preto e eles somem no fundo escuro.
 *
 * DisposableEffect é o que garante a volta: o onDispose roda no momento
 * em que a tela deixa de existir.
 */
@Composable
fun ForcarIconesClarosNaStatusBar() {
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
