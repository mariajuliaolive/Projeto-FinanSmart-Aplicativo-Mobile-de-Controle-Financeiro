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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Repositorio
import com.example.finansmart.data.formatarMoeda
import com.example.finansmart.data.lerValorDigitado
import com.example.finansmart.ui.components.CabecalhoTela
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.components.Etiqueta
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 12 - MEU PERFIL */

@Composable
fun PerfilScreen(
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    val perfilAtual = Repositorio.perfil.value

    // Copias locais: so vao para o repositorio quando a pessoa salvar.
    var nome by remember { mutableStateOf(perfilAtual.nome) }
    var telefone by remember { mutableStateOf(perfilAtual.telefone) }
    var renda by remember { mutableStateOf(formatarMoeda(perfilAtual.rendaMensal)) }

    Column(modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(6.dp))

            CabecalhoTela(titulo = "Meu perfil", aoVoltar = onVoltar)

            /* ---- avatar ---- */
            Spacer(Modifier.height(26.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.size(96.dp)) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(Dimens.RadiusPill))
                            .background(c.primaryFill),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = perfilAtual.iniciais,
                            style = t.MetaNome.copy(fontSize = 32.sp),
                            color = Color.White
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(RoundedCornerShape(Dimens.RadiusPill))
                            .background(c.surface)
                            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusPill)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📷", fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(Dimens.Space12))
                Text(
                    text = perfilAtual.nome,
                    style = t.MetaNome.copy(fontSize = 18.sp),
                    color = c.ink
                )
                Spacer(Modifier.height(Dimens.Space12))
                // Agora vem do Firebase: so e "verificada" quem clicou no
                // link do e-mail de confirmacao enviado no cadastro.
                if (perfilAtual.verificada) {
                    Etiqueta(
                        texto = "Conta verificada",
                        corTexto = c.pos,
                        corFundo = c.posSoft
                    )
                } else {
                    Etiqueta(
                        texto = "E-mail não confirmado",
                        corTexto = c.neg,
                        corFundo = c.negSoft
                    )
                }
            }

            /* ---- campos ---- */
            Spacer(Modifier.height(28.dp))
            CampoTexto(
                rotulo = "Nome completo",
                valor = nome,
                aoMudarValor = { nome = it },
                placeholder = "Maria Oliveira"
            )

            Spacer(Modifier.height(13.dp))
            // O e-mail nao e editavel no prototipo: fundo cinza e cadeado.
            CampoBloqueado(rotulo = "E-mail", valor = perfilAtual.email)

            Spacer(Modifier.height(13.dp))
            CampoTexto(
                rotulo = "Telefone",
                valor = telefone,
                aoMudarValor = { telefone = it },
                placeholder = "(11) 98888-1234",
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(Modifier.height(13.dp))
            CampoTexto(
                rotulo = "Renda mensal",
                valor = renda,
                aoMudarValor = { renda = it },
                placeholder = "R$ 3.000,00",
                tipoTeclado = KeyboardType.Decimal
            )

            Spacer(Modifier.height(Dimens.Space20))
        }

        /* ---- cancelar e salvar ---- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(c.surface)
                .padding(
                    start = Dimens.ScreenPadding, end = Dimens.ScreenPadding,
                    top = 14.dp, bottom = 26.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(Dimens.ButtonHeight)
                    .clip(RoundedCornerShape(Dimens.RadiusButton))
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusButton))
                    .clickable { onVoltar() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancelar",
                    style = t.Button.copy(fontSize = 15.sp),
                    color = c.ink2
                )
            }
            Box(
                Modifier
                    .weight(2f)
                    .height(Dimens.ButtonHeight)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(Dimens.RadiusButton),
                        ambientColor = c.primary,
                        spotColor = c.primary
                    )
                    .clip(RoundedCornerShape(Dimens.RadiusButton))
                    .background(c.primaryFill)
                    .clickable {
                        Repositorio.atualizarPerfil(
                            perfilAtual.copy(
                                nome = nome.trim(),
                                telefone = telefone.trim(),
                                rendaMensal = lerValorDigitado(renda)
                            )
                        )
                        onVoltar()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Salvar alterações",
                    style = t.Button.copy(fontSize = 15.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CampoBloqueado(rotulo: String, valor: String) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    Column {
        Text(text = rotulo, style = t.FieldLabel, color = c.ink2)
        Spacer(Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(Dimens.RadiusField))
                .background(c.surface2)
                .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valor,
                style = t.FieldText,
                color = c.ink2,
                modifier = Modifier.weight(1f)
            )
            Text(text = "🔒", fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PerfilPreview() {
    FinanSmartTheme { PerfilScreen() }
}
