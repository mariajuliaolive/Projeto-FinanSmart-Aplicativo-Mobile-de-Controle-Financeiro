package com.example.finansmart.ui.screens.transacoes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.*
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.components.CabecalhoTela
import com.example.finansmart.ui.components.Pilula
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * TELA 06 - NOVA TRANSACAO
 *
 * A tela 14 do prototipo ("Erros e avisos") nao e outra tela: e esta
 * mesma, quando a pessoa tenta salvar com os campos em branco. Por isso
 * as mensagens de erro moram aqui.
 */

@Composable
fun NovaTransacaoScreen(
    onVoltar: () -> Unit = {},
    onSalvou: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var tipo by remember { mutableStateOf(TipoTransacao.DESPESA) }
    var valorTexto by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.ALIMENTACAO) }
    var data by remember { mutableStateOf("19/09/2026") }
    var observacao by remember { mutableStateOf("") }
    var tentouSalvar by remember { mutableStateOf(false) }

    val valor = lerValorDigitado(valorTexto)
    val erroValor = tentouSalvar && valor <= 0.0
    val erroDescricao = tentouSalvar && descricao.isBlank()
    val corDoValor = if (tipo == TipoTransacao.RECEITA) c.pos else c.neg

    Column(modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(6.dp))

            CabecalhoTela(titulo = "Nova transação", aoVoltar = onVoltar)

            /* ---- receita / despesa ---- */
            Spacer(Modifier.height(Dimens.Space20))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.RadiusButton))
                    .background(c.surface2)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AbaDeTipo(
                    texto = "Receita",
                    ativa = tipo == TipoTransacao.RECEITA,
                    corAtiva = c.pos,
                    aoClicar = {
                        tipo = TipoTransacao.RECEITA
                        categoria = Categoria.RECEITA
                    },
                    modifier = Modifier.weight(1f)
                )
                AbaDeTipo(
                    texto = "Despesa",
                    ativa = tipo == TipoTransacao.DESPESA,
                    corAtiva = c.neg,
                    aoClicar = {
                        tipo = TipoTransacao.DESPESA
                        if (categoria == Categoria.RECEITA) categoria = Categoria.ALIMENTACAO
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            /* ---- valor ---- */
            Spacer(Modifier.height(Dimens.Space22))
            Cartao(preenchimento = 0.dp) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.ScreenPadding, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (erroValor) "Valor *" else "Valor",
                        style = t.StatLabel,
                        color = if (erroValor) c.neg else c.ink2
                    )
                    Spacer(Modifier.height(6.dp))
                    BasicTextField(
                        value = valorTexto,
                        onValueChange = { valorTexto = it },
                        singleLine = true,
                        textStyle = t.ValorEntrada.copy(
                            color = corDoValor,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(corDoValor),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { campo ->
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                                if (valorTexto.isEmpty()) {
                                    Text(
                                        text = "R$ 0,00",
                                        style = t.ValorEntrada,
                                        color = c.ink3,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                campo()
                            }
                        }
                    )
                }
            }
            if (erroValor) {
                Spacer(Modifier.height(6.dp))
                MensagemDeErro("Informe um valor maior que zero.")
            }

            /* ---- descricao ---- */
            Spacer(Modifier.height(Dimens.Space20))
            CampoTexto(
                rotulo = if (erroDescricao) "Descrição *" else "Descrição",
                valor = descricao,
                aoMudarValor = { descricao = it },
                placeholder = "Ex.: mercado do mês"
            )
            if (erroDescricao) {
                Spacer(Modifier.height(4.dp))
                MensagemDeErro("Campo obrigatório.")
            }

            /* ---- categoria ---- */
            if (tipo == TipoTransacao.DESPESA) {
                Spacer(Modifier.height(Dimens.Space16))
                Text(text = "Categoria", style = t.FieldLabel, color = c.ink2)
                Spacer(Modifier.height(9.dp))
                GradeDeCategorias(
                    selecionada = categoria,
                    aoSelecionar = { categoria = it }
                )
            }

            /* ---- data ---- */
            Spacer(Modifier.height(Dimens.Space16))
            CampoTexto(
                rotulo = "Data",
                valor = data,
                aoMudarValor = { data = it },
                placeholder = "19/09/2026",
                conteudoFinal = { Text(text = "📅", fontSize = 15.sp) }
            )

            /* ---- observacao ---- */
            Spacer(Modifier.height(Dimens.Space16))
            CampoTexto(
                rotulo = "Observação (opcional)",
                valor = observacao,
                aoMudarValor = { observacao = it },
                placeholder = "Compra da semana"
            )

            Spacer(Modifier.height(Dimens.Space20))

            /* ---- dica do prototipo (tela 14) ---- */
            if (tipo == TipoTransacao.DESPESA) {
                val jaGasto = Repositorio.gastosPorCategoria()
                    .firstOrNull { it.first == categoria }?.second ?: 0.0
                val planejado = 400.0
                val percentual = (jaGasto / planejado * 100).toInt()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
                        .background(c.primarySoft)
                        .padding(Dimens.Space16),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "💡", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Você já gastou $percentual% do planejado em " +
                                "${categoria.rotulo} neste mês.",
                        style = t.Caption,
                        color = c.ink2
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Space20))
        }

        /* ---- botao fixo no rodape ---- */
        Column(
            Modifier
                .fillMaxWidth()
                .background(c.surface)
                .padding(start = Dimens.ScreenPadding, end = Dimens.ScreenPadding, top = 14.dp, bottom = 26.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(Dimens.RadiusCardSmall),
                        ambientColor = c.primary,
                        spotColor = c.primary
                    )
                    .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
                    .background(c.primaryFill)
                    .clickable {
                        tentouSalvar = true
                        if (valor > 0.0 && descricao.isNotBlank()) {
                            val partes = data.split("/")
                            Repositorio.adicionarTransacao(
                                descricao = descricao.trim(),
                                valor = valor,
                                tipo = tipo,
                                categoria = categoria,
                                dia = partes.getOrNull(0)?.toIntOrNull() ?: 19,
                                mes = partes.getOrNull(1)?.toIntOrNull() ?: 9,
                                ano = partes.getOrNull(2)?.toIntOrNull() ?: 2026,
                                observacao = observacao.trim()
                            )
                            onSalvou()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Adicionar transação", style = t.BotaoGrande, color = Color.White)
            }
        }
    }
}

