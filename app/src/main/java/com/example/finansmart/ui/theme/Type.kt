package com.example.finansmart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.finansmart.R

/*
 * FONTES
 *
 * Os arquivos .ttf ficam em res/font/. O Android os enxerga pelo
 * R.font.<nome_do_arquivo_sem_extensao>.
 *
 * Aqui juntamos os 4 arquivos do DM Sans numa "familia". Depois, quando
 * o codigo pedir DMSans com FontWeight.Bold, o Compose escolhe sozinho
 * o arquivo dm_sans_bold.ttf.
 */

val DMSans = FontFamily(
    Font(R.font.dm_sans_regular,  FontWeight.Normal),   // 400
    Font(R.font.dm_sans_medium,   FontWeight.Medium),   // 500
    Font(R.font.dm_sans_semibold, FontWeight.SemiBold), // 600
    Font(R.font.dm_sans_bold,     FontWeight.Bold),     // 700
)

val DMMono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),    // 400
    Font(R.font.dm_mono_medium,  FontWeight.Medium),    // 500
)

/*
 * ESCALA DE TEXTO
 *
 * Cada estilo abaixo veio de uma medida real do prototipo. O nome diz
 * ONDE se usa, nao COMO e -- assim, se um dia o tamanho mudar, o nome
 * continua fazendo sentido.
 *
 * sp = "scale-independent pixels": respeita o tamanho de fonte que a
 * pessoa configurou no proprio celular (importante p/ acessibilidade).
 * dp = so tamanho fisico, nao muda. Por isso texto usa sp e caixa usa dp.
 */

object FinanSmartType {

    /* --- titulos --- */
    val SplashTitle = TextStyle(               // "FinanSmart" na splash
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 37.4.sp, letterSpacing = (-0.8).sp
    )
    val Balance = TextStyle(                   // "R$ 2.450,00" no card do saldo
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 39.6.sp, letterSpacing = (-1).sp
    )
    val ScreenTitle = TextStyle(               // "Bem-vinda de volta", "Criar conta"
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 33.6.sp, letterSpacing = (-0.5).sp
    )
    val Greeting = TextStyle(                  // "Ola, Maria!"
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 19.sp, lineHeight = 22.8.sp
    )
    val SectionTitle = TextStyle(              // "Gastos deste mes", "Ultimas transacoes"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp
    )

    /* --- valores --- */
    val CardValue = TextStyle(                 // "R$ 3.000,00" nos cards de receita/despesa
        fontFamily = DMSans, fontWeight = FontWeight.Bold, fontSize = 18.sp
    )
    val MonoValue = TextStyle(                 // "R$ 180" na lista de categorias
        fontFamily = DMMono, fontWeight = FontWeight.Medium, fontSize = 13.sp
    )

    /* --- texto corrido --- */
    val Body = TextStyle(                      // subtitulos das telas
        fontFamily = DMSans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp
    )
    val Footer = TextStyle(                    // "Ainda nao possui uma conta?"
        fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 13.5.sp
    )
    val Caption = TextStyle(                   // "Voce gastou 18% da sua renda"
        fontFamily = DMSans, fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp, lineHeight = 17.5.sp
    )

    /* --- botoes e campos --- */
    val Button = TextStyle(                    // "Entrar", "Criar conta"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp
    )
    val ButtonSmall = TextStyle(               // "+ Nova", "Enviar"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp
    )
    val FieldText = TextStyle(                 // o que a pessoa digita
        fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 14.5.sp
    )
    val FieldLabel = TextStyle(                // "E-mail", "Senha"
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 12.5.sp
    )

    /* --- listas --- */
    val ListTitle = TextStyle(                 // "Supermercado"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
    )
    val ListSubtitle = TextStyle(              // "Hoje - Alimentacao"
        fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 11.5.sp
    )
    val ListValue = TextStyle(                 // "-R$ 120,00"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
    )
    val StatLabel = TextStyle(                 // "Receitas", "Despesas"
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 12.sp
    )

