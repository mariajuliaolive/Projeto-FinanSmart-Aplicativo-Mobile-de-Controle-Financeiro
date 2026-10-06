package com.example.finansmart.data

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/*
 * FORMATACAO DE DINHEIRO
 *
 * O Brasil escreve 3.000,00 -- ponto para milhar, virgula para centavos.
 * Reunimos as funcoes aqui para que nenhuma tela precise se preocupar
 * com isso, e para que todas escrevam igual.
 */

private val SIMBOLOS_BR = DecimalFormatSymbols(Locale("pt", "BR")).apply {
    decimalSeparator = ','
    groupingSeparator = '.'
}

private val COM_CENTAVOS = DecimalFormat("#,##0.00", SIMBOLOS_BR)
private val SEM_CENTAVOS = DecimalFormat("#,##0", SIMBOLOS_BR)

/** 3000.0 -> "R$ 3.000,00" */
fun formatarMoeda(valor: Double): String = "R$ " + COM_CENTAVOS.format(valor)

/** 3000.0 -> "R$ 3.000"  (usado nos relatorios, onde nao cabe o centavo) */
fun formatarMoedaCurta(valor: Double): String = "R$ " + SEM_CENTAVOS.format(valor)

/** 120.0 + DESPESA -> "-R$ 120,00"   3000.0 + RECEITA -> "+R$ 3.000,00" */
fun formatarComSinal(valor: Double, tipo: TipoTransacao): String {
    val sinal = if (tipo == TipoTransacao.RECEITA) "+" else "-"
    return sinal + formatarMoeda(valor)
}

/** Le "120,50" ou "120.50" ou "1.200,50" e devolve o numero. Invalido -> 0.0 */
fun lerValorDigitado(texto: String): Double {
    val limpo = texto
        .replace("R$", "")
        .replace(" ", "")
        .replace(".", "")
        .replace(",", ".")
        .trim()
    return limpo.toDoubleOrNull() ?: 0.0
}

/*
 * DIGITACAO DE VALOR
 *
 * O jeito que os apps de banco fazem: a pessoa digita so numeros e eles
 * entram pela direita, como centavos.
 *
 *   digita 5     -> R$ 0,05
 *   digita 50    -> R$ 0,50
 *   digita 500   -> R$ 5,00
 *   digita 5000  -> R$ 50,00
 *
 * Guardamos o valor em CENTAVOS (um numero inteiro), nunca em texto.
 * Assim nao existe ambiguidade entre ponto e virgula, e nao ha erro de
 * arredondamento de ponto flutuante na digitacao.
 */

/** Acrescenta um digito ao valor em centavos. Ignora o que nao for numero. */
fun acrescentarDigito(centavos: Long, tecla: Char): Long {
    if (!tecla.isDigit()) return centavos
    val novo = centavos * 10 + (tecla - '0')
    // Trava em 999.999.999,99 para nao estourar o campo nem o Long.
    return if (novo > 99_999_999_999L) centavos else novo
}

/** Le o texto que veio do teclado e devolve o total em centavos. */
fun centavosDoTexto(texto: String): Long {
    var total = 0L
    texto.forEach { total = acrescentarDigito(total, it) }
    return total
}

/** 12345 centavos -> "R$ 123,45" */
fun formatarCentavos(centavos: Long): String = formatarMoeda(centavos / 100.0)

/** 12345 centavos -> 123.45 */
fun centavosParaReais(centavos: Long): Double = centavos / 100.0
