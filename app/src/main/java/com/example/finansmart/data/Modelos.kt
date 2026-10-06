package com.example.finansmart.data

import androidx.compose.ui.graphics.Color
import com.example.finansmart.ui.theme.CatAlimentacao
import com.example.finansmart.ui.theme.CatEducacao
import com.example.finansmart.ui.theme.CatLazer
import com.example.finansmart.ui.theme.CatMoradia
import com.example.finansmart.ui.theme.CatOutros
import com.example.finansmart.ui.theme.CatSaude
import com.example.finansmart.ui.theme.CatTransporte

/*
 * OS DADOS DO FINANSMART
 *
 * Aqui ficam as "formas" das informacoes que o app guarda: uma transacao,
 * uma meta, o perfil. Nenhuma tela e desenhada neste arquivo -- isto e so
 * a descricao do que existe.
 *
 * Separar assim deixa claro o que e regra do negocio e o que e aparencia.
 */

/** Despesa (sai dinheiro) ou receita (entra dinheiro). */
enum class TipoTransacao { RECEITA, DESPESA }

/**
 * As 7 categorias do prototipo, cada uma com seu emoji e sua cor.
 * A cor e a mesma nos temas claro e escuro: e a identidade da categoria.
 */
enum class Categoria(
    val rotulo: String,
    val emoji: String,
    val cor: Color,
) {
    ALIMENTACAO("Alimentação", "🛒", CatAlimentacao),
    TRANSPORTE("Transporte", "🚌", CatTransporte),
    MORADIA("Moradia", "🏠", CatMoradia),
    SAUDE("Saúde", "💊", CatSaude),
    EDUCACAO("Educação", "🎓", CatEducacao),
    LAZER("Lazer", "🎬", CatLazer),
    OUTROS("Outros", "⋯", CatOutros),
    /**
     * Categoria interna das receitas. Não aparece na grade de escolha da
     * despesa — serve só para as transações de entrada terem uma cor e um
     * rótulo quando aparecem nas listas.
     */
    RECEITA("Receita", "💰", CatLazer);

    companion object {
        /** As 7 do protótipo, que a pessoa escolhe ao lançar uma despesa. */
        val DESPESAS: List<Categoria> get() = entries.filter { it != RECEITA }
    }
}

/**
 * Categorias de receita.
 *
 * Não estavam no protótipo: foram pedidas no relatório de ajustes, para
 * que a regra "categoria OU descrição" valha igual nas duas telas.
 */
enum class CategoriaReceita(
    val rotulo: String,
    val emoji: String,
    val cor: Color,
) {
    SALARIO("Salário", "💰", CatLazer),
    FREELANCE("Freelance", "💼", CatAlimentacao),
    PRESENTE("Presente", "🎁", CatSaude),
    INVESTIMENTO("Investimento", "📈", CatTransporte),
    OUTROS("Outros", "⋯", CatOutros),
}

/**
 * Uma transacao.
 *
 * O emoji e proprio de cada transacao, nao da categoria: no prototipo o
 * Restaurante usa o hamburguer e o Supermercado usa o carrinho, embora os
 * dois sejam Alimentacao.
 */
data class Transacao(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val tipo: TipoTransacao,
    val categoria: Categoria,
    val emoji: String,
    val dia: Int,
    val mes: Int,
    val ano: Int,
    val hora: String = "",
    val observacao: String = "",
    val formaPagamento: String = "",
) {
    /** "19/09/2026" */
    val dataFormatada: String
        get() = "%02d/%02d/%04d".format(dia, mes, ano)

    /** Chave usada para agrupar a lista por dia. */
    val chaveDoDia: String
        get() = "%04d-%02d-%02d".format(ano, mes, dia)
}

/** Uma meta de economia. */
data class Meta(
    val id: Long,
    val nome: String,
    val emoji: String,
    val objetivo: Double,
    val guardado: Double,
    val prazoMes: Int,
    val prazoAno: Int,
    val reservaAutomatica: Boolean = false,
) {
    val concluida: Boolean get() = guardado >= objetivo

    /** De 0f a 1f, para a barra de progresso. */
    val progresso: Float
        get() = if (objetivo <= 0.0) 0f else (guardado / objetivo).coerceIn(0.0, 1.0).toFloat()

    val percentual: Int get() = (progresso * 100).toInt()

    val falta: Double get() = (objetivo - guardado).coerceAtLeast(0.0)

    /** "dez/2026" */
    val prazoFormatado: String
        get() = MESES_CURTOS[(prazoMes - 1).coerceIn(0, 11)] + "/" + prazoAno
}

/** Os dados da pessoa que usa o app. */
data class Perfil(
    val nome: String,
    val email: String,
    val telefone: String,
    val rendaMensal: Double,
    val verificada: Boolean = true,
) {
    /** "MA" -- as iniciais que aparecem no circulo do avatar. */
    val iniciais: String
        get() = nome.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }

    val primeiroNome: String
        get() = nome.trim().split(" ").firstOrNull().orEmpty()
}

val MESES_CURTOS = listOf(
    "jan", "fev", "mar", "abr", "mai", "jun",
    "jul", "ago", "set", "out", "nov", "dez"
)

val MESES_LONGOS = listOf(
    "JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO",
    "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"
)
