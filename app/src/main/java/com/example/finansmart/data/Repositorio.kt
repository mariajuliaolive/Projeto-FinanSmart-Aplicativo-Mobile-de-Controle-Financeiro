package com.example.finansmart.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

/*
 * O REPOSITORIO
 *
 * Guarda as transacoes, as metas e o perfil, e e o unico lugar do app que
 * pode muda-los. As telas leem daqui e chamam as funcoes daqui -- nenhuma
 * tela guarda dados por conta propria.
 *
 * Por isso, quando voce adiciona uma transacao na tela 06, ela aparece na
 * lista da tela 05 e muda os totais da tela 04: todas olham para a mesma
 * fonte.
 *
 * LIMITACAO IMPORTANTE: isto vive na memoria. Ao fechar o app, tudo volta
 * aos dados de exemplo. Guardar de verdade (Room, DataStore ou um
 * servidor) e o proximo passo natural do projeto.
 *
 * mutableStateListOf avisa o Compose quando a lista muda -- e por isso que
 * as telas se redesenham sozinhas depois de um add ou um remove.
 */
object Repositorio {

    val transacoes = mutableStateListOf<Transacao>()
    val metas = mutableStateListOf<Meta>()
    val perfil = mutableStateOf(
        Perfil(
            nome = "Maria Oliveira",
            email = "maria@email.com",
            telefone = "(11) 98888-1234",
            rendaMensal = 3000.0,
        )
    )

    /** Tema escolhido na tela 09. null = seguir o sistema. */
    val temaEscuroForcado = mutableStateOf<Boolean?>(null)

    private var proximoId = 1L

    init {
        carregarDadosDeExemplo()
    }

    /* ---------------- leitura ---------------- */

    fun totalReceitas(): Double =
        transacoes.filter { it.tipo == TipoTransacao.RECEITA }.sumOf { it.valor }

    fun totalDespesas(): Double =
        transacoes.filter { it.tipo == TipoTransacao.DESPESA }.sumOf { it.valor }

    fun saldo(): Double = totalReceitas() - totalDespesas()

    /** Quanto foi gasto em cada categoria, da maior para a menor. */
    fun gastosPorCategoria(): List<Pair<Categoria, Double>> =
        transacoes
            .filter { it.tipo == TipoTransacao.DESPESA }
            .groupBy { it.categoria }
            .map { (categoria, lista) -> categoria to lista.sumOf { it.valor } }
            .sortedByDescending { it.second }

    fun buscarTransacao(id: Long): Transacao? = transacoes.firstOrNull { it.id == id }

    fun buscarMeta(id: Long): Meta? = metas.firstOrNull { it.id == id }

    /** Metas ainda em andamento -- as concluidas nao entram no resumo do topo. */
    fun metasEmAndamento(): List<Meta> = metas.filter { !it.concluida }

    /* ---------------- escrita ---------------- */

    fun adicionarTransacao(
        descricao: String,
        valor: Double,
        tipo: TipoTransacao,
        categoria: Categoria,
        dia: Int,
        mes: Int,
        ano: Int,
        observacao: String = "",
    ) {
        transacoes.add(
            0,
            Transacao(
                id = proximoId++,
                descricao = descricao,
                valor = valor,
                tipo = tipo,
                categoria = categoria,
                emoji = if (tipo == TipoTransacao.RECEITA) "💰" else categoria.emoji,
                dia = dia,
                mes = mes,
                ano = ano,
                hora = "",
                observacao = observacao,
                formaPagamento = if (tipo == TipoTransacao.RECEITA) "Depósito" else "Cartão",
            )
        )
    }

    fun removerTransacao(id: Long) {
        transacoes.removeAll { it.id == id }
    }

    fun adicionarMeta(
        nome: String,
        emoji: String,
        objetivo: Double,
        guardado: Double,
        prazoMes: Int,
        prazoAno: Int,
        reservaAutomatica: Boolean,
    ) {
        metas.add(
            Meta(
                id = proximoId++,
                nome = nome,
                emoji = emoji,
                objetivo = objetivo,
                guardado = guardado,
                prazoMes = prazoMes,
                prazoAno = prazoAno,
                reservaAutomatica = reservaAutomatica,
            )
        )
    }

    fun atualizarPerfil(novo: Perfil) {
        perfil.value = novo
    }

    /* ---------------- dados de exemplo ---------------- */

    /*
     * Sao os mesmos valores do prototipo. Uma observacao honesta: no
     * desenho, a lista de transacoes da tela 05 somava R$ 399,20, mas os
     * cards mostravam R$ 550,00 de despesa -- numeros de maquete nao
     * precisam fechar. Aqui eles fecham, entao acrescentei as transacoes
     * que faltavam (Uber, Curso, Cinema, Diversos) para que a soma de
     * verdade de os R$ 550,00 e as categorias do grafico batam com o
     * desenho.
     */
    private fun carregarDadosDeExemplo() {
        transacoes.clear()
        metas.clear()
        proximoId = 1L

        fun despesa(
            desc: String, valor: Double, cat: Categoria, emoji: String,
            dia: Int, hora: String, obs: String = "", pgto: String = "Cartão · final 4821",
        ) = Transacao(
            id = proximoId++, descricao = desc, valor = valor,
            tipo = TipoTransacao.DESPESA, categoria = cat, emoji = emoji,
            dia = dia, mes = 9, ano = 2026, hora = hora,
            observacao = obs, formaPagamento = pgto,
        )

        transacoes.addAll(
            listOf(
                despesa("Supermercado", 120.0, Categoria.ALIMENTACAO, "🛒", 19, "14:20", "Compra da semana"),
                despesa("Passagem", 9.20, Categoria.TRANSPORTE, "🚌", 19, "08:05"),
                despesa("Restaurante", 45.0, Categoria.ALIMENTACAO, "🍔", 18, "12:40"),
                despesa("Farmácia", 45.0, Categoria.SAUDE, "💊", 18, "19:10"),
                despesa("Uber", 80.80, Categoria.TRANSPORTE, "🚕", 17, "21:15"),
                despesa("Curso online", 30.0, Categoria.EDUCACAO, "🎓", 12, "10:00"),
                despesa("Cinema", 25.0, Categoria.LAZER, "🎬", 10, "20:30"),
                despesa("Diversos", 15.0, Categoria.OUTROS, "⋯", 8, "16:00"),
                Transacao(
                    id = proximoId++, descricao = "Salário", valor = 3000.0,
                    tipo = TipoTransacao.RECEITA, categoria = Categoria.RECEITA,
                    emoji = "💰", dia = 5, mes = 9, ano = 2026,
                    hora = "09:00", formaPagamento = "Depósito",
                ),
                despesa("Aluguel", 180.0, Categoria.MORADIA, "🏠", 5, "07:00", "", "Débito automático"),
            )
        )

        metas.addAll(
            listOf(
                Meta(proximoId++, "Curso", "🎓", 2000.0, 1200.0, 12, 2026),
                Meta(proximoId++, "Notebook", "💻", 5000.0, 2500.0, 3, 2027),
                Meta(proximoId++, "Viagem", "✈️", 1500.0, 1500.0, 6, 2026),
            )
        )
    }
}
