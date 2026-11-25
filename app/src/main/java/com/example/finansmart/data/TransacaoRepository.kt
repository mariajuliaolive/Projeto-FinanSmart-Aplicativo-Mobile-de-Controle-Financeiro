package com.example.finansmart.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/*
 * TRANSACAO REPOSITORY
 *
 * O unico arquivo que conversa com o Firestore sobre transacoes.
 * Cada pessoa tem a sua propria colecao:
 *
 *     users/{uid}/transacoes/{id}
 *
 * As regras de seguranca (parte 1) garantem que ninguem le a pasta
 * de outro uid -- este arquivo so precisa usar o uid certo.
 */
class TransacaoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun colecao(uid: String) =
        firestore.collection("users").document(uid).collection("transacoes")

    /*
     * Observa as transacoes EM TEMPO REAL.
     *
     * callbackFlow transforma o "ouvinte" do Firestore (que avisa toda vez
     * que algo muda) num Flow do Kotlin. Cada vez que uma transacao e
     * adicionada ou removida -- neste celular ou em outro -- uma lista
     * nova e enviada (trySend).
     *
     * awaitClose roda quando ninguem mais observa (ex.: logout) e remove
     * o ouvinte. Sem isso, ele continuaria ligado gastando internet.
     */
    fun observar(uid: String): Flow<List<Transacao>> = callbackFlow {
        val ouvinte: ListenerRegistration = colecao(uid)
            .orderBy("criadoEm", Query.Direction.DESCENDING)   // mais novas primeiro
            .addSnapshotListener { resultado, erro ->
                if (erro != null) {
                    close(erro)        // encerra o Flow avisando o erro
                    return@addSnapshotListener
                }
                val lista = resultado?.documents?.mapNotNull { it.paraTransacao() }
                trySend(lista.orEmpty())
            }

        awaitClose { ouvinte.remove() }
    }

    /*
     * Grava uma transacao. O id vira o nome do documento.
     *
     * Sem internet, o Firestore guarda no celular e envia sozinho quando
     * a conexao voltar -- e o ouvinte acima ja mostra a transacao na hora.
     * Por isso NAO esperamos (.await) aqui: a tela nao fica travada.
     */
    fun adicionar(uid: String, transacao: Transacao) {
        colecao(uid).document(transacao.id.toString()).set(transacao.paraMapa())
    }

    /**
     * Atualiza uma transacao que ja existe. Como nas metas, update() e
     * nao set(): assim o "criadoEm" original e preservado.
     */
    fun atualizar(uid: String, transacao: Transacao) {
        colecao(uid).document(transacao.id.toString()).update(
            mapOf(
                "descricao" to transacao.descricao,
                "valor" to transacao.valor,
                "tipo" to transacao.tipo.name,
                "categoria" to transacao.categoria.name,
                "emoji" to transacao.emoji,
                "dia" to transacao.dia,
                "mes" to transacao.mes,
                "ano" to transacao.ano,
                "observacao" to transacao.observacao,
                "formaPagamento" to transacao.formaPagamento,
            )
        )
    }

    fun remover(uid: String, id: Long) {
        colecao(uid).document(id.toString()).delete()
    }

    /** Usado so para criar os dados iniciais de uma conta nova (parte 3). */
    suspend fun estaVazio(uid: String): Boolean =
        colecao(uid).limit(1).get().await().isEmpty
}

/* ------------------------------------------------------------------
 * CONVERSAO  Transacao  <->  documento do Firestore
 *
 * O Firestore guarda tipos simples (texto, numero, booleano). Os enums
 * (tipo, categoria) vao como texto: "DESPESA", "ALIMENTACAO"...
 * ------------------------------------------------------------------ */

private fun Transacao.paraMapa(): Map<String, Any> = mapOf(
    "descricao" to descricao,
    "valor" to valor,
    "tipo" to tipo.name,
    "categoria" to categoria.name,
    "emoji" to emoji,
    "dia" to dia,
    "mes" to mes,
    "ano" to ano,
    "hora" to hora,
    "observacao" to observacao,
    "formaPagamento" to formaPagamento,
    "criadoEm" to System.currentTimeMillis(),
)

/*
 * Le um documento e monta a Transacao. Se algum campo estiver faltando
 * ou estragado, usa um valor padrao em vez de fechar o app.
 * Retorna null so se o id nao for um numero (documento que nao e nosso).
 */
private fun DocumentSnapshot.paraTransacao(): Transacao? {
    val id = id.toLongOrNull() ?: return null
    return Transacao(
        id = id,
        descricao = getString("descricao").orEmpty(),
        valor = getDouble("valor") ?: 0.0,
        tipo = runCatching { TipoTransacao.valueOf(getString("tipo").orEmpty()) }
            .getOrDefault(TipoTransacao.DESPESA),
        categoria = runCatching { Categoria.valueOf(getString("categoria").orEmpty()) }
            .getOrDefault(Categoria.OUTROS),
        emoji = getString("emoji").orEmpty(),
        dia = getLong("dia")?.toInt() ?: 1,
        mes = getLong("mes")?.toInt() ?: 1,
        ano = getLong("ano")?.toInt() ?: 2026,
        hora = getString("hora").orEmpty(),
        observacao = getString("observacao").orEmpty(),
        formaPagamento = getString("formaPagamento").orEmpty(),
    )
}
