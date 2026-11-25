package com.example.finansmart.ui.screens.transacoes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.*
import com.example.finansmart.ui.components.BotaoPrimario
import com.example.finansmart.ui.components.ItemTransacao
import com.example.finansmart.ui.components.Pilula
import com.example.finansmart.ui.components.RotuloSecao
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * TELA 05 - TRANSACOES
 *
 *
 */

@Composable
fun TransacoesScreen(
    onNovaTransacao: () -> Unit = {},
    onAbrirTransacao: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var busca by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf<TipoTransacao?>(null) }
    var filtroCategoria by remember { mutableStateOf<Categoria?>(null) }

    val todas = Repositorio.transacoes

    val filtradas = todas.filter { transacao ->
        val casaBusca = busca.isBlank() ||
                transacao.descricao.contains(busca, ignoreCase = true) ||
                transacao.categoria.rotulo.contains(busca, ignoreCase = true)
        val casaTipo = filtroTipo == null || transacao.tipo == filtroTipo
        val casaCategoria = filtroCategoria == null || transacao.categoria == filtroCategoria
        casaBusca && casaTipo && casaCategoria
    }

    val entradas = filtradas.filter { it.tipo == TipoTransacao.RECEITA }.sumOf { it.valor }
    val saidas = filtradas.filter { it.tipo == TipoTransacao.DESPESA }.sumOf { it.valor }

    val temFiltro = busca.isNotBlank() || filtroTipo != null || filtroCategoria != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Spacer(Modifier.height(6.dp))

        /* ---- titulo e botao + ---- */
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Minhas transações",
                style = t.TituloLista,
                color = c.ink,
                modifier = Modifier.weight(1f)
            )
            Box(
                Modifier
                    .size(Dimens.IconBoxMedium)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.primaryFill)
                    .clickable { onNovaTransacao() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "＋", fontSize = 20.sp, color = Color.White)
            }
        }

        /* ---- busca ---- */
        Spacer(Modifier.height(Dimens.Space18))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔍", fontSize = 14.sp, modifier = Modifier.alpha(0.5f))
                Spacer(Modifier.width(9.dp))
                BasicTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = t.Body.copy(color = c.ink),
                    cursorBrush = SolidColor(c.primary),
                    decorationBox = { campo ->
                        if (busca.isEmpty()) {
                            Text(text = "Buscar transação", style = t.Body, color = c.ink3)
                        }
                        campo()
                    }
                )
            }
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚙️", fontSize = 16.sp)
            }
        }

        /* ---- filtros ---- */
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
            Pilula(
                texto = "Este mês ▾",
                selecionada = true,
                aoClicar = {}
            )
            Pilula(
                texto = (filtroCategoria?.rotulo ?: "Categoria") + " ▾",
                selecionada = filtroCategoria != null,
                aoClicar = {
                    // Percorre as categorias em ordem a cada toque.
                    val lista = Categoria.entries.filter { it != Categoria.RECEITA }
                    val atual = lista.indexOf(filtroCategoria)
                    filtroCategoria = if (atual == lista.lastIndex) null else lista[atual + 1]
                },
                destaque = true
            )
            Pilula(
                texto = when (filtroTipo) {
                    TipoTransacao.RECEITA -> "Receitas ▾"
                    TipoTransacao.DESPESA -> "Despesas ▾"
                    null -> "Tipo ▾"
                },
                selecionada = filtroTipo != null,
                aoClicar = {
                    filtroTipo = when (filtroTipo) {
                        null -> TipoTransacao.RECEITA
                        TipoTransacao.RECEITA -> TipoTransacao.DESPESA
                        TipoTransacao.DESPESA -> null
                    }
                },
                destaque = true
            )
        }

        /* ---- entradas e saidas ---- */
        Spacer(Modifier.height(Dimens.Space16))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CaixaDeTotal("Entradas", formatarMoeda(entradas), c.pos, c.posSoft, Modifier.weight(1f))
            CaixaDeTotal("Saídas", formatarMoeda(saidas), c.neg, c.negSoft, Modifier.weight(1f))
        }

        /* ---- lista ou estado vazio ---- */
        if (filtradas.isEmpty()) {
            EstadoVazio(
                busca = busca,
                temFiltro = temFiltro,
                aoLimpar = {
                    busca = ""
                    filtroTipo = null
                    filtroCategoria = null
                },
                aoAdicionar = onNovaTransacao
            )
        } else {
            // Agrupa por dia, mantendo a ordem em que ja estao.
            val porDia = filtradas.groupBy { it.chaveDoDia }
            porDia.forEach { (_, doDia) ->
                Spacer(Modifier.height(24.dp))
                RotuloSecao(texto = tituloDoDia(doDia.first()))
                Spacer(Modifier.height(Dimens.Space8))
                doDia.forEachIndexed { indice, transacao ->
                    if (indice > 0) Spacer(Modifier.height(Dimens.Space8))
                    ItemTransacao(
                        transacao = transacao,
                        aoClicar = { onAbrirTransacao(transacao.id) },
                        subtitulo = subtituloDaLista(transacao)
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))
    }
}

