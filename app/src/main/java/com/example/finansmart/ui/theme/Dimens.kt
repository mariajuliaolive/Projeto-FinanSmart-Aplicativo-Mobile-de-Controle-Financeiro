package com.example.finansmart.ui.theme

import androidx.compose.ui.unit.dp

/*
 * MEDIDAS QUE SE REPETEM
 *
 * No prototipo, todo campo tem 52px de altura, todo botao principal 54px,
 * todo card pequeno tem canto de 16px. Em vez de repetir esses numeros em
 * 12 telas, eles ficam aqui.
 *
 * dp = "density-independent pixels". Um botao de 54.dp tem o mesmo tamanho
 * FISICO num celular de tela boa e num mais simples -- o Android converte
 * sozinho. Por isso usamos dp, nunca pixels crus.
 */

object Dimens {

    /* --- alturas fixas --- */
    val FieldHeight       = 52.dp   // campo de texto
    val ButtonHeight      = 54.dp   // botao principal ("Entrar", "Criar conta")
    val ButtonHeightSmall = 50.dp   // botao secundario (Google, Face ID)
    val ButtonHeightInline= 40.dp   // botoes dentro do card do saldo
    val NavBarHeight      = 64.dp   // barra inferior de 5 abas

    /* --- quadrados de icone --- */
    val IconBoxSmall  = 24.dp   // seta dentro do card de receita/despesa
    val IconBoxMedium = 40.dp   // icone do item de transacao
    val IconBoxLarge  = 44.dp   // avatar e botao de notificacao
    val LogoSmall     = 56.dp   // logo no login
    val LogoLarge     = 92.dp   // logo na splash

    /* --- arredondamento dos cantos --- */
    val RadiusField      = 12.dp  // campos e botoes pequenos
    val RadiusButton     = 14.dp  // botao principal
    val RadiusCardSmall  = 16.dp  // card de receita/despesa, item de lista
    val RadiusCardMedium = 20.dp  // card de secao
    val RadiusCardLarge  = 24.dp  // card escuro do saldo
    val RadiusLogoSmall  = 18.dp  // logo do login
    val RadiusLogoLarge  = 28.dp  // logo da splash
    val RadiusPill       = 99.dp  // totalmente redondo (avatar, barra de progresso)

    /* --- margens laterais da tela --- */
    val ScreenPaddingAuth = 28.dp  // splash, login, cadastro
    val ScreenPadding     = 20.dp  // telas internas

    /* --- espacos entre elementos --- */
    val Space4  = 4.dp
    val Space8  = 8.dp
    val Space12 = 12.dp
    val Space16 = 16.dp
    val Space18 = 18.dp
    val Space22 = 22.dp
    val Space26 = 26.dp
    val Space32 = 32.dp

    /* --- detalhes --- */
    val BorderWidth   = 1.dp   // borda dos cards e campos
    val ProgressTrack = 4.dp   // altura da barrinha de carregamento
    val BarThickness  = 14.dp  // altura da barra de gastos por categoria
    val LegendDot     = 8.dp   // bolinha colorida da legenda
}