@Composable
private fun AbaDeTipo(
    texto: String,
    ativa: Boolean,
    corAtiva: Color,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (ativa) corAtiva else Color.Transparent)
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = FinanSmartTheme.type.ListTitle.copy(
                fontWeight = if (ativa) androidx.compose.ui.text.font.FontWeight.SemiBold
                else androidx.compose.ui.text.font.FontWeight.Medium
            ),
            color = if (ativa) Color.White else c.ink2
        )
    }
}

@Composable
private fun GradeDeCategorias(
    selecionada: Categoria,
    aoSelecionar: (Categoria) -> Unit,
) {
    val lista = Categoria.entries.filter { it != Categoria.RECEITA }
    // Duas fileiras manuais: o Compose basico nao tem "flex-wrap".
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
        lista.chunked(2).forEach { dupla ->
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
                dupla.forEach { categoria ->
                    Pilula(
                        texto = "${categoria.emoji} ${categoria.rotulo}",
                        selecionada = categoria == selecionada,
                        aoClicar = { aoSelecionar(categoria) },
                        destaque = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (dupla.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MensagemDeErro(texto: String) {
    Text(
        text = "⚠ $texto",
        style = FinanSmartTheme.type.ListSubtitle,
        color = FinanSmartTheme.colors.neg
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NovaTransacaoPreview() {
    FinanSmartTheme { NovaTransacaoScreen() }
}
