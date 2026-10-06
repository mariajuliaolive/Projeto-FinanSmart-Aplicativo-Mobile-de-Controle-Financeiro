package com.example.finansmart.ui.screens.relatorios

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.*
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.components.MarcadorDeLegenda
import com.example.finansmart.ui.components.Pilula
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 07 - RELATORIOS */

@Composable
fun RelatoriosScreen(modifier: Modifier = Modifier) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    val receitas = Repositorio.totalReceitas()
    val despesas = Repositorio.totalDespesas()
    val saldo = Repositorio.saldo()
    val porCategoria = Repositorio.gastosPorCategoria()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Spacer(Modifier.height(6.dp))

        // A caixinha "Este mes" saiu: nao filtrava nada, porque o app so
        // tem lancamentos de um mes. O periodo passou a ser o subtitulo.
        Text(text = "Relatórios", style = t.TituloLista, color = c.ink)
        Spacer(Modifier.height(4.dp))
        Text(
            text = MESES_LONGOS[8].lowercase().replaceFirstChar { it.uppercase() } + " de 2026",
            style = t.Body,
            color = c.ink2
        )

        /* ---- cards do topo ---- */
        Spacer(Modifier.height(Dimens.Space20))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CartaoPequeno("Receitas", formatarMoedaCurta(receitas), c.pos, Modifier.weight(1f))
            CartaoPequeno("Despesas", formatarMoedaCurta(despesas), c.neg, Modifier.weight(1f))
        }

        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
                .background(c.panel)
                .padding(Dimens.Space16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saldo do período",
                style = t.Caption,
                color = c.panelSub,
                modifier = Modifier.weight(1f)
            )
            Text(text = formatarMoeda(saldo), style = t.SaldoPeriodo, color = Color.White)
        }

        /* ---- rosca de categorias ---- */
        Spacer(Modifier.height(Dimens.Space18))
        Cartao {
            Text(text = "Gastos por categoria", style = t.SectionTitle, color = c.ink)
            Spacer(Modifier.height(Dimens.Space16))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoscaDeCategorias(
                    fatias = porCategoria,
                    total = despesas,
                    corDoMiolo = c.surface,
                    modifier = Modifier.size(132.dp)
                )

                Spacer(Modifier.height(Dimens.Space18))

                // Legenda embaixo, em duas colunas largas: assim os nomes
                // cabem inteiros e nao ficam por baixo do grafico.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    porCategoria.chunked(2).forEach { dupla ->
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
                            dupla.forEach { (categoria, _) ->
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MarcadorDeLegenda(cor = categoria.cor)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = categoria.rotulo,
                                        style = t.Rotulo115,
                                        color = c.ink,
                                        maxLines = 1
                                    )
                                }
                            }
                            if (dupla.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        /* ---- barras receitas x despesas ---- */
        Spacer(Modifier.height(14.dp))
        Cartao {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Receitas × despesas",
                    style = t.SectionTitle,
                    color = c.ink,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "JUN–SET", style = t.MonoLabel, color = c.ink3)
            }
            Spacer(Modifier.height(Dimens.Space18))

            /*
             * Os quatro meses do prototipo. Como o app so tem os dados de
             * setembro, os tres meses anteriores ficam com os valores do
             * desenho -- um historico de exemplo.
             */
            val meses = listOf(
                Mes("Jun", 3000.0, 1180.0),
                Mes("Jul", 3000.0, 1780.0),
                Mes("Ago", 3000.0, 1400.0),
                Mes("Set", receitas, despesas),
            )
            val maior = meses.maxOf { maxOf(it.receita, it.despesa) }.coerceAtLeast(1.0)

            Row(
                modifier = Modifier.fillMaxWidth().height(132.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space16),
                verticalAlignment = Alignment.Bottom
            ) {
                meses.forEachIndexed { indice, mes ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.height(104.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            BarraVertical((mes.receita / maior).toFloat(), c.pos)
                            BarraVertical((mes.despesa / maior).toFloat(), c.neg)
                        }
                        Spacer(Modifier.height(Dimens.Space8))
                        Text(
                            text = mes.nome,
                            style = t.Rotulo11.copy(
                                fontWeight = if (indice == meses.lastIndex)
                                    androidx.compose.ui.text.font.FontWeight.SemiBold
                                else androidx.compose.ui.text.font.FontWeight.Medium
                            ),
                            color = if (indice == meses.lastIndex) c.ink else c.ink2
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.Space12))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space16)) {
                LegendaSimples("Receitas", c.pos)
                LegendaSimples("Despesas", c.neg)
            }
        }

        /* ---- evolucao (linha) ---- */
        Spacer(Modifier.height(14.dp))
        Cartao {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Evolução dos gastos",
                    style = t.SectionTitle,
                    color = c.ink,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "−32% vs. ago", style = t.Rotulo115, color = c.pos)
            }
            LinhaDeEvolucao(
                pontos = listOf(0.78f, 0.58f, 0.67f, 0.36f, 0.22f),
                corLinha = c.primary,
                corArea = c.primarySoft,
                corPonto = c.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 14.dp)
            )
            Spacer(Modifier.height(Dimens.Space8))
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4").forEach { rotulo ->
                    Text(
                        text = rotulo,
                        style = t.Rotulo11,
                        color = c.ink3,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))
    }
}

