package com.example.finansmart.ui.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finansmart.data.MESES_LONGOS
import com.example.finansmart.data.Repositorio
import com.example.finansmart.data.TipoTransacao
import com.example.finansmart.data.formatarMoeda
import com.example.finansmart.ui.components.*
import com.example.finansmart.ui.theme.Dimens
import com.example.finansmart.ui.theme.FinanSmartTheme

/*
 * TELAS QUE NAO EXISTIAM NO PROTOTIPO
 *
 * O relatorio de ajustes apontou que havia icones e itens de menu sem
 * destino: o sino do Dashboard e tres linhas das Configuracoes. Estas
 * telas fecham esses caminhos, seguindo o mesmo tema das demais.
 */

/* ------------------------- NOTIFICACOES ------------------------- */

private data class Aviso(
    val emoji: String,
    val titulo: String,
    val texto: String,
    val quando: String,
    val naoLido: Boolean,
)

@Composable
fun NotificacoesScreen(
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    // Os avisos sao montados a partir dos dados reais do app.
    val despesas = Repositorio.totalDespesas()
    val receitas = Repositorio.totalReceitas()
    val maiorCategoria = Repositorio.gastosPorCategoria().firstOrNull()
    val metaQuaseLa = Repositorio.metasEmAndamento().maxByOrNull { it.progresso }
    val ultimaReceita = Repositorio.transacoes
        .firstOrNull { it.tipo == TipoTransacao.RECEITA }

    val avisos = buildList {
        if (maiorCategoria != null) {
            val (categoria, valor) = maiorCategoria
            add(
                Aviso(
                    emoji = categoria.emoji,
                    titulo = "${categoria.rotulo} é seu maior gasto",
                    texto = "Você gastou ${formatarMoeda(valor)} em ${categoria.rotulo} " +
                            "neste mês, de um total de ${formatarMoeda(despesas)}.",
                    quando = "Hoje",
                    naoLido = true,
                )
            )
        }
        if (metaQuaseLa != null) {
            add(
                Aviso(
                    emoji = metaQuaseLa.emoji,
                    titulo = "Meta ${metaQuaseLa.nome}: ${metaQuaseLa.percentual}%",
                    texto = "Faltam ${formatarMoeda(metaQuaseLa.falta)} para você " +
                            "alcançar o objetivo.",
                    quando = "Hoje",
                    naoLido = true,
                )
            )
        }
        if (ultimaReceita != null) {
            add(
                Aviso(
                    emoji = "💰",
                    titulo = "Receita registrada",
                    texto = "${ultimaReceita.descricao} de " +
                            "${formatarMoeda(ultimaReceita.valor)} entrou na sua conta.",
                    quando = ultimaReceita.dataFormatada,
                    naoLido = false,
                )
            )
        }
        add(
            Aviso(
                emoji = "📊",
                titulo = "Resumo de ${MESES_LONGOS[8].lowercase()}",
                texto = "Entraram ${formatarMoeda(receitas)} e saíram " +
                        "${formatarMoeda(despesas)}. Seu saldo está positivo.",
                quando = "01/10",
                naoLido = false,
            )
        )
    }

    MolduraDeTela(titulo = "Notificações", onVoltar = onVoltar, modifier = modifier) {
        if (avisos.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔔", fontSize = 40.sp)
                Spacer(Modifier.height(Dimens.Space16))
                Text(
                    text = "Nenhuma notificação por enquanto.",
                    style = t.Body,
                    color = c.ink2,
                    textAlign = TextAlign.Center
                )
            }
            return@MolduraDeTela
        }

        avisos.forEachIndexed { indice, aviso ->
            if (indice > 0) Spacer(Modifier.height(Dimens.Space8))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
                    .background(if (aviso.naoLido) c.primarySoft else c.surface)
                    .padding(Dimens.Space16),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    Modifier
                        .size(Dimens.IconBoxMedium)
                        .clip(RoundedCornerShape(Dimens.RadiusField))
                        .background(if (aviso.naoLido) c.surface else c.surface2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = aviso.emoji, fontSize = 18.sp)
                }
                Spacer(Modifier.width(Dimens.Space12))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = aviso.titulo,
                            style = t.ListTitle,
                            color = c.ink,
                            modifier = Modifier.weight(1f)
                        )
                        if (aviso.naoLido) {
                            Box(
                                Modifier
                                    .size(7.dp)
                                    .clip(RoundedCornerShape(Dimens.RadiusPill))
                                    .background(c.primaryFill)
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(text = aviso.texto, style = t.Caption, color = c.ink2)
                    Spacer(Modifier.height(5.dp))
                    Text(text = aviso.quando, style = t.ListSubtitle, color = c.ink3)
                }
            }
        }
    }
}

