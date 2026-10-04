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
