package com.example.finansmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/**
 * Quadradinho com a seta no alto da tela.
 *
 * 42dp no Cadastro (sozinho) e 40dp quando vem ao lado de um titulo,
 * como nas telas 06, 10, 11 e 12 -- as duas medidas vem do prototipo.
 */
@Composable
fun BotaoVoltar(
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    tamanho: Dp = 42.dp,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier = modifier
            .size(tamanho)
            .clip(RoundedCornerShape(Dimens.RadiusField))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "←", fontSize = 16.sp, color = c.ink)
    }
}

/**
 * Cabecalho das telas internas: a seta de voltar e o titulo ao lado,
 * com um espaco opcional a direita (o "..." da tela 10, por exemplo).
 */
@Composable
fun CabecalhoTela(
    titulo: String,
    aoVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    acaoDireita: (@Composable () -> Unit)? = null,
) {
    val c = FinanSmartTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotaoVoltar(aoClicar = aoVoltar, tamanho = 40.dp)
        Spacer(Modifier.width(Dimens.Space12))
        Text(
            text = titulo,
            style = FinanSmartTheme.type.TituloInterno,
            color = c.ink,
            modifier = Modifier.weight(1f)
        )
        if (acaoDireita != null) acaoDireita()
    }
}