    /* --- navegacao e rotulos --- */
    val NavLabel = TextStyle(                  // "Inicio", "Transacoes"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 10.sp
    )

    /* --- titulos das telas internas (05 a 12) --- */
    val TituloLista = TextStyle(               // "Minhas transacoes", "Relatorios"
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, letterSpacing = (-0.4).sp
    )
    val TituloInterno = TextStyle(             // "Nova transacao", "Meu perfil"
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 21.sp, letterSpacing = (-0.3).sp
    )

    /* --- valores grandes --- */
    val ValorEntrada = TextStyle(              // o valor na tela 06
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, letterSpacing = (-1.2).sp
    )
    val ValorDetalhe = TextStyle(              // o valor na tela 10
        fontFamily = DMSans, fontWeight = FontWeight.Bold,
        fontSize = 38.sp, letterSpacing = (-1.2).sp
    )
    val SaldoPeriodo = TextStyle(              // "R$ 2.450,00" na tela 07
        fontFamily = DMSans, fontWeight = FontWeight.Bold, fontSize = 22.sp
    )
    val ValorMedio = TextStyle(                // cards de receita/despesa da tela 07
        fontFamily = DMSans, fontWeight = FontWeight.Bold, fontSize = 17.sp
    )

    /* --- metas (tela 08) --- */
    val MetaNome = TextStyle(
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
    )
    val MetaPercentual = TextStyle(
        fontFamily = DMSans, fontWeight = FontWeight.Bold, fontSize = 17.sp
    )
    val MetaDetalhe = TextStyle(
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 12.sp
    )

    /* --- listas e configuracoes --- */
    val ItemConfig = TextStyle(                // "Meu perfil", "Tema", "Moeda"
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 14.5.sp
    )
    val DetalheRotulo = TextStyle(             // "Data", "Categoria" na tela 10
        fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 13.5.sp
    )
    val DetalheValor = TextStyle(              // o que fica a direita deles
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 13.5.sp
    )

    /* --- pilulas e botoes --- */
    val Pilula = TextStyle(                    // "Este mes", "Categoria", "Tipo"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
    )
    val PilulaCategoria = TextStyle(           // as categorias da tela 06
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp
    )
    val BotaoGrande = TextStyle(               // "Adicionar transacao", "Criar meta"
        fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
    )

    /* --- rotulos pequenos --- */
    val Rotulo115 = TextStyle(                 // legendas dos graficos
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 11.5.sp
    )
    val Rotulo11 = TextStyle(                  // "Jun", "Jul", "Sem 1"
        fontFamily = DMSans, fontWeight = FontWeight.Medium, fontSize = 11.sp
    )
    val MonoSecao = TextStyle(                 // "HOJE - 19 SET", "CONTA"
        fontFamily = DMMono, fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp, letterSpacing = 1.sp
    )

    val MonoLabel = TextStyle(                 // "SETEMBRO", "CARREGANDO..."
        fontFamily = DMMono, fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp, letterSpacing = 1.sp
    )
}

/*
 * O Compose tem componentes prontos (Text, Button...) que procuram um
 * Typography do Material. Preenchemos ele aqui so para que nada no app
 * apareca com a fonte padrao do sistema por engano.
 */
val AppTypography = Typography(
    displayLarge  = FinanSmartType.Balance,
    headlineLarge = FinanSmartType.ScreenTitle,
    titleLarge    = FinanSmartType.Greeting,
    titleMedium   = FinanSmartType.SectionTitle,
    bodyLarge     = FinanSmartType.Body,
    bodyMedium    = FinanSmartType.FieldText,
    bodySmall     = FinanSmartType.Caption,
    labelLarge    = FinanSmartType.Button,
    labelMedium   = FinanSmartType.FieldLabel,
    labelSmall    = FinanSmartType.NavLabel,
)
