package com.example.finansmart.ui.screens.login

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.components.BotaoPrimario
import com.example.finansmart.ui.components.BotaoSecundario
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

@Composable
fun LoginScreen(
    onEntrar: () -> Unit = {},
    onCriarConta: () -> Unit = {},
    onEsqueciSenha: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg)
            .safeDrawingPadding()
    ) {

        /* ---------- parte que rola ---------- */
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingAuth)
        ) {
            Spacer(Modifier.height(36.dp))

            LogoPequeno()

            Spacer(Modifier.height(26.dp))

            Text(text = "Bem-vinda de volta", style = t.ScreenTitle, color = c.ink)

            Spacer(Modifier.height(Dimens.Space8))

            Text(
                text = "Entre para continuar cuidando das suas finanças.",
                style = t.Body,
                color = c.ink2
            )

            Spacer(Modifier.height(Dimens.Space32))

            CampoTexto(
                rotulo = "E-mail",
                valor = email,
                aoMudarValor = { email = it },
                placeholder = "maria@email.com",
                icone = "✉️",
                tipoTeclado = KeyboardType.Email
            )

            Spacer(Modifier.height(15.dp))

            CampoTexto(
                rotulo = "Senha",
                valor = senha,
                aoMudarValor = { senha = it },
                placeholder = "••••••••",
                icone = "🔒",
                ocultarTexto = !senhaVisivel,
                tipoTeclado = KeyboardType.Password,
                conteudoFinal = {
                    Text(
                        text = if (senhaVisivel) "🙈" else "👁",
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { senhaVisivel = !senhaVisivel }
                    )
                }
            )

            Spacer(Modifier.height(15.dp))

            Text(
                text = "Esqueci minha senha",
                style = t.FieldLabel.copy(fontSize = 13.sp),
                color = c.primaryInk,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onEsqueciSenha() }
            )

            Spacer(Modifier.height(Dimens.Space18))

            BotaoPrimario(texto = "Entrar", aoClicar = onEntrar)

            Spacer(Modifier.height(22.dp))

            DivisorOu()

            Spacer(Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotaoSecundario("🔵 Google", aoClicar = {}, modifier = Modifier.weight(1f))
                // Não faz sentido esse botão de Face Id
                BotaoSecundario("🆔 Face ID", aoClicar = {}, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.Space20))
        }

        /* ---------- rodapé fixo ---------- */
        Rodape(onCriarConta = onCriarConta)
    }
}

@Composable
private fun DivisorOu() {
    val c = FinanSmartTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(c.line))
        Text(
            text = "ou",
            style = FinanSmartTheme.type.ListSubtitle,
            color = c.ink3,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Box(Modifier.weight(1f).height(1.dp).background(c.line))
    }
}

@Composable
private fun Rodape(onCriarConta: () -> Unit) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.ScreenPaddingAuth,
                vertical = 0.dp
            )
            .padding(top = Dimens.Space20, bottom = 34.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = " Não possui uma conta? ", style = t.Footer, color = c.ink2)
        Text(
            text = "Criar conta",
            style = t.Footer.copy(fontWeight = FontWeight.SemiBold),
            color = c.primaryInk,
            modifier = Modifier.clickable { onCriarConta() }
        )
    }
}

@Composable
private fun LogoPequeno() {
    val c = FinanSmartTheme.colors
    Box(
        modifier = Modifier
            .size(Dimens.LogoSmall)
            .clip(RoundedCornerShape(Dimens.RadiusLogoSmall))
            .background(c.primaryFill),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(20.dp)
                .rotate(45f)
                .border(3.dp, Color.White, RoundedCornerShape(6.dp))
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginPreview() {
    FinanSmartTheme { LoginScreen() }
}