/* ------------------------- ALTERAR SENHA ------------------------- */

@Composable
fun AlterarSenhaScreen(
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var atual by remember { mutableStateOf("") }
    var nova by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var visivel by remember { mutableStateOf(false) }
    var salvou by remember { mutableStateOf(false) }

    val conferem = nova.isNotBlank() && nova == confirmar
    val podeSalvar = atual.isNotBlank() && nova.length >= 8 && conferem

    MolduraDeTela(titulo = "Alterar senha", onVoltar = onVoltar, modifier = modifier) {
        Text(
            text = "Escolha uma senha com pelo menos 8 caracteres.",
            style = t.Body,
            color = c.ink2
        )

        Spacer(Modifier.height(Dimens.Space22))

        CampoTexto(
            rotulo = "Senha atual",
            valor = atual,
            aoMudarValor = { atual = it; salvou = false },
            placeholder = "••••••••",
            ocultarTexto = !visivel,
            conteudoFinal = {
                BotaoOlho(visivel = visivel, aoAlternar = { visivel = !visivel })
            }
        )

        Spacer(Modifier.height(13.dp))
        CampoTexto(
            rotulo = "Nova senha",
            valor = nova,
            aoMudarValor = { nova = it; salvou = false },
            placeholder = "••••••••",
            ocultarTexto = !visivel
        )

        Spacer(Modifier.height(13.dp))
        CampoTexto(
            rotulo = "Confirmar nova senha",
            valor = confirmar,
            aoMudarValor = { confirmar = it; salvou = false },
            placeholder = "••••••••",
            ocultarTexto = !visivel,
            conteudoFinal = if (conferem) {
                { Text(text = "✓", fontSize = 14.sp, color = c.pos) }
            } else null
        )

        if (confirmar.isNotBlank() && !conferem) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "⚠ As duas senhas não são iguais.",
                style = t.ListSubtitle,
                color = c.neg
            )
        }

        Spacer(Modifier.height(Dimens.Space22))

        BotaoPrimario(
            texto = if (salvou) "Senha alterada ✓" else "Salvar nova senha",
            aoClicar = { salvou = true },
            habilitado = podeSalvar && !salvou
        )

        if (!podeSalvar) {
            Spacer(Modifier.height(Dimens.Space8))
            Text(
                text = ExplicacaoDoBotao(
                    listOf(
                        "a senha atual" to atual.isBlank(),
                        "uma nova senha de 8+ caracteres" to (nova.length < 8),
                        "a confirmação igual à nova" to !conferem,
                    )
                ),
                style = t.ListSubtitle,
                color = c.ink3
            )
        }

        Spacer(Modifier.height(Dimens.Space16))
        AvisoDePrototipo(
            "Esta tela ainda não troca a senha de verdade: o FinanSmart não " +
                    "tem servidor. A validação funciona, o envio é o próximo passo."
        )
    }
}

/* ------------------------- PRIVACIDADE ------------------------- */

@Composable
fun PrivacidadeScreen(
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    var biometria by remember { mutableStateOf(false) }
    var ocultarSaldo by remember { mutableStateOf(false) }
    var compartilhar by remember { mutableStateOf(false) }

    MolduraDeTela(titulo = "Privacidade", onVoltar = onVoltar, modifier = modifier) {
        RotuloSecao(texto = "ACESSO")
        Spacer(Modifier.height(9.dp))
        GrupoBranco {
            LinhaDeOpcao(
                emoji = "🔐",
                texto = "Exigir biometria ao abrir",
                mostrarSeta = false,
                conteudoDireita = {
                    Interruptor(ligado = biometria, aoAlternar = { biometria = !biometria })
                }
            )
            Divisoria()
            LinhaDeOpcao(
                emoji = "🙈",
                texto = "Começar com o saldo oculto",
                mostrarSeta = false,
                conteudoDireita = {
                    Interruptor(ligado = ocultarSaldo, aoAlternar = { ocultarSaldo = !ocultarSaldo })
                }
            )
        }

        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "DADOS")
        Spacer(Modifier.height(9.dp))
        GrupoBranco {
            LinhaDeOpcao(
                emoji = "📈",
                texto = "Compartilhar estatísticas de uso",
                mostrarSeta = false,
                conteudoDireita = {
                    Interruptor(ligado = compartilhar, aoAlternar = { compartilhar = !compartilhar })
                }
            )
        }

        Spacer(Modifier.height(Dimens.Space18))
        Text(
            text = "Seus lançamentos ficam apenas neste aparelho. O FinanSmart " +
                    "não envia nada para a internet e não usa seus dados para " +
                    "publicidade.",
            style = t.Caption,
            color = c.ink2
        )

        Spacer(Modifier.height(Dimens.Space16))
        AvisoDePrototipo(
            "As chaves acima ainda não mudam o comportamento do app — são a " +
                    "interface pronta para quando a biometria e as preferências forem ligadas."
        )
    }
}

