package com.example.finansmart.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.components.Divisoria
import com.example.finansmart.ui.theme.FinanSmartTheme

/**
 * As 5 abas do rodape. Aparece nas telas 04, 05, 07, 08 e 09.
 *
 * Cada aba conhece sua rota, entao a barra sabe sozinha qual esta ativa:
 * basta comparar com a rota atual.
 */
enum class Aba(val rota: String, val rotulo: String, val emoji: String) {
    INICIO(Rotas.DASHBOARD, "Início", "🏠"),
    TRANSACOES(Rotas.TRANSACOES, "Transações", "💳"),
    RELATORIOS(Rotas.RELATORIOS, "Relatórios", "📊"),
    METAS(Rotas.METAS, "Metas", "🎯"),
    CONFIG(Rotas.CONFIGURACOES, "Config.", "⚙️"),
}

@Composable
fun BarraNavegacaoInferior(
    rotaAtual: String?,
    aoSelecionar: (String) -> Unit,
    modifier: Modifier = Modifier,
    mostrarDivisoria: Boolean = true,
) {
    val c = FinanSmartTheme.colors

    Column(
        modifier
            .fillMaxWidth()
            .background(c.surface)
            .navigationBarsPadding()   // fica ACIMA dos botoes do sistema
    ) {
        if (mostrarDivisoria) Divisoria()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp, top = 10.dp, bottom = 18.dp)
        ) {
            Aba.entries.forEach { aba ->
                val ativa = rotaAtual == aba.rota
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { aoSelecionar(aba.rota) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /*
                     * O prototipo deixa o emoji da aba inativa em cinza
                     * (filter: grayscale). O Android nao tem esse filtro
                     * para emoji, entao usamos transparencia -- o efeito
                     * de "apagado" e o mesmo.
                     */
                    Text(
                        text = aba.emoji,
                        fontSize = 19.sp,
                        modifier = if (ativa) Modifier else Modifier.alpha(0.55f)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = aba.rotulo,
                        style = FinanSmartTheme.type.NavLabel.copy(
                            fontWeight = if (ativa) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 9.5.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = if (ativa) c.primaryInk else c.ink3,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
