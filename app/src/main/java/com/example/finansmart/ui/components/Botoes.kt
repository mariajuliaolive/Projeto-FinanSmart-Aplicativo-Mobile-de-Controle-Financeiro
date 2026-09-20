package com.example.finansmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/** Botão roxo de ação principal. Ocupa a largura toda. */
@Composable
fun BotaoPrimario(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(Dimens.RadiusButton),
                ambientColor = c.primary,
                spotColor = c.primary
            )
            .clip(RoundedCornerShape(Dimens.RadiusButton))
            .background(
                if (habilitado) c.primaryFill else c.primaryFill.copy(alpha = 0.4f)
            )
            .clickable(enabled = habilitado, onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = FinanSmartTheme.type.Button, color = Color.White)
    }
}

/** Botão branco com borda. Usado no "Google" e "Face ID". */
@Composable
fun BotaoSecundario(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    Box(
        modifier = modifier
            .height(Dimens.ButtonHeightSmall)
            .clip(RoundedCornerShape(Dimens.RadiusField))
            .background(c.surface)
            .border(Dimens.BorderWidth, c.line, RoundedCornerShape(Dimens.RadiusField))
            .clickable(onClick = aoClicar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = FinanSmartTheme.type.Footer.copy(fontWeight = FontWeight.Medium),
            color = c.ink
        )
    }
}