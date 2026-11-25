package com.example.finansmart.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*
 * META REPOSITORY
 *
 * Mesmo molde do TransacaoRepository, agora para as metas:
 *
 *     users/{uid}/metas/{id}
 *
 * Compare os dois arquivos lado a lado: so mudam o nome da colecao
 * e os campos. E esse o padrao -- depois de entender um, voce entende todos.
 */
class MetaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun colecao(uid: String) =
        firestore.collection("users").document(uid).collection("metas")

    /** Metas em tempo real, na ordem em que foram criadas. */
    fun observar(uid: String): Flow<List<Meta>> = callbackFlow {
        val ouvinte = colecao(uid)
            .orderBy("criadoEm", Query.Direction.ASCENDING)
            .addSnapshotListener { resultado, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                trySend(resultado?.documents?.mapNotNull { it.paraMeta() }.orEmpty())
            }
        awaitClose { ouvinte.remove() }
    }

    /** Grava a meta (funciona offline; o ouvinte mostra na hora). */
    fun adicionar(uid: String, meta: Meta) {
        colecao(uid).document(meta.id.toString()).set(meta.paraMapa())
    }
}

/* ---------------- conversao Meta <-> documento ---------------- */

private fun Meta.paraMapa(): Map<String, Any> = mapOf(
    "nome" to nome,
    "emoji" to emoji,
    "objetivo" to objetivo,
    "guardado" to guardado,
    "prazoMes" to prazoMes,
    "prazoAno" to prazoAno,
    "reservaAutomatica" to reservaAutomatica,
    "criadoEm" to System.currentTimeMillis(),
)

private fun DocumentSnapshot.paraMeta(): Meta? {
    val id = id.toLongOrNull() ?: return null
    return Meta(
        id = id,
        nome = getString("nome").orEmpty(),
        emoji = getString("emoji") ?: "🎯",
        objetivo = getDouble("objetivo") ?: 0.0,
        guardado = getDouble("guardado") ?: 0.0,
        prazoMes = getLong("prazoMes")?.toInt() ?: 12,
        prazoAno = getLong("prazoAno")?.toInt() ?: 2026,
        reservaAutomatica = getBoolean("reservaAutomatica") ?: false,
    )
}
