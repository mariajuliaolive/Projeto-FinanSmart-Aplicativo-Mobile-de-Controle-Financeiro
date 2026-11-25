package com.example.finansmart.ui.screens.dashboard

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.*
import com.example.finansmart.ui.components.BotaoOlho
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.components.ItemTransacao
import com.example.finansmart.ui.components.MarcadorDeLegenda
import com.example.finansmart.ui.theme.CatOutros
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 04 - DASHBOARD */

@Composable
fun DashboardScreen(
    onNovaTransacao: () -> Unit = {},
    onNotificacoes: () -> Unit = {},
    onVerTodas: () -> Unit = {},
    onAbrirTransacao: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var saldoVisivel by remember { mutableStateOf(true) }

    val perfil = Repositorio.perfil.value
    // O SALDO e de todos os tempos: e o dinheiro que a pessoa tem.
    // Receitas, despesas e o grafico sao DO MES ATUAL, como dizem os titulos.
    val mesAtual = MesAno.atual()
    val doMes = Repositorio.transacoes.doMes(mesAtual)
    val receitas = doMes.somaReceitas()
    val despesas = doMes.somaDespesas()
    val saldo = Repositorio.saldo()
    val porCategoria = doMes.despesasPorCategoria()
    val ultimas = Repositorio.transacoes.take(3)

    val percentualGasto = if (receitas > 0) (despesas / receitas * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        /* ---- saudacao ---- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(Dimens.IconBoxLarge)
                    .clip(RoundedCornerShape(Dimens.RadiusPill))
                    .background(c.primarySoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = perfil.iniciais,
                    style = t.MetaNome,
                    color = c.primaryInk
                )
            }
            Spacer(Modifier.width(Dimens.Space12))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Olá, ${perfil.primeiroNome}! 👋",
                    style = t.Greeting,
                    color = c.ink
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Você gastou $percentualGasto% da sua renda este mês.",
                    style = t.Caption,
                    color = c.ink2
                )
            }
            Box(
                Modifier
                    .size(Dimens.IconBoxLarge)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
                    .clickable { onNotificacoes() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔔", fontSize = 17.sp)
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 9.dp, end = 10.dp)
                        .size(7.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusPill))
                        .background(Color(0xFFEF4444))
                )
            }
        }

        /* ---- cartao do saldo ---- */
        Column(
            modifier = Modifier
                .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = Dimens.Space18)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusCardLarge))
                .background(c.panel)
                .padding(Dimens.Space22)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Saldo disponível",
                    style = t.Caption,
                    color = c.panelSub,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(Dimens.RadiusPill))
                        .background(c.panel2),
                    contentAlignment = Alignment.Center
                ) {
                    BotaoOlho(
                        visivel = saldoVisivel,
                        aoAlternar = { saldoVisivel = !saldoVisivel },
                        cor = Color.White,
                        tamanho = 17.dp
                    )
                }
            }
            Spacer(Modifier.height(Dimens.Space8))
            Text(
                text = if (saldoVisivel) formatarMoeda(saldo) else "•  •  •  •  •",
                style = t.Balance,
                color = Color.White
            )
            Spacer(Modifier.height(Dimens.Space18))
            // "Enviar" saiu: o FinanSmart e um controle de gastos, nao um
            // banco -- nao ha para onde enviar dinheiro. O botao de lancar
            // uma transacao passa a ocupar a largura toda.
            BotaoDoCartao(
                texto = "＋ Nova transação",
                fundo = c.primaryFill,
                aoClicar = onNovaTransacao,
                modifier = Modifier.fillMaxWidth()
            )
        }

        /* ---- receitas e despesas ---- */
        Row(
            modifier = Modifier
                .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
        ) {
            CartaoResumo("Receitas", formatarMoeda(receitas), "↓", c.pos, c.posSoft, Modifier.weight(1f))
            CartaoResumo("Despesas", formatarMoeda(despesas), "↑", c.neg, c.negSoft, Modifier.weight(1f))
        }

        /* ---- gastos do mes ---- */
        Cartao(
            modifier = Modifier.padding(
                start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = Dimens.Space18
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Gastos deste mês",
                    style = t.SectionTitle,
                    color = c.ink,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = MESES_LONGOS[mesAtual.mes - 1],
                    style = t.MonoLabel,
                    color = c.primaryInk
                )
            }

            Spacer(Modifier.height(Dimens.Space16))

            // A barra empilhada: cada categoria ocupa sua fatia do total.
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(Dimens.BarThickness)
                    .clip(RoundedCornerShape(Dimens.RadiusPill))
            ) {
                porCategoria.forEach { (categoria, valor) ->
                    val fatia = if (despesas > 0) (valor / despesas).toFloat() else 0f
                    if (fatia > 0f) {
                        Box(
                            Modifier
                                .weight(fatia)
                                .fillMaxHeight()
                                .background(categoria.cor)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.Space16))

            porCategoria.take(4).forEach { (categoria, valor) ->
                LinhaDeCategoria(categoria.rotulo, categoria.cor, valor, destaque = true)
                Spacer(Modifier.height(9.dp))
            }
            val resto = porCategoria.drop(4)
            if (resto.isNotEmpty()) {
                LinhaDeCategoria(
                    nome = resto.joinToString(", ") { it.first.rotulo },
                    cor = CatOutrosClaro,
                    valor = resto.sumOf { it.second },
                    destaque = false
                )
            }
        }

        /* ---- ultimas transacoes ---- */
        Column(
            modifier = Modifier.padding(
                start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = Dimens.Space18
            )
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Últimas transações",
                    style = t.SectionTitle,
                    color = c.ink,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Ver todas",
                    style = t.StatLabel.copy(fontSize = 12.5.sp),
                    color = c.primaryInk,
                    modifier = Modifier.clickable { onVerTodas() }
                )
            }
            Spacer(Modifier.height(10.dp))
            ultimas.forEach { transacao ->
                ItemTransacao(
                    transacao = transacao,
                    aoClicar = { onAbrirTransacao(transacao.id) },
                    subtitulo = legendaDeData(transacao)
                )
                Spacer(Modifier.height(Dimens.Space8))
            }
        }

        Spacer(Modifier.height(22.dp))
    }
}

