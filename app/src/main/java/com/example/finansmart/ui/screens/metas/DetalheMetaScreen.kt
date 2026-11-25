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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Repositorio
import com.example.finansmart.data.centavosDoTexto
import com.example.finansmart.data.centavosParaReais
import com.example.finansmart.data.formatarCentavos
import com.example.finansmart.data.formatarMoeda
import com.example.finansmart.ui.components.BarraDeProgresso
import com.example.finansmart.ui.components.BotaoPrimario
import com.example.finansmart.ui.components.CabecalhoTela
import com.example.finansmart.ui.components.CampoTexto
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * DETALHE DA META
 *
 * Abre ao tocar num cartao da aba Metas. Aqui a pessoa:
 *   - ve o progresso em destaque;
 *   - guarda ou retira dinheiro;
 *   - exclui a meta (com confirmacao).
 */
@Composable
fun DetalheMetaScreen(
    metaId: Long,
    onVoltar: () -> Unit = {},
    onEditar: () -> Unit = {},
    onExcluiu: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // Lida do Repositorio a cada redesenho: quando o Firestore confirma
    // o novo valor guardado, a barra e os numeros se atualizam sozinhos.
    val meta = Repositorio.buscarMeta(metaId)

    // Estado so desta tela (nao vai para o banco).
    var centavos by remember { mutableLongStateOf(0L) }
    var aviso by remember { mutableStateOf<String?>(null) }
    var confirmandoExclusao by remember { mutableStateOf(false) }

    if (meta == null) {
        // Acontece por um instante depois de excluir, ou se a meta sumiu.
        Box(modifier.fillMaxSize().background(c.bg), contentAlignment = Alignment.Center) {
            Text(text = "Meta não encontrada.", style = t.Body, color = c.ink2)
        }
        return
    }

    val corDoProgresso = if (meta.concluida) c.pos else c.primaryFill

    Column(
        modifier
            .fillMaxSize()
            .background(c.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Spacer(Modifier.height(6.dp))
        CabecalhoTela(
            titulo = "Minha meta",
            aoVoltar = onVoltar,
            // acaoDireita: o espaco a direita do titulo, que o componente
            // ja tinha previsto. Aqui entra o "Editar".
            acaoDireita = {
                Text(
                    text = "✏️ Editar",
                    style = t.ListValue,
                    color = c.primaryInk,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.RadiusButton))
                        .clickable { onEditar() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        )
        Spacer(Modifier.height(Dimens.Space22))

        /* ---------- resumo da meta ---------- */
        Cartao {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(Dimens.IconBoxLarge)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (meta.concluida) c.posSoft else c.primarySoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = meta.emoji, fontSize = 22.sp)
                }
                Spacer(Modifier.width(Dimens.Space12))
                Column(Modifier.weight(1f)) {
                    Text(text = meta.nome, style = t.MetaNome, color = c.ink)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (meta.concluida) "Meta batida 🎉" else "Prazo: ${meta.prazoFormatado}",
                        style = t.Caption,
                        color = c.ink2
                    )
                }
                Text(
                    text = "${meta.percentual}%",
                    style = t.MetaPercentual.copy(fontSize = 22.sp),
                    color = if (meta.concluida) c.pos else c.primaryInk
                )
            }

            Spacer(Modifier.height(Dimens.Space18))
            BarraDeProgresso(progresso = meta.progresso, cor = corDoProgresso, altura = 12.dp)
            Spacer(Modifier.height(Dimens.Space16))

            LinhaDeValor("Economizado", formatarMoeda(meta.guardado), destaque = true)
            Spacer(Modifier.height(Dimens.Space8))
            LinhaDeValor("Objetivo", formatarMoeda(meta.objetivo))
            Spacer(Modifier.height(Dimens.Space8))
            LinhaDeValor("Falta", formatarMoeda(meta.falta))
        }

        /* ---------- guardar / retirar ---------- */
        Spacer(Modifier.height(Dimens.Space22))
        Text(text = "Movimentar", style = t.MetaNome, color = c.ink)
        Spacer(Modifier.height(Dimens.Space12))

        CampoTexto(
            rotulo = "Valor",
            // Digitacao "de banco": so numeros, entram pela direita como centavos.
            valor = if (centavos == 0L) "" else formatarCentavos(centavos),
            aoMudarValor = { centavos = centavosDoTexto(it); aviso = null },
            placeholder = "R$ 0,00",
            tipoTeclado = KeyboardType.Number
        )

        Spacer(Modifier.height(Dimens.Space12))

        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
            BotaoPrimario(
                texto = "＋ Guardar",
                aoClicar = {
                    val valor = centavosParaReais(centavos)
                    Repositorio.movimentarMeta(meta.id, valor)
                    aviso = "${formatarMoeda(valor)} guardados na meta."
                    centavos = 0L
                },
                habilitado = centavos > 0L,
                modifier = Modifier.weight(1f)
            )
            BotaoContorno(
                texto = "− Retirar",
                cor = c.ink,
                habilitado = centavos > 0L && meta.guardado > 0.0,
                aoClicar = {
                    // Nao deixa retirar mais do que existe na meta.
                    val valor = minOf(centavosParaReais(centavos), meta.guardado)
                    Repositorio.movimentarMeta(meta.id, -valor)
                    aviso = "${formatarMoeda(valor)} retirados da meta."
                    centavos = 0L
                },
                modifier = Modifier.weight(1f)
            )
        }

        if (aviso != null) {
            Spacer(Modifier.height(Dimens.Space8))
            Text(text = "✓ $aviso", style = t.ListSubtitle, color = c.pos)
        }

        /* ---------- excluir (com confirmacao) ---------- */
        Spacer(Modifier.height(32.dp))

        if (!confirmandoExclusao) {
            BotaoContorno(
                texto = "🗑 Excluir meta",
                cor = c.neg,
                aoClicar = { confirmandoExclusao = true },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Segundo passo: excluir e permanente, entao pedimos confirmacao.
            Text(
                text = "Excluir \"${meta.nome}\"? Isso não pode ser desfeito.",
                style = t.Body,
                color = c.ink
            )
            Spacer(Modifier.height(Dimens.Space12))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
                BotaoContorno(
                    texto = "Cancelar",
                    cor = c.ink2,
                    aoClicar = { confirmandoExclusao = false },
                    modifier = Modifier.weight(1f)
                )
                BotaoContorno(
                    texto = "Sim, excluir",
                    cor = c.neg,
                    aoClicar = {
                        Repositorio.removerMeta(meta.id)
                        onExcluiu()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

/** "Economizado ........ R$ 2.500,00" */
@Composable
private fun LinhaDeValor(rotulo: String, valor: String, destaque: Boolean = false) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = rotulo, style = t.Caption, color = c.ink2, modifier = Modifier.weight(1f))
        Text(
            text = valor,
            style = t.MetaDetalhe.copy(fontWeight = if (destaque) FontWeight.Bold else FontWeight.Medium),
            color = c.ink
        )
    }
}

/** Botao branco com borda colorida (Retirar, Excluir, Cancelar). */
@Composable
private fun BotaoContorno(
    texto: String,
    cor: Color,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    val c = FinanSmartTheme.colors
    val corFinal = if (habilitado) cor else cor.copy(alpha = 0.35f)
    Box(
        modifier
            .height(Dimens.ButtonHeight)
            .clip(RoundedCornerShape(Dimens.RadiusButton))
            .background(c.surface)
            .border(Dimens.BorderWidth, corFinal, RoundedCornerShape(Dimens.RadiusButton))
            .clickable(enabled = habilitado, onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = FinanSmartTheme.type.ListValue, color = corFinal)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetalheMetaPreview() {
    FinanSmartTheme { DetalheMetaScreen(metaId = 1L) }
}
