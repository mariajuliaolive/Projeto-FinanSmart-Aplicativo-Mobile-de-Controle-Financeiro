package com.example.finansmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/**
 * Campo de texto do FinanSmart: rótulo em cima, caixa branca com borda.
 * Quando recebe o foco, a borda vira roxa e ganha um brilho em volta.
 */

@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    aoMudarValor: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icone: String? = null,
    ocultarTexto: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    conteudoFinal: @Composable (() -> Unit)? = null,
){
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // Guarda se este campo está com o cursor dentro dele agora.
    val interacoes = remember { MutableInteractionSource() }
    val focado by interacoes.collectIsFocusedAsState()

    Column(modifier){
        Text(text = rotulo, style = t.FieldLabel, color = c.ink2)

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // anel de brilho: transparente quando não está focado
                .border(
                    width = 3.dp,
                    color = if (focado) c.primary.copy(alpha = 0.15f) else Color.Transparent,
                    shape = RoundedCornerShape(Dimens.RadiusField + 3.dp)
                )
                .padding(3.dp)
                .height(Dimens.FieldHeight)
                .clip(RoundedCornerShape(Dimens.RadiusField))
                .background(c.surface)
                .border(
                    width = Dimens.BorderWidth,
                    color = if (focado) c.primary else c.line,
                    shape = RoundedCornerShape(Dimens.RadiusField)
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                if (icone != null) {
                    Text(text = icone, fontSize = 15.sp, modifier = Modifier.alpha(0.5f))
                    Spacer(Modifier.width(10.dp))
                }

                BasicTextField(
                    value = valor,
                    onValueChange = aoMudarValor,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = t.FieldText.copy(color = c.ink),
                    cursorBrush = SolidColor(c.primary),
                    visualTransformation =
                        if (ocultarTexto) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
                    interactionSource = interacoes,
                    decorationBox = { campo ->
                        if (valor.isEmpty()) {
                            Text(text = placeholder, style = t.FieldText, color = c.ink3)
                        }
                        campo()
                    }
                )

                if (conteudoFinal != null) {
                    Spacer(Modifier.width(10.dp))
                    conteudoFinal()
                }
            }
        }
    }
}

