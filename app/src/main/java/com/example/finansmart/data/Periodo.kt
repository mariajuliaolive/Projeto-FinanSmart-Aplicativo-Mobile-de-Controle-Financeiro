package com.example.finansmart.data

import java.util.Calendar

/*
 * PERIODO: "qual mes e hoje?" e as contas feitas por mes.
 *
 * O prototipo tinha setembro/2026 escrito direto no codigo. Agora que o
 * app guarda dados de verdade, tudo passa a partir da data do celular.
 *
 * Usamos Calendar (e nao LocalDate) porque o app roda a partir do
 * Android 7 (minSdk 24), e LocalDate so existe a partir do Android 8.
 */

/** Um mes de um ano: MesAno(11, 2026) = novembro de 2026. */
data class MesAno(val mes: Int, val ano: Int) {

    fun anterior(): MesAno = if (mes == 1) MesAno(12, ano - 1) else MesAno(mes - 1, ano)

    fun proximo(): MesAno = if (mes == 12) MesAno(1, ano + 1) else MesAno(mes + 1, ano)

    /** "Novembro" */
    val nomeLongo: String
        get() = MESES_LONGOS[mes - 1].lowercase().replaceFirstChar { it.uppercase() }

    /** "nov" */
    val nomeCurto: String
        get() = MESES_CURTOS[mes - 1]

    /** Para comparar meses: 2026*12+11 > 2026*12+10. */
    val numero: Int
        get() = ano * 12 + mes

    companion object {
        fun atual(): MesAno {
            val hoje = Calendar.getInstance()
            return MesAno(hoje.get(Calendar.MONTH) + 1, hoje.get(Calendar.YEAR))
        }
    }
}

/** Data de hoje como Triple(dia, mes, ano). */
fun hoje(): Triple<Int, Int, Int> {
    val c = Calendar.getInstance()
    return Triple(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
}

/** Data de ontem (cuida da virada de mes e de ano sozinho). */
fun ontem(): Triple<Int, Int, Int> {
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_MONTH, -1)
    return Triple(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
}

/** "25/11/2026" */
fun hojeFormatado(): String {
    val (dia, mes, ano) = hoje()
    return "%02d/%02d/%04d".format(dia, mes, ano)
}

/* ---------------- contas sobre uma lista de transacoes ---------------- */
/*
 * Funcoes de extensao: "List<Transacao>.doMes(...)" se le como se fosse
 * um metodo da propria lista -> transacoes.doMes(MesAno.atual()).
 * Sao Kotlin puro: entra lista, sai numero. Faceis de ler e de testar.
 */

fun List<Transacao>.doMes(periodo: MesAno): List<Transacao> =
    filter { it.mes == periodo.mes && it.ano == periodo.ano }

fun List<Transacao>.somaReceitas(): Double =
    filter { it.tipo == TipoTransacao.RECEITA }.sumOf { it.valor }

fun List<Transacao>.somaDespesas(): Double =
    filter { it.tipo == TipoTransacao.DESPESA }.sumOf { it.valor }

/** Quanto foi gasto em cada categoria, da maior para a menor. */
fun List<Transacao>.despesasPorCategoria(): List<Pair<Categoria, Double>> =
    filter { it.tipo == TipoTransacao.DESPESA }
        .groupBy { it.categoria }
        .map { (categoria, lista) -> categoria to lista.sumOf { it.valor } }
        .sortedByDescending { it.second }

/**
 * Despesas de um mes divididas em 4 semanas:
 * dias 1-7, 8-14, 15-21 e 22 ate o fim (a ultima "semana" leva os dias extras).
 */
fun List<Transacao>.despesasPorSemana(): List<Double> {
    val semanas = DoubleArray(4)
    filter { it.tipo == TipoTransacao.DESPESA }.forEach { t ->
        val indice = ((t.dia - 1) / 7).coerceIn(0, 3)
        semanas[indice] += t.valor
    }
    return semanas.toList()
}
