package com.example.finansmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finansmart.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanSmartTheme {
                Scaffold { padding ->
                    AmostraDoTema(Modifier.padding(padding))
                }
            }
        }
    }
}

/*
 * TELA TEMPORARIA -- so para conferir a Etapa 3.
 * Vai ser apagada na Etapa 4, quando a Splash de verdade entrar no lugar.
 */
@Composable
fun AmostraDoTema(modifier: Modifier = Modifier) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space16)
    ) {
        Text("SETEMBRO", style = t.MonoLabel, color = c.primaryInk)
        Text("Amostra do tema", style = t.ScreenTitle, color = c.ink)
        Text(
            "Se as letras estao arredondadas e o roxo aparece, DM Sans e as cores carregaram.",
            style = t.Body, color = c.ink2
        )

        // Card escuro do saldo
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusCardLarge))
                .background(c.panel)
                .padding(Dimens.Space22)
        ) {
            Text("Saldo disponivel", style = t.Caption, color = c.panelSub)
            Spacer(Modifier.height(Dimens.Space8))
            Text("R$ 2.450,00", style = t.Balance, color = Color.White)
            Spacer(Modifier.height(Dimens.Space18))
            Box(
                Modifier.fillMaxWidth().height(Dimens.ButtonHeightInline)
                    .clip(RoundedCornerShape(Dimens.RadiusField))
                    .background(c.primaryFill),
                contentAlignment = Alignment.Center
            ) { Text("+ Nova", style = t.ButtonSmall, color = Color.White) }
        }

        // Dois cards lado a lado
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
            CardValor("Receitas", "R$ 3.000,00", c.pos, c.posSoft, Modifier.weight(1f))
            CardValor("Despesas", "R$ 550,00", c.neg, c.negSoft, Modifier.weight(1f))
        }

        // Botao principal
        Box(
            Modifier.fillMaxWidth().height(Dimens.ButtonHeight)
                .clip(RoundedCornerShape(Dimens.RadiusButton))
                .background(c.primaryFill),
            contentAlignment = Alignment.Center
        ) { Text("Entrar", style = t.Button, color = Color.White) }

        // Cores das categorias
        Text("Categorias", style = t.SectionTitle, color = c.ink)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
            listOf(
                CatMoradia, CatAlimentacao, CatTransporte,
                CatSaude, CatEducacao, CatLazer, CatOutros
            ).forEach { cor ->
                Box(
                    Modifier.weight(1f).height(Dimens.IconBoxMedium)
                        .clip(RoundedCornerShape(Dimens.RadiusField))
                        .background(cor)
                )
            }
        }

        // DM Mono
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Moradia", style = t.Body, color = c.ink)
            Text("R$ 180", style = t.MonoValue, color = c.ink)
        }

        Text(
            if (c.isDark) "Tema ESCURO ativo" else "Tema CLARO ativo",
            style = t.MonoLabel, color = c.ink3
        )
        Spacer(Modifier.height(Dimens.Space32))
    }
}

@Composable
private fun CardValor(
    rotulo: String, valor: String,
    corTexto: Color, corFundoIcone: Color,
    modifier: Modifier = Modifier
) {
    val c = FinanSmartTheme.colors
    Column(
        modifier
            .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
            .background(c.surface)
            .padding(Dimens.Space16)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(Dimens.IconBoxSmall)
                    .clip(RoundedCornerShape(Dimens.Space8))
                    .background(corFundoIcone)
            )
            Spacer(Modifier.width(Dimens.Space8))
            Text(rotulo, style = FinanSmartTheme.type.StatLabel, color = c.ink2)
        }
        Spacer(Modifier.height(Dimens.Space8))
        Text(valor, style = FinanSmartTheme.type.CardValue, color = corTexto)
    }
}

@Preview(showBackground = true)
@Composable
fun AmostraClaroPreview() {
    FinanSmartTheme(darkTheme = false) { AmostraDoTema() }
}

@Preview(showBackground = true)
@Composable
fun AmostraEscuroPreview() {
    FinanSmartTheme(darkTheme = true) { AmostraDoTema() }
}
