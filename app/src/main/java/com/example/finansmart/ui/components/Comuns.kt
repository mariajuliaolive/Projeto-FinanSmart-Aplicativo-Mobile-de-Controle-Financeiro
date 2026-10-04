package com.example.finansmart.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Transacao
import com.example.finansmart.data.TipoTransacao
import com.example.finansmart.data.formatarComSinal
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * PECAS QUE APARECEM EM VARIAS TELAS.
 */

/**
 * Um item da lista de transacoes: emoji num quadrado, descricao e
 * subtitulo, valor colorido a direita.
 * Telas 04 e 05.
 */
@Composable
fun ItemTransacao(
    transacao: Transacao,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    val ehReceita = transacao.tipo == TipoTransacao.RECEITA

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusCardSmall))
            .clickable(onClick = aoClicar)
            .padding(Dimens.Space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.IconBoxMedium)
                .clip(RoundedCornerShape(Dimens.RadiusField))
                .background(if (ehReceita) c.posSoft else c.primarySoft),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transacao.emoji, fontSize = 18.sp)
        }

        Spacer(Modifier.width(Dimens.Space12))

        Column(Modifier.weight(1f)) {
            Text(
                text = transacao.descricao,
                style = t.ListTitle,
                color = c.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = subtitulo ?: padraoSubtitulo(transacao),
                style = t.ListSubtitle,
                color = c.ink2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(Dimens.Space8))

        Text(
            text = formatarComSinal(transacao.valor, transacao.tipo),
            style = t.ListValue,
            color = if (ehReceita) c.pos else c.neg
        )
    }
}

private fun padraoSubtitulo(transacao: Transacao): String {
    val partes = mutableListOf(transacao.categoria.rotulo)
    if (transacao.hora.isNotBlank()) partes.add(transacao.hora)
    return partes.joinToString(" · ")
}

/**
 * Pilula clicavel. Usada nos filtros da tela 05 e nas categorias da 06.
 * Selecionada = fundo cheio; solta = fundo branco com borda.
 */
@Composable
fun Pilula(
    texto: String,
    selecionada: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    corSelecionada: Color? = null,
    destaque: Boolean = false,
) {
    val c = FinanSmartTheme.colors
    val fundo = corSelecionada ?: if (destaque) c.primaryFill else c.panel

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(if (selecionada) fundo else c.surface)
            .then(
                if (selecionada) Modifier
                else Modifier.border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusPill))
            )
            .clickable(onClick = aoClicar)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    ) {
        Text(
            text = texto,
            style = if (selecionada) FinanSmartTheme.type.PilulaCategoria
            else FinanSmartTheme.type.PilulaCategoria,
            color = if (selecionada) Color.White else c.ink2
        )
    }
}

/** Rotulo de secao em DM Mono maiusculo: "HOJE - 19 SET", "CONTA". */
@Composable
fun RotuloSecao(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = FinanSmartTheme.type.MonoSecao,
        color = FinanSmartTheme.colors.ink3,
        modifier = modifier
    )
}

/** Cartao branco com borda, usado como moldura de varias secoes. */
@Composable
fun Cartao(
    modifier: Modifier = Modifier,
    raio: androidx.compose.ui.unit.Dp = Dimens.RadiusCardMedium,
    preenchimento: androidx.compose.ui.unit.Dp = 18.dp,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    val c = FinanSmartTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(raio))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(raio))
            .padding(preenchimento),
        content = conteudo
    )
}

/**
 * Linha de uma lista de configuracoes: emoji, texto, e algo a direita.
 * Tela 09.
 */
@Composable
fun LinhaDeOpcao(
    emoji: String,
    texto: String,
    modifier: Modifier = Modifier,
    valor: String? = null,
    mostrarSeta: Boolean = true,
    aoClicar: (() -> Unit)? = null,
    conteudoDireita: (@Composable () -> Unit)? = null,
) {
    val c = FinanSmartTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (aoClicar != null) Modifier.clickable(onClick = aoClicar) else Modifier)
            .padding(horizontal = Dimens.Space16, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 17.sp)
        Spacer(Modifier.width(Dimens.Space12))
        Text(
            text = texto,
            style = FinanSmartTheme.type.ItemConfig,
            color = c.ink,
            modifier = Modifier.weight(1f)
        )
        if (valor != null) {
            Text(
                text = valor,
                style = FinanSmartTheme.type.DetalheValor,
                color = c.ink2
            )
        }
        if (conteudoDireita != null) {
            conteudoDireita()
        } else if (mostrarSeta) {
            Spacer(Modifier.width(Dimens.Space8))
            Text(text = "›", fontSize = 16.sp, color = c.ink3)
        }
    }
}

/** Separador fino usado dentro dos cartoes de lista. */
@Composable
fun Divisoria() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(FinanSmartTheme.colors.line)
    )
}

/** Chavinha de ligar/desligar, nas telas 09 e 11. */
@Composable
fun Interruptor(
    ligado: Boolean,
    aoAlternar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val alinhamento by animateFloatAsState(
        targetValue = if (ligado) 1f else -1f,
        label = "posicaoDaBolinha"
    )

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(if (ligado) c.primaryFill else c.surface2)
            .clickable(onClick = aoAlternar)
            .padding(horizontal = 3.dp),
        contentAlignment = BiasAlignment(alinhamento, 0f)
    ) {
        Box(
            Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(if (ligado) Color.White else c.ink3)
        )
    }
}

/** Barra de progresso horizontal, com cantos redondos. */
@Composable
fun BarraDeProgresso(
    progresso: Float,
    modifier: Modifier = Modifier,
    altura: androidx.compose.ui.unit.Dp = 10.dp,
    cor: Color? = null,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier
            .fillMaxWidth()
            .height(altura)
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(c.track)
    ) {
        Box(
            Modifier
                .fillMaxWidth(progresso.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(cor ?: c.primaryFill)
        )
    }
}

/** Bolinha ou quadradinho colorido das legendas de grafico. */
@Composable
fun MarcadorDeLegenda(cor: Color, redondo: Boolean = false) {
    Box(
        Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(if (redondo) Dimens.RadiusPill else 3.dp))
            .background(cor)
    )
}

/** Etiqueta arredondada de status: "Despesa - Confirmada", "Conta verificada". */
@Composable
fun Etiqueta(
    texto: String,
    corTexto: Color,
    corFundo: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(corFundo)
            .padding(horizontal = 13.dp, vertical = 6.dp)
    ) {
        Text(
            text = texto,
            style = FinanSmartTheme.type.Rotulo115.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            color = corTexto
        )
    }
}
