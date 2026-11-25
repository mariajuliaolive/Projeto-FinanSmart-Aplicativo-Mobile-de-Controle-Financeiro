package com.example.finansmart.ui.screens.cadastro

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.components.BotaoOlho
import com.example.finansmart.ui.components.BotaoPrimario
import com.example.finansmart.ui.components.CaixaDeMarcar
import com.example.finansmart.ui.components.BotaoVoltar
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 03 - CADASTRO */

@Composable
fun CadastroScreen(
    onVoltar: () -> Unit = {},
    // Agora a tela entrega o que foi digitado; quem cria a conta e o ViewModel.
    onCriarConta: (nome: String, email: String, senha: String) -> Unit = { _, _, _ -> },
    // Estes dois vem do AuthUiState e so servem para desenhar.
    carregando: Boolean = false,
    erro: String? = null,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var aceitouTermos by remember { mutableStateOf(false) }

    // Valores derivados: nao sao guardados, sao calculados do que ja temos.
    val forca = forcaDaSenha(senha)
    val senhasConferem = confirmar.isNotEmpty() && confirmar == senha
    val podeCriar = nome.isNotBlank() && email.isNotBlank() &&
            senha.isNotBlank() && senhasConferem && aceitouTermos

    val marcaDeConfere: (@Composable () -> Unit)? =
        if (senhasConferem) {
            { Text(text = "✓", fontSize = 14.sp, color = c.pos) }
        } else {
            null
        }

    Column(
        modifier = modifier.fillMaxSize().background(c.bg).safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingAuth)
        ) {
            Spacer(Modifier.height(14.dp))
            BotaoVoltar(aoClicar = onVoltar)
            Spacer(Modifier.height(22.dp))
            Text(text = "Criar conta", style = t.ScreenTitle, color = c.ink)
            Spacer(Modifier.height(Dimens.Space8))
            Text(text = "Leva menos de um minuto.", style = t.Body, color = c.ink2)

            Spacer(Modifier.height(28.dp))

            CampoTexto(
                rotulo = "Nome",
                valor = nome,
                aoMudarValor = { nome = it },
                placeholder = "Maria Oliveira"
            )

            Spacer(Modifier.height(13.dp))

            CampoTexto(
                rotulo = "E-mail",
                valor = email,
                aoMudarValor = { email = it },
                placeholder = "maria@email.com",
                tipoTeclado = KeyboardType.Email
            )

            Spacer(Modifier.height(13.dp))

            CampoTexto(
                rotulo = "Senha",
                valor = senha,
                aoMudarValor = { senha = it },
                placeholder = "••••••••",
                ocultarTexto = !senhaVisivel,
                tipoTeclado = KeyboardType.Password,
                conteudoFinal = {
                    BotaoOlho(
                        visivel = senhaVisivel,
                        aoAlternar = { senhaVisivel = !senhaVisivel }
                    )
                }
            )

            BarraForcaSenha(forca = forca)

            Spacer(Modifier.height(16.dp))

            CampoTexto(
                rotulo = "Confirmar senha",
                valor = confirmar,
                aoMudarValor = { confirmar = it },
                placeholder = "••••••••",
                ocultarTexto = !senhaVisivel,
                tipoTeclado = KeyboardType.Password,
                conteudoFinal = marcaDeConfere
            )

            Spacer(Modifier.height(Dimens.Space16))

            AceiteDosTermos(
                marcado = aceitouTermos,
                aoAlternar = { aceitouTermos = !aceitouTermos }
            )

            Spacer(Modifier.height(Dimens.Space16))

            BotaoPrimario(
                texto = if (carregando) "Criando conta…" else "Criar conta",
                aoClicar = { onCriarConta(nome, email, senha) },
                // Apagado enquanto espera o Firebase: evita criar a conta duas vezes.
                habilitado = podeCriar && !carregando
            )

            // Erro vindo do Firebase (e-mail ja usado, sem internet...), ja traduzido.
            if (erro != null) {
                Spacer(Modifier.height(Dimens.Space8))
                Text(text = "⚠ $erro", style = t.ListSubtitle, color = c.neg)
            }

            // Botao apagado e mudo confunde: aqui ele diz o que falta.
            if (!podeCriar) {
                Spacer(Modifier.height(Dimens.Space8))
                Text(
                    text = oQueFalta(
                        nome = nome, email = email, senha = senha,
                        senhasConferem = senhasConferem, aceitouTermos = aceitouTermos
                    ),
                    style = t.ListSubtitle,
                    color = c.ink3
                )
            }

            Spacer(Modifier.height(Dimens.Space20))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenPaddingAuth)
                .padding(top = Dimens.Space20, bottom = 34.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Já tem conta? ", style = t.Footer, color = c.ink2)
            Text(
                text = "Voltar ao login",
                style = t.Footer.copy(fontWeight = FontWeight.SemiBold),
                color = c.primaryInk,
                modifier = Modifier.clickable { onVoltar() }
            )
        }
    }
}