/* ------------------------- SOBRE ------------------------- */

@Composable
fun SobreScreen(
    onVoltar: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = FinanSmartTheme.colors
    val t = FinanSmartTheme.type

    MolduraDeTela(titulo = "Sobre o FinanSmart", onVoltar = onVoltar, modifier = modifier) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(Dimens.LogoSmall)
                    .clip(RoundedCornerShape(Dimens.RadiusLogoSmall))
                    .background(c.primaryFill),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .rotate(45f)
                        .border(3.dp, Color.White, RoundedCornerShape(6.dp))
                )
            }
            Spacer(Modifier.height(Dimens.Space12))
            Text(text = "FinanSmart", style = t.MetaNome.copy(fontSize = 18.sp), color = c.ink)
            Spacer(Modifier.height(4.dp))
            Text(text = "Versão 1.0.0", style = t.ListSubtitle, color = c.ink3)
        }

        Spacer(Modifier.height(Dimens.Space22))

        Text(
            text = "Seu dinheiro organizado, do jeito simples.",
            style = t.Body.copy(fontSize = 15.sp),
            color = c.ink
        )
        Spacer(Modifier.height(Dimens.Space12))
        Text(
            text = "O FinanSmart é um aplicativo de controle financeiro pessoal: " +
                    "você registra o que entra e o que sai, acompanha para onde " +
                    "o dinheiro está indo e define metas de economia.",
            style = t.Body,
            color = c.ink2
        )

        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "O QUE DÁ PARA FAZER")
        Spacer(Modifier.height(9.dp))
        GrupoBranco {
            LinhaDeOpcao(emoji = "💳", texto = "Lançar receitas e despesas", mostrarSeta = false)
            Divisoria()
            LinhaDeOpcao(emoji = "📊", texto = "Ver relatórios por categoria", mostrarSeta = false)
            Divisoria()
            LinhaDeOpcao(emoji = "🎯", texto = "Acompanhar metas de economia", mostrarSeta = false)
        }

        Spacer(Modifier.height(22.dp))
        RotuloSecao(texto = "CRÉDITOS")
        Spacer(Modifier.height(9.dp))
        GrupoBranco {
            LinhaDeOpcao(emoji = "👩‍💻", texto = "Maria Julia Oliveira", mostrarSeta = false)
            Divisoria()
            LinhaDeOpcao(emoji = "📱", texto = "Kotlin e Jetpack Compose", mostrarSeta = false)
            Divisoria()
            LinhaDeOpcao(emoji = "🔤", texto = "Fontes DM Sans e DM Mono", mostrarSeta = false)
        }

        Spacer(Modifier.height(Dimens.Space20))
    }
}

/* ------------------------- PECAS COMUNS ------------------------- */

/** Cabeçalho com voltar + conteúdo rolável. Usado pelas quatro telas acima. */
@Composable
private fun MolduraDeTela(
    titulo: String,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    val c = FinanSmartTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .background(c.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Spacer(Modifier.height(6.dp))
        CabecalhoTela(titulo = titulo, aoVoltar = onVoltar)
        Spacer(Modifier.height(Dimens.Space22))
        conteudo()
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GrupoBranco(conteudo: @Composable ColumnScope.() -> Unit) {
    val c = FinanSmartTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(c.surface),
        content = conteudo
    )
}

/** Faixa que avisa com honestidade o que ainda não está ligado. */
@Composable
private fun AvisoDePrototipo(texto: String) {
    val c = FinanSmartTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusCardSmall))
            .background(c.surface2)
            .padding(Dimens.Space16),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "ℹ️", fontSize = 15.sp)
        Spacer(Modifier.width(10.dp))
        Text(text = texto, style = FinanSmartTheme.type.Caption, color = c.ink2)
    }
}

/** Monta "Falta: a senha atual e a confirmação." a partir do que está pendente. */
@Composable
private fun ExplicacaoDoBotao(itens: List<Pair<String, Boolean>>): String {
    val faltando = itens.filter { it.second }.map { it.first }
    return when (faltando.size) {
        0 -> ""
        1 -> "Falta informar ${faltando[0]}."
        else -> "Falta informar " + faltando.dropLast(1).joinToString(", ") +
                " e " + faltando.last() + "."
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NotificacoesPreview() {
    FinanSmartTheme { NotificacoesScreen() }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SobrePreview() {
    FinanSmartTheme { SobreScreen() }
}
