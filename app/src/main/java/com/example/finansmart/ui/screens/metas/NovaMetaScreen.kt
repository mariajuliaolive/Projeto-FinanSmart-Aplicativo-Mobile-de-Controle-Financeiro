package com.example.finansmart.ui.screens.metas

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Repositorio
import com.example.finansmart.data.formatarMoeda
import com.example.finansmart.data.formatarMoedaCurta
import com.example.finansmart.data.lerValorDigitado
import com.example.finansmart.ui.components.CabecalhoTela
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.components.Interruptor
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme
import kotlin.math.ceil
import java.util.Calendar
import kotlin.math.max

/* TELA 11 - NOVA META */

private val ICONES = listOf(
    "🎓", "💻", "✈️",
    "🏠", "🚗", "⋯"
)

@Composable
fun NovaMetaScreen(
    onVoltar: () -> Unit = {},
    onCriou: () -> Unit = {},
    // null = criar meta nova. Com um id = EDITAR essa meta (mesma tela!).
    metaId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // No modo edicao, os campos comecam preenchidos com a meta atual.
    // O remember(metaEditada?.id) so recalcula se for OUTRA meta -- assim
    // o que a pessoa digita nao e apagado a cada redesenho.
    val metaEditada = metaId?.let { Repositorio.buscarMeta(it) }
    val editando = metaEditada != null

    var icone by remember(metaEditada?.id) { mutableStateOf(metaEditada?.emoji ?: ICONES.first()) }
    var nome by remember(metaEditada?.id) { mutableStateOf(metaEditada?.nome ?: "") }
    var objetivoTexto by remember(metaEditada?.id) {
        mutableStateOf(metaEditada?.let { formatarMoeda(it.objetivo) } ?: "")
    }
    var prazo by remember(metaEditada?.id) {
        mutableStateOf(
            metaEditada?.let { "%02d/%04d".format(it.prazoMes, it.prazoAno) } ?: "12/2026"
        )
    }
    var guardadoTexto by remember(metaEditada?.id) {
        mutableStateOf(metaEditada?.let { formatarMoeda(it.guardado) } ?: "")
    }
    var reservaAutomatica by remember(metaEditada?.id) {
        mutableStateOf(metaEditada?.reservaAutomatica ?: true)
    }

    val objetivo = lerValorDigitado(objetivoTexto)
    val guardado = lerValorDigitado(guardadoTexto)
    val renda = Repositorio.perfil.value.rendaMensal

    // Quanto falta dividido pelos meses que restam ate o prazo.
    val mesesRestantes = mesesAte(prazo)
    val porMes = if (mesesRestantes > 0) ceil((objetivo - guardado) / mesesRestantes) else 0.0
    val percentualDaRenda = if (renda > 0) porMes / renda * 100 else 0.0

    val podeCriar = nome.isNotBlank() && objetivo > 0.0

    Column(modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(6.dp))

            CabecalhoTela(titulo = if (editando) "Editar meta" else "Nova meta", aoVoltar = onVoltar)

            /* ---- icone ---- */
            Spacer(Modifier.height(Dimens.Space22))
            Text(text = "Ícone", style = t.FieldLabel, color = c.ink2)
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                ICONES.forEach { opcao ->
                    val ativo = opcao == icone
                    Box(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (ativo) c.primaryFill else c.surface)
                            .then(
                                if (ativo) Modifier
                                else Modifier.border(
                                    Dimens.BorderWidth, c.line, RoundedCornerShape(14.dp)
                                )
                            )
                            .clickable { icone = opcao },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = opcao, fontSize = 21.sp)
                    }
                }
            }

            /* ---- campos ---- */
            Spacer(Modifier.height(Dimens.Space20))
            CampoTexto(
                rotulo = "Nome da meta",
                valor = nome,
                aoMudarValor = { nome = it },
                placeholder = "Curso de inglês"
            )

            Spacer(Modifier.height(13.dp))
            CampoTexto(
                rotulo = "Valor objetivo",
                valor = objetivoTexto,
                aoMudarValor = { objetivoTexto = it },
                placeholder = "R$ 2.000,00",
                tipoTeclado = KeyboardType.Decimal
            )

            Spacer(Modifier.height(13.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
                CampoTexto(
                    rotulo = "Prazo",
                    valor = prazo,
                    aoMudarValor = { prazo = it },
                    placeholder = "12/2026",
                    modifier = Modifier.weight(1f),
                    conteudoFinal = { Text(text = "📅", fontSize = 14.sp) }
                )
                CampoTexto(
                    rotulo = "Já guardado",
                    valor = guardadoTexto,
                    aoMudarValor = { guardadoTexto = it },
                    placeholder = "R$ 0,00",
                    tipoTeclado = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            /* ---- sugestao ---- */
            if (objetivo > 0.0) {
                Spacer(Modifier.height(Dimens.Space18))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(c.primarySoft)
                        .padding(horizontal = 18.dp, vertical = Dimens.Space16)
                ) {
                    Text(
                        text = "Sugestão do FinanSmart",
                        style = t.ListTitle.copy(fontSize = 13.sp),
                        color = c.primaryInk
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Guardando ${formatarMoedaCurta(porMes)} por mês você atinge a " +
                                "meta no prazo. Isso é " +
                                "%.1f".format(percentualDaRenda).replace(".", ",") +
                                "% da sua renda atual.",
                        style = t.ListValue.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        color = c.ink
                    )
                }
            }

            /* ---- reserva automatica ---- */
            Spacer(Modifier.height(Dimens.Space16))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
                    .background(c.surface)
                    .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusCardSmall))
                    .padding(horizontal = Dimens.Space16, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔁", fontSize = 17.sp)
                Spacer(Modifier.width(Dimens.Space12))
                Text(
                    text = "Reserva automática mensal",
                    style = t.ListValue.copy(fontWeight = FontWeight.Medium),
                    color = c.ink,
                    modifier = Modifier.weight(1f)
                )
                Interruptor(
                    ligado = reservaAutomatica,
                    aoAlternar = { reservaAutomatica = !reservaAutomatica }
                )
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
                    .background(if (podeCriar) c.primaryFill else c.primaryFill.copy(alpha = 0.4f))
                    .clickable(enabled = podeCriar) {
                        val partes = prazo.split("/")
                        val prazoMes = partes.getOrNull(0)?.toIntOrNull() ?: 12
                        val prazoAno = partes.getOrNull(1)?.toIntOrNull() ?: 2026

                        if (metaEditada != null) {
                            // copy(): mesma meta (mesmo id), so com os campos novos.
                            Repositorio.atualizarMeta(
                                metaEditada.copy(
                                    nome = nome.trim(),
                                    emoji = icone,
                                    objetivo = objetivo,
                                    guardado = guardado,
                                    prazoMes = prazoMes,
                                    prazoAno = prazoAno,
                                    reservaAutomatica = reservaAutomatica,
                                )
                            )
                        } else {
                            Repositorio.adicionarMeta(
                                nome = nome.trim(),
                                emoji = icone,
                                objetivo = objetivo,
                                guardado = guardado,
                                prazoMes = prazoMes,
                                prazoAno = prazoAno,
                                reservaAutomatica = reservaAutomatica
                            )
                        }
                        onCriou()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (editando) "Salvar alterações" else "Criar meta",
                    style = t.BotaoGrande,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Quantos meses faltam de HOJE ate o prazo "MM/AAAA".
 * Antes a conta partia de setembro/2026 fixo (a data do prototipo);
 * agora usa o mes atual do celular. Calendar.MONTH conta de 0 a 11,
 * por isso o +1.
 */
private fun mesesAte(prazo: String): Int {
    val partes = prazo.split("/")
    val mes = partes.getOrNull(0)?.toIntOrNull() ?: return 12
    val ano = partes.getOrNull(1)?.toIntOrNull() ?: return 12
    val hoje = Calendar.getInstance()
    val mesAtual = hoje.get(Calendar.MONTH) + 1
    val anoAtual = hoje.get(Calendar.YEAR)
    val total = (ano - anoAtual) * 12 + (mes - mesAtual)
    return max(total, 1)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NovaMetaPreview() {
    FinanSmartTheme { NovaMetaScreen() }
}
