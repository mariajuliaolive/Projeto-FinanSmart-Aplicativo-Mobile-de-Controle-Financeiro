package com.example.finansmart.ui.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Repositorio
import com.example.finansmart.ui.components.Divisoria
import com.example.finansmart.ui.components.Interruptor
import com.example.finansmart.ui.components.LinhaDeOpcao
import com.example.finansmart.ui.components.RotuloSecao
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 09 - CONFIGURACOES */

@Composable
fun ConfiguracoesScreen(
    onAbrirPerfil: () -> Unit = {},
    onAlterarSenha: () -> Unit = {},
    onPrivacidade: () -> Unit = {},
    onSobre: () -> Unit = {},
    onSair: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    val perfil = Repositorio.perfil.value
    var notificacoes by remember { mutableStateOf(true) }

    val temaForcado = Repositorio.temaEscuroForcado.value
    val nomeDoTema = when (temaForcado) {
        true -> "Escuro"
        false -> "Claro"
        null -> "Sistema"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Spacer(Modifier.height(6.dp))

        Text(text = "Configurações", style = t.TituloLista, color = c.ink)

        /* ---- cartao do perfil ---- */
        Spacer(Modifier.height(Dimens.Space18))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusCardMedium))
                .background(c.panel)
                .clickable { onAbrirPerfil() }
                .padding(Dimens.Space16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(Dimens.RadiusPill))
                    .background(c.primaryFill),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = perfil.iniciais,
                    style = t.MetaNome.copy(fontSize = 18.sp),
                    color = Color.White
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(text = perfil.nome, style = t.MetaNome, color = Color.White)
                Spacer(Modifier.height(2.dp))
                Text(text = perfil.email, style = t.Caption, color = c.panelSub)
            }
            Text(text = "›", fontSize = 16.sp, color = c.panelSub)
        }

        /* ---- CONTA ---- */
        Spacer(Modifier.height(24.dp))
        RotuloSecao(texto = "CONTA")
        Spacer(Modifier.height(9.dp))
        GrupoDeOpcoes {
            // "Editar informacoes" saiu: abria exatamente a mesma tela que
            // "Meu perfil", onde os campos ja sao editaveis.
            LinhaDeOpcao(
                emoji = "👤",
                texto = "Meu perfil",
                aoClicar = onAbrirPerfil
            )
        }

        /* ---- PREFERENCIAS ---- */
        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "PREFERÊNCIAS")
        Spacer(Modifier.height(9.dp))
        GrupoDeOpcoes {
            LinhaDeOpcao(
                emoji = "🌙",
                texto = "Tema",
                valor = nomeDoTema,
                aoClicar = {
                    // Percorre Sistema -> Claro -> Escuro -> Sistema
                    Repositorio.temaEscuroForcado.value = when (temaForcado) {
                        null -> false
                        false -> true
                        true -> null
                    }
                }
            )
            Divisoria()
            LinhaDeOpcao(
                emoji = "🔔",
                texto = "Notificações",
                mostrarSeta = false,
                conteudoDireita = {
                    Interruptor(
                        ligado = notificacoes,
                        aoAlternar = { notificacoes = !notificacoes }
                    )
                }
            )
        }

        /* ---- SEGURANCA ---- */
        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "SEGURANÇA")
        Spacer(Modifier.height(9.dp))
        GrupoDeOpcoes {
            LinhaDeOpcao(emoji = "🔑", texto = "Alterar senha", aoClicar = onAlterarSenha)
            Divisoria()
            LinhaDeOpcao(emoji = "🛡️", texto = "Privacidade", aoClicar = onPrivacidade)
        }

        /* ---- SOBRE ---- */
        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "SOBRE")
        Spacer(Modifier.height(9.dp))
        GrupoDeOpcoes {
            LinhaDeOpcao(emoji = "ℹ️", texto = "Sobre o FinanSmart", aoClicar = onSobre)
            Divisoria()
            LinhaDeOpcao(
                emoji = "📦",
                texto = "Versão do aplicativo",
                mostrarSeta = false,
                conteudoDireita = {
                    Text(
                        text = "1.0.0",
                        style = t.MonoValue.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                        ),
                        color = c.ink3
                    )
                }
            )
        }

        /* ---- sair ---- */
        Spacer(Modifier.height(Dimens.Space20))
        Box(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(Dimens.RadiusButton))
                .border(Dimens.BorderWidth, c.neg, RoundedCornerShape(Dimens.RadiusButton))
                .clickable { onSair() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↩ Sair da conta",
                style = t.ItemConfig.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = c.neg
            )
        }

        Spacer(Modifier.height(22.dp))
    }
}

/** A moldura branca que agrupa varias LinhaDeOpcao. */
@Composable
private fun GrupoDeOpcoes(conteudo: @Composable ColumnScope.() -> Unit) {
    val c = FinanSmartTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(18.dp)),
        content = conteudo
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ConfiguracoesPreview() {
    FinanSmartTheme { ConfiguracoesScreen() }
}
