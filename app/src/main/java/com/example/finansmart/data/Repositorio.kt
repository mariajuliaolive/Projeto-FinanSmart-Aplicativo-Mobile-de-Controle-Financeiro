package com.example.finansmart.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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

    /* ---------------- ligacao com o Firestore ---------------- */

    private val transacaoRepo = TransacaoRepository()
    private val metaRepo = MetaRepository()
    private val perfilRepo = PerfilRepository()
    private val authRepo = AuthRepository()

    // Escopo das coroutines do Repositorio. Dispatchers.Main: a lista e
    // atualizada na thread da tela, que e onde o Compose espera.
    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var uidConectado: String? = null
    private var observacaoDasTransacoes: Job? = null
    private var observacaoDasMetas: Job? = null
    private var observacaoDoPerfil: Job? = null

    // Nome salvo no "Meu perfil" (Firestore). Quando existe, ele vence
    // o nome do login -- mesmo que o login seja atualizado depois.
    private var nomeDoFirestore: String? = null

    /**
     * Chamado quando alguem entra (login, cadastro ou app reaberto).
     * Troca os dados de exemplo pelas transacoes desta pessoa no
     * Firestore, e passa a receber cada mudanca em tempo real.
     */
    fun conectar(uid: String) {
        if (uid == uidConectado) return          // ja esta ligado nessa conta
        desconectar()
        uidConectado = uid

        // Tira o telefone e a renda de exemplo: ate o Firestore responder
        // (ou se a conta for nova), eles ficam vazios em vez de mostrar
        // os dados da "Maria" de exemplo.
        perfil.value = perfil.value.copy(telefone = "", rendaMensal = 0.0)

        observacaoDasTransacoes = escopo.launch {
            transacaoRepo.observar(uid)
                .catch { erro -> Log.e("Repositorio", "Erro ao ler transacoes", erro) }
                .collect { listaDoFirestore ->
                    // Troca o conteudo da lista: as telas se redesenham sozinhas.
                    transacoes.clear()
                    transacoes.addAll(listaDoFirestore)
                }
        }

        // Mesma coisa para as metas, numa segunda "escuta" independente.
        observacaoDasMetas = escopo.launch {
            metaRepo.observar(uid)
                .catch { erro -> Log.e("Repositorio", "Erro ao ler metas", erro) }
                .collect { listaDoFirestore ->
                    metas.clear()
                    metas.addAll(listaDoFirestore)
                }
        }

        // E o perfil: o que estiver salvo no Firestore vence o de exemplo.
        // Se o nome foi editado no "Meu perfil", ele tambem vence o do cadastro.
        observacaoDoPerfil = escopo.launch {
            perfilRepo.observar(uid)
                .catch { erro -> Log.e("Repositorio", "Erro ao ler perfil", erro) }
                .collect { dados ->
                    if (dados != null) {
                        nomeDoFirestore = dados.nome?.takeIf { it.isNotBlank() }
                        val atual = perfil.value
                        perfil.value = atual.copy(
                            nome = dados.nome?.takeIf { it.isNotBlank() } ?: atual.nome,
                            telefone = dados.telefone ?: atual.telefone,
                            rendaMensal = dados.rendaMensal ?: atual.rendaMensal,
                        )
                    }
                }
        }
    }

    /** Chamado ao sair: para de ouvir o Firestore e limpa a tela. */
    fun desconectar() {
        observacaoDasTransacoes?.cancel()        // dispara o awaitClose -> remove o ouvinte
        observacaoDasTransacoes = null
        observacaoDasMetas?.cancel()
        observacaoDasMetas = null
        observacaoDoPerfil?.cancel()
        observacaoDoPerfil = null
        uidConectado = null
        nomeDoFirestore = null
        transacoes.clear()
        metas.clear()
    }

    /*
     * Ids unicos a partir do relogio (milissegundos). O maxOf garante que
     * duas transacoes criadas no mesmo milissegundo nao repitam o id.
     */
    private var ultimoId = 0L
    private fun novoId(): Long {
        ultimoId = maxOf(System.currentTimeMillis(), ultimoId + 1)
        return ultimoId
    }

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
        emoji: String? = null,
    ) {
        val nova = Transacao(
            id = novoId(),
            descricao = descricao,
            valor = valor,
            tipo = tipo,
            categoria = categoria,
            emoji = emoji ?: if (tipo == TipoTransacao.RECEITA) "💰" else categoria.emoji,
            dia = dia,
            mes = mes,
            ano = ano,
            hora = "",
            observacao = observacao,
            formaPagamento = if (tipo == TipoTransacao.RECEITA) "Depósito" else "Cartão",
        )

        val uid = uidConectado
        if (uid != null) {
            // Logado: grava no Firestore. NAO adicionamos na lista aqui --
            // o ouvinte do Firestore avisa na hora e a lista se atualiza
            // sozinha (se adicionassemos tambem, ela apareceria duplicada).
            transacaoRepo.adicionar(uid, nova)
        } else {
            transacoes.add(0, nova)              // sem login: so na memoria
        }
    }

    fun removerTransacao(id: Long) {
        val uid = uidConectado
        if (uid != null) {
            transacaoRepo.remover(uid, id)       // o ouvinte tira da lista
        } else {
            transacoes.removeAll { it.id == id }
        }
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
        val nova = Meta(
            id = novoId(),
            nome = nome,
            emoji = emoji,
            objetivo = objetivo,
            guardado = guardado,
            prazoMes = prazoMes,
            prazoAno = prazoAno,
            reservaAutomatica = reservaAutomatica,
        )

        val uid = uidConectado
        if (uid != null) {
            metaRepo.adicionar(uid, nova)        // o ouvinte coloca na lista
        } else {
            metas.add(nova)                      // sem login: so na memoria
        }
    }

    fun atualizarPerfil(novo: Perfil) {
        perfil.value = novo                       // aparece na hora
        nomeDoFirestore = novo.nome.takeIf { it.isNotBlank() }
        uidConectado?.let { perfilRepo.salvar(it, novo) }   // e fica salvo
        // Tambem no Firebase Auth: assim, ao reabrir o app, o nome do login
        // ja e o novo e nao "pisca" o antigo antes do Firestore responder.
        authRepo.atualizarNome(novo.nome)
    }

    /**
     * Chamado quando alguem faz login ou cria conta: troca o nome e o
     * e-mail de exemplo pelos da conta do Firebase. Telefone e renda
     * continuam os de exemplo -- o Firebase Auth nao guarda esses dados
     * (eles irao para o Firestore mais adiante).
     */
    fun usarContaLogada(nome: String, email: String, verificada: Boolean) {
        perfil.value = perfil.value.copy(
            nome = nomeDoFirestore ?: nome,
            email = email,
            verificada = verificada,
        )
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