private val CatOutrosClaro = CatOutros

private fun legendaDeData(transacao: Transacao): String {
    val quando = when (transacao.dia) {
        19 -> "Hoje"
        18 -> "Ontem"
        else -> "%02d/%02d".format(transacao.dia, transacao.mes)
    }
    val categoria = if (transacao.tipo == TipoTransacao.RECEITA) "Receita"
    else transacao.categoria.rotulo
    return "$quando — $categoria"
}

@Composable
private fun BotaoDoCartao(
    texto: String,
    fundo: Color,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .height(Dimens.ButtonHeightInline)
            .clip(RoundedCornerShape(Dimens.RadiusField))
            .background(fundo)
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = FinanSmartTheme.type.ButtonSmall, color = Color.White)
    }
}

@Composable
private fun CartaoResumo(
    rotulo: String,
    valor: String,
    seta: String,
    cor: Color,
    corFundoIcone: Color,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    Column(
        modifier
            .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusCardSmall))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(Dimens.IconBoxSmall)
                    .clip(RoundedCornerShape(Dimens.Space8))
                    .background(corFundoIcone),
                contentAlignment = Alignment.Center
            ) {
                Text(text = seta, fontSize = 12.sp, color = cor)
            }
            Spacer(Modifier.width(7.dp))
            Text(text = rotulo, style = FinanSmartTheme.type.StatLabel, color = c.ink2)
        }
        Spacer(Modifier.height(Dimens.Space8))
        Text(text = valor, style = FinanSmartTheme.type.CardValue, color = cor)
    }
}

@Composable
private fun LinhaDeCategoria(nome: String, cor: Color, valor: Double, destaque: Boolean) {
    val c = FinanSmartTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        MarcadorDeLegenda(cor = cor, redondo = true)
        Spacer(Modifier.width(Dimens.Space8))
        Text(
            text = nome,
            style = FinanSmartTheme.type.ListValue.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                fontSize = 13.sp
            ),
            color = if (destaque) c.ink else c.ink2
        )
        Spacer(Modifier.width(2.dp))
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
                .height(1.dp)
                .background(c.dots)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = formatarMoedaCurta(valor),
            style = FinanSmartTheme.type.MonoValue,
            color = if (destaque) c.ink else c.ink2
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DashboardPreview() {
    FinanSmartTheme { DashboardScreen() }
}
