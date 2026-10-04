package com.example.finansmart.ui.screens.metas

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.Meta
import com.example.finansmart.data.Repositorio
import com.example.finansmart.data.formatarMoedaCurta
import com.example.finansmart.ui.components.BarraDeProgresso
import com.example.finansmart.ui.components.Cartao
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/* TELA 08 - METAS */

@Composable
fun MetasScreen(
    onNovaMeta: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    val metas = Repositorio.metas
    // O resumo do topo conta so as metas em andamento, como no prototipo.
    val emAndamento = Repositorio.metasEmAndamento()
    val guardado = emAndamento.sumOf { it.guardado }
    val objetivo = emAndamento.sumOf { it.objetivo }

    Column(modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(Modifier.height(6.dp))

            Text(text = "Minhas metas", style = t.TituloLista, color = c.ink)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Você guardou ${formatarMoedaCurta(guardado)} de " +
                        "${formatarMoedaCurta(objetivo)} no total.",
                style = t.Footer.copy(lineHeight = 20.sp),
                color = c.ink2
            )

            Spacer(Modifier.height(Dimens.Space20))

            metas.forEachIndexed { indice, meta ->
                if (indice > 0) Spacer(Modifier.height(Dimens.Space12))
                CartaoDeMeta(meta)
            }

            Spacer(Modifier.height(Dimens.Space20))
        }

        /* ---- botao fixo acima da barra de abas ---- */
        Column(
            Modifier
                .fillMaxWidth()
                .background(c.surface)
                .padding(horizontal = Dimens.ScreenPadding)
                .padding(top = Dimens.Space12)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(Dimens.RadiusButton),
                        ambientColor = c.primary,
                        spotColor = c.primary
                    )
                    .clip(RoundedCornerShape(Dimens.RadiusButton))
                    .background(c.primaryFill)
                    .clickable { onNovaMeta() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "＋ Nova meta",
                    style = t.Button.copy(fontSize = 15.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CartaoDeMeta(meta: Meta) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type
    val corDoProgresso = if (meta.concluida) c.pos else c.primaryFill

    Cartao {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(Dimens.IconBoxLarge)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (meta.concluida) c.posSoft else c.primarySoft),
                contentAlignment = Alignment.Center
            ) {
                Text(text = meta.emoji, fontSize = 20.sp)
            }

            Spacer(Modifier.width(Dimens.Space12))

            Column(Modifier.weight(1f)) {
                Text(text = meta.nome, style = t.MetaNome, color = c.ink)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Objetivo: ${formatarMoedaCurta(meta.objetivo)} · " +
                            if (meta.concluida) "concluída" else "até ${meta.prazoFormatado}",
                    style = t.MetaDetalhe.copy(fontWeight = FontWeight.Normal),
                    color = c.ink2
                )
            }

            Spacer(Modifier.width(Dimens.Space8))

            Text(
                text = "${meta.percentual}%",
                style = t.MetaPercentual,
                color = if (meta.concluida) c.pos else c.primaryInk
            )
        }

        Spacer(Modifier.height(Dimens.Space16))

        BarraDeProgresso(progresso = meta.progresso, cor = corDoProgresso)

        Spacer(Modifier.height(9.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f)) {
                Text(text = "Economizado ", style = t.MetaDetalhe, color = c.ink2)
                Text(
                    text = formatarMoedaCurta(meta.guardado),
                    style = t.MetaDetalhe.copy(fontWeight = FontWeight.Bold),
                    color = c.ink
                )
            }
            Text(
                text = if (meta.concluida) "Meta batida 🎉"
                else "Faltam ${formatarMoedaCurta(meta.falta)}",
                style = t.MetaDetalhe,
                color = c.ink2
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MetasPreview() {
    FinanSmartTheme { MetasScreen() }
}
