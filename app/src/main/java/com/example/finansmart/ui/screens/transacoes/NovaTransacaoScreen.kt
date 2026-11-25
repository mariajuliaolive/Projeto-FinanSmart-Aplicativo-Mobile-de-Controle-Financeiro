package com.example.finansmart.ui.screens.transacoes

import androidx.compose.foundation.background
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
import com.example.finansmart.ui.components.CabecalhoTela
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.components.Pilula
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * TELA 06 - NOVA TRANSACAO
 *
 * A tela 14 do prototipo ("Erros e avisos") nao e outra tela: e esta
 * mesma, quando a pessoa tenta salvar sem o necessario.
 */

@Composable
fun NovaTransacaoScreen(
    onVoltar: () -> Unit = {},
    onSalvou: () -> Unit = {},
    // null = nova transacao. Com um id = EDITAR essa transacao (mesma tela).
    transacaoId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // Modo edicao: os campos comecam com os valores da transacao.
    // remember(chave): so recalcula o valor inicial se for OUTRA transacao.
    val original = transacaoId?.let { Repositorio.buscarTransacao(it) }
    val editando = original != null
    val chave = original?.id

    var tipo by remember(chave) { mutableStateOf(original?.tipo ?: TipoTransacao.DESPESA) }

    // Guardamos so os DIGITOS que a pessoa teclou ("5000"). O "R$ 50,00"
    // e desenhado a partir deles. Ver CampoDeValor, no fim do arquivo.
    var digitos by remember(chave) {
        mutableStateOf(original?.let { Math.round(it.valor * 100).toString() } ?: "")
    }

    // Na edicao: se a descricao for igual ao nome da categoria, ela foi
    // preenchida automaticamente ao salvar -- entao o campo volta vazio.
    var descricao by remember(chave) {
        mutableStateOf(
            original?.let { o ->
                o.descricao.takeIf { d ->
                    d != o.categoria.rotulo && CategoriaReceita.entries.none { it.rotulo == d }
                }
            } ?: ""
        )
    }
    // Comecam SEM nada escolhido: a regra e "categoria OU descricao",
    // entao deixar uma pre-selecionada tornaria a regra inutil.
    var categoriaDespesa by remember(chave) {
        mutableStateOf(original?.categoria?.takeIf { it != Categoria.RECEITA })
    }
    // A categoria de receita nao e gravada; ela e reconhecida pelo emoji.
    var categoriaReceita by remember(chave) {
        mutableStateOf(
            original?.takeIf { it.tipo == TipoTransacao.RECEITA }
                ?.let { o -> CategoriaReceita.entries.firstOrNull { it.emoji == o.emoji } }
        )
    }
    // Antes era "19/09/2026" fixo (a data do prototipo). Agora: hoje.
    var data by remember(chave) { mutableStateOf(original?.dataFormatada ?: hojeFormatado()) }
    var observacao by remember(chave) { mutableStateOf(original?.observacao ?: "") }
    var tentouSalvar by remember { mutableStateOf(false) }

    val ehReceita = tipo == TipoTransacao.RECEITA
    val centavos = digitos.toLongOrNull() ?: 0L
    val valor = centavosParaReais(centavos)
    val temCategoria = if (ehReceita) categoriaReceita != null else categoriaDespesa != null
    val temDescricao = descricao.isNotBlank()

    // Pelo menos UM dos dois: categoria ou descricao.
    val faltaIdentificar = !temCategoria && !temDescricao
    val erroValor = tentouSalvar && centavos <= 0L
    val erroIdentificacao = tentouSalvar && faltaIdentificar
    val podeSalvar = centavos > 0L && !faltaIdentificar

    val corDoValor = if (ehReceita) c.pos else c.neg

    Column(modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(6.dp))

            CabecalhoTela(
                titulo = if (editando) "Editar transação" else "Nova transação",
                aoVoltar = onVoltar
            )

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
                AbaDeTipo("Receita", ehReceita, c.pos, { tipo = TipoTransacao.RECEITA }, Modifier.weight(1f))
                AbaDeTipo("Despesa", !ehReceita, c.neg, { tipo = TipoTransacao.DESPESA }, Modifier.weight(1f))
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
                    CampoDeValor(
                        digitos = digitos,
                        aoMudar = { digitos = it },
                        cor = corDoValor
                    )
                }
            }
            if (erroValor) {
                Spacer(Modifier.height(6.dp))
                MensagemDeErro("Informe um valor maior que zero.")
            }

            /* ---- categoria ---- */
            Spacer(Modifier.height(Dimens.Space20))
            Text(
                text = "Categoria",
                style = t.FieldLabel,
                color = if (erroIdentificacao) c.neg else c.ink2
            )
            Spacer(Modifier.height(9.dp))
            if (ehReceita) {
                GradeDePilulas(
                    itens = CategoriaReceita.entries.map { it.emoji + " " + it.rotulo },
                    indiceSelecionado = categoriaReceita?.ordinal,
                    aoSelecionar = { indice ->
                        categoriaReceita =
                            if (categoriaReceita?.ordinal == indice) null
                            else CategoriaReceita.entries[indice]
                    }
                )
            } else {
                val lista = Categoria.DESPESAS
                GradeDePilulas(
                    itens = lista.map { it.emoji + " " + it.rotulo },
                    indiceSelecionado = categoriaDespesa?.let { lista.indexOf(it) },
                    aoSelecionar = { indice ->
                        categoriaDespesa =
                            if (categoriaDespesa == lista[indice]) null else lista[indice]
                    }
                )
            }

            /* ---- descricao ---- */
            Spacer(Modifier.height(Dimens.Space16))
            CampoTexto(
                rotulo = if (temCategoria) "Descrição (opcional)" else "Descrição",
                valor = descricao,
                aoMudarValor = { descricao = it },
                placeholder = if (ehReceita) "Ex.: salário, freelance, presente"
                else "Ex.: mercado do mês"
            )

            if (erroIdentificacao) {
                Spacer(Modifier.height(6.dp))
                MensagemDeErro("Escolha uma categoria ou escreva uma descrição.")
            }

            /* ---- data ---- */
            Spacer(Modifier.height(Dimens.Space16))
            CampoTexto(
                rotulo = "Data",
                valor = data,
                aoMudarValor = { data = it },
                placeholder = "dd/mm/aaaa",
                conteudoFinal = { Text(text = "📅", fontSize = 15.sp) }
            )

            /* ---- observacao ---- */
            Spacer(Modifier.height(Dimens.Space16))
            CampoTexto(
                rotulo = "Observação (opcional)",
                valor = observacao,
                aoMudarValor = { observacao = it },
                placeholder = if (ehReceita) "Pagamento do mês" else "Compra da semana"
            )

            /* ---- dica de quanto ja foi gasto na categoria ---- */
            val cat = categoriaDespesa
            if (!ehReceita && cat != null) {
                Spacer(Modifier.height(Dimens.Space20))
                // Antes: "X% do planejado", com um planejado de R$ 400 inventado.
                // O app nao tem orcamento por categoria, entao mostramos o
                // valor real ja gasto nela no mes atual.
                val jaGasto = Repositorio.transacoes
                    .doMes(MesAno.atual())
                    .filter { it.tipo == TipoTransacao.DESPESA && it.categoria == cat }
                    .sumOf { it.valor }
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
                        text = if (jaGasto > 0.0)
                            "Você já gastou ${formatarMoeda(jaGasto)} em ${cat.rotulo} neste mês."
                        else
                            "Este será seu primeiro gasto com ${cat.rotulo} neste mês.",
                        style = t.Caption,
                        color = c.ink2
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Space20))
        }

        /* ---- botao fixo ---- */
        Column(
            Modifier
                .fillMaxWidth()
                .background(c.surface)
                .padding(
                    start = Dimens.ScreenPadding, end = Dimens.ScreenPadding,
                    top = 14.dp, bottom = 26.dp
                )
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
                        if (podeSalvar) {
                            val partes = data.split("/")
                            // Se a data digitada estiver incompleta, usa a de hoje.
                            val (diaHoje, mesHoje, anoHoje) = hoje()
                            val descricaoFinal = descricao.trim().ifBlank {
                                if (ehReceita) categoriaReceita?.rotulo.orEmpty()
                                else categoriaDespesa?.rotulo.orEmpty()
                            }
                            val categoriaFinal = if (ehReceita) Categoria.RECEITA
                            else categoriaDespesa ?: Categoria.OUTROS
                            val emojiFinal = if (ehReceita) categoriaReceita?.emoji ?: "💰"
                            else categoriaDespesa?.emoji ?: "⋯"
                            val dia = partes.getOrNull(0)?.toIntOrNull()?.coerceIn(1, 31) ?: diaHoje
                            val mes = partes.getOrNull(1)?.toIntOrNull()?.coerceIn(1, 12) ?: mesHoje
                            val ano = partes.getOrNull(2)?.toIntOrNull() ?: anoHoje

                            if (original != null) {
                                Repositorio.atualizarTransacao(
                                    original.copy(
                                        descricao = descricaoFinal,
                                        valor = valor,
                                        tipo = tipo,
                                        categoria = categoriaFinal,
                                        emoji = emojiFinal,
                                        dia = dia,
                                        mes = mes,
                                        ano = ano,
                                        observacao = observacao.trim(),
                                        formaPagamento = if (ehReceita) "Depósito" else "Cartão",
                                    )
                                )
                            } else {
                                Repositorio.adicionarTransacao(
                                    descricao = descricaoFinal,
                                    valor = valor,
                                    tipo = tipo,
                                    categoria = categoriaFinal,
                                    emoji = emojiFinal,
                                    dia = dia,
                                    mes = mes,
                                    ano = ano,
                                    observacao = observacao.trim()
                                )
                            }
                            onSalvou()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (editando) "Salvar alterações" else "Adicionar transação",
                    style = t.BotaoGrande,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * O campo grande do valor, com formatacao automatica.
 *
 *   digita 5     -> R$ 0,05
 *   digita 50    -> R$ 0,50
 *   digita 500   -> R$ 5,00
 *   digita 5000  -> R$ 50,00
 *
 * O TRUQUE: o campo de texto guarda APENAS os digitos crus ("5000") e e
 * desenhado transparente. Por cima dele mostramos o texto formatado.
 *
 * Por que nao colocar o "R$ 50,00" direto no campo? Porque o texto
 * formatado tem digitos proprios (o 0 e o 0 dos centavos), e a cada tecla
 * o cursor volta ao inicio -- os numeros entravam na frente e a conta
 * saia errada. Com digitos crus nao ha ambiguidade.
 */
@Composable
private fun CampoDeValor(
    digitos: String,
    aoMudar: (String) -> Unit,
    cor: Color,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    val centavos = digitos.toLongOrNull() ?: 0L

    BasicTextField(
        value = digitos,
        onValueChange = { novo ->
            // So numeros, e no maximo 999.999.999,99
            aoMudar(novo.filter { it.isDigit() }.trimStart('0').take(11))
        },
        singleLine = true,
        textStyle = t.ValorEntrada.copy(
            color = Color.Transparent,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { campoInvisivel ->
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = formatarCentavos(centavos),
                    style = t.ValorEntrada,
                    color = if (centavos > 0L) cor else c.ink3,
                    textAlign = TextAlign.Center
                )
                campoInvisivel()
            }
        }
    )
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

/** Pilulas em duas colunas. O Compose basico nao tem "flex-wrap". */
@Composable
private fun GradeDePilulas(
    itens: List<String>,
    indiceSelecionado: Int?,
    aoSelecionar: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
        itens.chunked(2).forEachIndexed { linha, dupla ->
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
                dupla.forEachIndexed { coluna, rotulo ->
                    val indice = linha * 2 + coluna
                    Pilula(
                        texto = rotulo,
                        selecionada = indice == indiceSelecionado,
                        aoClicar = { aoSelecionar(indice) },
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