/** Monta a frase que explica por que o botao ainda esta apagado. */
private fun oQueFalta(
    nome: String,
    email: String,
    senha: String,
    senhasConferem: Boolean,
    aceitouTermos: Boolean,
): String {
    val pendencias = buildList {
        if (nome.isBlank()) add("o nome")
        if (email.isBlank()) add("o e-mail")
        if (senha.isBlank()) add("a senha")
        else if (!senhasConferem) add("a confirmação igual à senha")
        if (!aceitouTermos) add("aceitar os termos")
    }
    return when (pendencias.size) {
        0 -> ""
        1 -> "Falta " + pendencias[0] + "."
        else -> "Falta " + pendencias.dropLast(1).joinToString(", ") +
                " e " + pendencias.last() + "."
    }
}

/**
 * Quantos criterios a senha cumpre, de 0 a 4.
 * Kotlin puro: entra um texto, sai um numero. Facil de ler e de testar.
 */
private fun forcaDaSenha(senha: String): Int {
    if (senha.isEmpty()) return 0
    var pontos = 0
    if (senha.length >= 8) pontos++
    if (senha.any { it.isDigit() }) pontos++
    if (senha.any { it.isUpperCase() }) pontos++
    if (senha.any { !it.isLetterOrDigit() }) pontos++
    return pontos
}

/** Os 4 tracinhos e o texto embaixo do campo de senha. */
@Composable
private fun BarraForcaSenha(forca: Int) {
    val c = FinanSmartTheme.colors

    val cor = when (forca) {
        0 -> c.track
        1 -> c.neg
        2 -> c.warn
        else -> c.pos
    }
    val nomeDaForca = when (forca) {
        0 -> ""
        1 -> "Senha fraca"
        2 -> "Senha média"
        3 -> "Senha forte"
        else -> "Senha muito forte"
    }

    Column {
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(4) { indice ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusPill))
                        .background(if (indice < forca) cor else c.track)
                )
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (nomeDaForca.isEmpty()) {
                "8+ caracteres e um número."
            } else {
                nomeDaForca + " — 8+ caracteres e um número."
            },
            style = FinanSmartTheme.type.ListSubtitle,
            color = c.ink2
        )
    }
}

/** Quadradinho de marcar + o texto dos termos. */
@Composable
private fun AceiteDosTermos(marcado: Boolean, aoAlternar: () -> Unit) {
    val c = FinanSmartTheme.colors
    Row(
        modifier = Modifier.clickable(onClick = aoAlternar),
        verticalAlignment = Alignment.Top
    ) {
        CaixaDeMarcar(marcada = marcado, aoAlternar = aoAlternar)
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Li e aceito os Termos de uso e a Política de privacidade.",
            style = FinanSmartTheme.type.Caption,
            color = c.ink2
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CadastroPreview() {
    FinanSmartTheme { CadastroScreen() }
}