private data class Mes(val nome: String, val receita: Double, val despesa: Double)

@Composable
private fun RowScope.BarraVertical(fracao: Float, cor: Color) {
    Box(
        Modifier
            .width(14.dp)
            .fillMaxHeight(fracao.coerceIn(0.05f, 1f))
            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
            .background(cor)
    )
}

@Composable
private fun LegendaSimples(texto: String, cor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MarcadorDeLegenda(cor = cor)
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            style = FinanSmartTheme.type.Rotulo115,
            color = FinanSmartTheme.colors.ink2
        )
    }
}

@Composable
private fun CartaoPequeno(
    rotulo: String,
    valor: String,
    cor: Color,
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
        Text(text = rotulo, style = FinanSmartTheme.type.Rotulo115, color = c.ink2)
        Spacer(Modifier.height(5.dp))
        Text(text = valor, style = FinanSmartTheme.type.ValorMedio, color = cor)
    }
}

/**
 * A rosca colorida. Desenhada com Canvas: cada categoria vira um arco
 * proporcional ao quanto representa do total.
 */
@Composable
private fun RoscaDeCategorias(
    fatias: List<Pair<Categoria, Double>>,
    total: Double,
    corDoMiolo: Color,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            var inicio = -90f
            fatias.forEach { (categoria, valor) ->
                val angulo = if (total > 0) (valor / total * 360f).toFloat() else 0f
                drawArc(
                    color = categoria.cor,
                    startAngle = inicio,
                    sweepAngle = angulo,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height)
                )
                inicio += angulo
            }
        }
        Box(
            Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(corDoMiolo),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "total",
                    style = FinanSmartTheme.type.NavLabel.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = c.ink2
                )
                Text(
                    text = formatarMoedaCurta(total),
                    style = FinanSmartTheme.type.ListTitle.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = c.ink,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

/** A linha de evolucao com a area pintada embaixo. */
@Composable
private fun LinhaDeEvolucao(
    pontos: List<Float>,
    corLinha: Color,
    corArea: Color,
    corPonto: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        if (pontos.size < 2) return@Canvas
        val passo = size.width / (pontos.size - 1)
        val coords = pontos.mapIndexed { indice, valor ->
            Offset(passo * indice, size.height * (1f - valor))
        }

        val area = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            coords.drop(1).forEach { lineTo(it.x, it.y) }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(area, corArea)

        val linha = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            coords.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            linha,
            corLinha,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        coords.forEach { ponto ->
            drawCircle(corPonto, radius = 4.dp.toPx(), center = ponto)
            drawCircle(
                corLinha,
                radius = 4.dp.toPx(),
                center = ponto,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RelatoriosPreview() {
    FinanSmartTheme { RelatoriosScreen() }
}