private fun tituloDoDia(transacao: Transacao): String {
    val mes = MESES_CURTOS[(transacao.mes - 1).coerceIn(0, 11)].uppercase()
    val dia = "%02d".format(transacao.dia)
    // Antes: dia 19 era sempre "HOJE" (a data do prototipo).
    // Agora compara dia, mes E ano com a data do celular.
    val data = Triple(transacao.dia, transacao.mes, transacao.ano)
    return when (data) {
        hoje() -> "HOJE · $dia $mes"
        ontem() -> "ONTEM · $dia $mes"
        else -> "$dia $mes"
    }
}

private fun subtituloDaLista(transacao: Transacao): String {
    if (transacao.tipo == TipoTransacao.RECEITA) {
        return "Receita · " + transacao.formaPagamento.lowercase()
    }
    val detalhe = if (transacao.formaPagamento.contains("Débito", ignoreCase = true)) {
        "débito automático"
    } else {
        transacao.hora
    }
    return transacao.categoria.rotulo + " · " + detalhe
}

@Composable
private fun CaixaDeTotal(
    rotulo: String,
    valor: String,
    cor: Color,
    fundo: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(fundo)
            .padding(horizontal = 14.dp, vertical = Dimens.Space12)
    ) {
        Text(text = rotulo, style = FinanSmartTheme.type.Rotulo115, color = cor)
        Spacer(Modifier.height(3.dp))
        Text(
            text = valor,
            style = FinanSmartTheme.type.SectionTitle.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = cor
        )
    }
}

/** Tela 13 do prototipo: a busca nao achou nada. */
@Composable
private fun EstadoVazio(
    busca: String,
    temFiltro: Boolean,
    aoLimpar: () -> Unit,
    aoAdicionar: () -> Unit,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(c.surface2),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔍", fontSize = 28.sp)
        }
        Spacer(Modifier.height(Dimens.Space18))
        Text(
            text = "Nenhuma transação encontrada",
            style = t.MetaNome,
            color = c.ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.Space8))
        Text(
            text = if (busca.isNotBlank()) {
                "Não achamos nada para “$busca”. Tente outro termo ou limpe os filtros."
            } else {
                "Nenhuma transação com esses filtros. Tente limpar e começar de novo."
            },
            style = t.Body,
            color = c.ink2,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Dimens.Space22))
        if (temFiltro) {
            Box(
                Modifier
                    .height(Dimens.ButtonHeightSmall)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
                    .clickable(onClick = aoLimpar)
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Limpar filtros", style = t.Button, color = c.ink)
            }
            Spacer(Modifier.height(10.dp))
        }
        BotaoPrimario(texto = "Adicionar transação", aoClicar = aoAdicionar)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransacoesPreview() {
    FinanSmartTheme { TransacoesScreen() }
}
