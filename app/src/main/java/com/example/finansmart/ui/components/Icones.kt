package com.example.finansmart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finansmart.R
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * ICONES DESENHADOS
 *
 * Emoji nao serve para tudo: o tamanho depende da fonte do sistema e o
 * desenho muda de celular para celular. Onde o icone precisa ser preciso
 * -- o olho da senha e o check da caixinha -- usamos vetor ou Canvas.
 */

/**
 * Olho de mostrar/esconder, o mesmo desenho que os apps de banco usam.
 * Substitui os emojis 👁 e 🙈, que vinham em tamanhos diferentes e
 * destoavam do resto da interface.
 */
@Composable
fun BotaoOlho(
    visivel: Boolean,
    aoAlternar: () -> Unit,
    modifier: Modifier = Modifier,
    cor: Color? = null,
    tamanho: Dp = 22.dp,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier = modifier
            .size(tamanho + 16.dp)          // area de toque confortavel
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .clickable(onClick = aoAlternar),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                if (visivel) R.drawable.ic_olho else R.drawable.ic_olho_cortado
            ),
            contentDescription = if (visivel) "Esconder" else "Mostrar",
            tint = cor ?: c.ink3,
            modifier = Modifier.size(tamanho)
        )
    }
}

/**
 * Caixinha de marcar.
 *
 * O "✓" como texto vazava para fora do quadrado: a fonte de emoji
 * desenha o glifo maior que a caixa. Aqui o check e desenhado a mao,
 * dois tracos dentro dos limites do quadrado.
 */
@Composable
fun CaixaDeMarcar(
    marcada: Boolean,
    aoAlternar: () -> Unit,
    modifier: Modifier = Modifier,
    tamanho: Dp = 22.dp,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier = modifier
            .size(tamanho)
            .clip(RoundedCornerShape(6.dp))
            .background(if (marcada) c.primaryFill else c.surface2)
            .clickable(onClick = aoAlternar),
        contentAlignment = Alignment.Center
    ) {
        if (marcada) {
            Canvas(Modifier.size(tamanho * 0.55f)) {
                val traco = size.minDimension * 0.18f
                // O check vai de (0.08, 0.52) a (0.40, 0.84) a (0.92, 0.20),
                // em fracoes do quadrado -- nunca passa das bordas.
                val p1 = Offset(size.width * 0.08f, size.height * 0.52f)
                val p2 = Offset(size.width * 0.40f, size.height * 0.84f)
                val p3 = Offset(size.width * 0.92f, size.height * 0.20f)
                drawLine(Color.White, p1, p2, strokeWidth = traco, cap = StrokeCap.Round)
                drawLine(Color.White, p2, p3, strokeWidth = traco, cap = StrokeCap.Round)
            }
        }
    }
}

/** Seta ">" das listas de configuracao, desenhada em vez de caractere. */
@Composable
fun SetaDireita(modifier: Modifier = Modifier, cor: Color? = null) {
    val c = FinanSmartTheme.colors
    Canvas(modifier.size(14.dp)) {
        val traco = size.minDimension * 0.14f
        val x1 = size.width * 0.34f
        val x2 = size.width * 0.66f
        drawLine(
            cor ?: c.ink3,
            Offset(x1, size.height * 0.18f),
            Offset(x2, size.height * 0.50f),
            strokeWidth = traco, cap = StrokeCap.Round
        )
        drawLine(
            cor ?: c.ink3,
            Offset(x2, size.height * 0.50f),
            Offset(x1, size.height * 0.82f),
            strokeWidth = traco, cap = StrokeCap.Round
        )
    }
}

/** Logo do Google em miniatura, para o botao "Entrar com Google". */
@Composable
fun MarcaGoogle(tamanho: Dp = 18.dp) {
    Canvas(Modifier.size(tamanho)) {
        val r = size.minDimension / 2f
        val centro = Offset(size.width / 2f, size.height / 2f)
        // Quatro quadrantes nas cores da marca, formando um circulo.
        val cores = listOf(
            Color(0xFF4285F4), Color(0xFF34A853),
            Color(0xFFFBBC05), Color(0xFFEA4335),
        )
        cores.forEachIndexed { indice, cor ->
            drawArc(
                color = cor,
                startAngle = -90f + indice * 90f,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = Offset(centro.x - r, centro.y - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
        }
        drawCircle(Color.White, radius = r * 0.42f, center = centro)
    }
}

/** Linha com o logo do Google e o texto, usada no botao de login. */
@Composable
fun ConteudoBotaoGoogle(texto: String = "Entrar com Google") {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MarcaGoogle()
        Spacer(Modifier.width(10.dp))
        Text(
            text = texto,
            style = FinanSmartTheme.type.Footer.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            ),
            color = FinanSmartTheme.colors.ink
        )
    }
}
