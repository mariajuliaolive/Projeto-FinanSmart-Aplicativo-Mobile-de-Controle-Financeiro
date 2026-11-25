package com.example.finansmart.ui.screens.transacoes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.*
import com.example.finansmart.ui.components.BarraDeProgresso
import com.example.finansmart.ui.components.Divisoria
import com.example.finansmart.ui.components.Etiqueta
import com.example.finansmart.ui.components.ForcarIconesClarosNaStatusBar
import com.example.finansmart.ui.components.MarcadorDeLegenda
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 10 - DETALHE DA TRANSACAO */

@Composable
fun DetalheTransacaoScreen(
    transacaoId: Long,
    onVoltar: () -> Unit = {},
    onEditar: () -> Unit = {},
    onExcluiu: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // O topo desta tela e escuro nos dois temas.
    ForcarIconesClarosNaStatusBar()

    val transacao = Repositorio.buscarTransacao(transacaoId)

    if (transacao == null) {
        // Pode acontecer se a transacao foi excluida e a tela reabriu.
        Box(
            modifier.fillMaxSize().background(c.bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Transação não encontrada.",
                style = t.Body,
                color = c.ink2
            )
        }
        return
    }

    val ehReceita = transacao.tipo == TipoTransacao.RECEITA
    // O mes DA TRANSACAO (antes: setembro fixo, com "R$ 400 planejados"
    // inventados). Agora comparamos com o total real de despesas do mes.
    val mesDaTransacao = MesAno(transacao.mes, transacao.ano)
    val despesasDoMes = Repositorio.transacoes.doMes(mesDaTransacao)
        .filter { it.tipo == TipoTransacao.DESPESA }
    val gastoNaCategoria = despesasDoMes
        .filter { it.categoria == transacao.categoria }
        .sumOf { it.valor }
    val totalDoMes = despesasDoMes.sumOf { it.valor }
    val fatiaDoMes = if (totalDoMes > 0) gastoNaCategoria / totalDoMes else 0.0

    Column(modifier.fillMaxSize().background(c.bg)) {

        /* ---- topo escuro ---- */
        Column(
            Modifier
                .fillMaxWidth()
                .background(c.panel)
                .statusBarsPadding()
                .padding(bottom = 30.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = Dimens.Space16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoDoTopo(texto = "←", aoClicar = onVoltar)
                Text(
                    text = "Transação",
                    style = t.SectionTitle,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                BotaoDoTopo(texto = "⋯", aoClicar = {})
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(c.panel2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = transacao.emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(text = transacao.descricao, style = t.DetalheValor, color = c.panelSub)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatarComSinal(transacao.valor, transacao.tipo),
                    style = t.ValorDetalhe,
                    color = Color.White
                )
                Spacer(Modifier.height(10.dp))
                Etiqueta(
                    texto = if (ehReceita) "Receita · Confirmada" else "Despesa · Confirmada",
                    corTexto = if (ehReceita) c.pos else c.neg,
                    corFundo = if (ehReceita) c.posSoft else c.negSoft
                )
            }
        }

        /* ---- detalhes ---- */
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(22.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(18.dp))
            ) {
                LinhaDeDetalhe(
                    rotulo = "Data",
                    valor = transacao.dataFormatada +
                            if (transacao.hora.isNotBlank()) " · ${transacao.hora}" else ""
                )
                Divisoria()
                LinhaDeDetalhe(rotulo = "Categoria") {
                    MarcadorDeLegenda(cor = transacao.categoria.cor, redondo = true)
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = transacao.categoria.rotulo,
                        style = FinanSmartTheme.type.DetalheValor,
                        color = c.ink
                    )
                }
                Divisoria()
                LinhaDeDetalhe(
                    rotulo = "Forma de pagamento",
                    valor = transacao.formaPagamento.ifBlank { "—" }
                )
                Divisoria()
                LinhaDeDetalhe(
                    rotulo = "Observação",
                    valor = transacao.observacao.ifBlank { "—" }
                )
            }

            /* ---- resumo da categoria ---- */
            if (!ehReceita) {
                Spacer(Modifier.height(Dimens.Space16))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(c.primarySoft)
                        .padding(horizontal = 18.dp, vertical = Dimens.Space16)
                ) {
                    Text(
                        text = "${transacao.categoria.rotulo} em ${mesDaTransacao.nomeLongo.lowercase()}",
                        style = t.ListTitle.copy(fontSize = 13.sp),
                        color = c.primaryInk
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatarMoedaCurta(gastoNaCategoria),
                            style = t.MetaPercentual.copy(fontSize = 20.sp),
                            color = c.ink
                        )
                        Spacer(Modifier.width(Dimens.Space8))
                        Text(
                            text = "${(fatiaDoMes * 100).toInt()}% dos gastos do mês",
                            style = t.Caption,
                            color = c.ink2
                        )
                    }
                    Spacer(Modifier.height(Dimens.Space12))
                    BarraDeProgresso(
                        progresso = fatiaDoMes.toFloat(),
                        altura = 8.dp
                    )
                }
            }

            /* ---- editar e excluir ---- */
            Spacer(Modifier.height(Dimens.Space18))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotaoDeAcao(
                    texto = "✏️ Editar",
                    cor = c.ink,
                    corBorda = c.line,
                    aoClicar = onEditar,   // antes: {} -- o botao nao fazia nada
                    modifier = Modifier.weight(1f)
                )
                BotaoDeAcao(
                    texto = "🗑 Excluir",
                    cor = c.neg,
                    corBorda = c.neg,
                    aoClicar = {
                        Repositorio.removerTransacao(transacao.id)
                        onExcluiu()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(26.dp))
        }
    }
}

@Composable
private fun BotaoDoTopo(texto: String, aoClicar: () -> Unit) {
    val c = FinanSmartTheme.colors
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Dimens.RadiusField))
            .background(c.panel2)
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
private fun LinhaDeDetalhe(rotulo: String, valor: String) {
    LinhaDeDetalhe(rotulo) {
        Text(
            text = valor,
            style = FinanSmartTheme.type.DetalheValor,
            color = FinanSmartTheme.colors.ink
        )
    }
}

@Composable
private fun LinhaDeDetalhe(rotulo: String, direita: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Space16, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rotulo,
            style = FinanSmartTheme.type.DetalheRotulo,
            color = FinanSmartTheme.colors.ink2,
            modifier = Modifier.weight(1f)
        )
        direita()
    }
}

@Composable
private fun BotaoDeAcao(
    texto: String,
    cor: Color,
    corBorda: Color,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier
            .height(Dimens.ButtonHeightSmall)
            .clip(RoundedCornerShape(Dimens.RadiusButton))
            .background(c.surface)
            .border(Dimens.BorderWidth, corBorda, RoundedCornerShape(Dimens.RadiusButton))
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = FinanSmartTheme.type.ListValue, color = cor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetalhePreview() {
    FinanSmartTheme { DetalheTransacaoScreen(transacaoId = 1L) }
}
