package com.example.finansmart.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*
 * PERFIL REPOSITORY
 *
 * Diferente de transacoes e metas (varias por pessoa, numa COLECAO),
 * o perfil e um so. Por isso ele mora no proprio DOCUMENTO do usuario:
 *
 *     users/{uid}            <- nome, telefone, rendaMensal
 *       transacoes/...
 *       metas/...
 *
 * O e-mail nao vai aqui: ele ja vem do Firebase Auth.
 */

/** O que o Firestore guarda do perfil. Campos null = ainda nao preenchidos. */
data class DadosDoPerfil(
    val nome: String?,
    val telefone: String?,
    val rendaMensal: Double?,
)

class PerfilRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun documento(uid: String) = firestore.collection("users").document(uid)

    /**
     * Observa o perfil em tempo real. Envia null enquanto o documento
     * nao existe (conta nova que ainda nao salvou o perfil).
     */
    fun observar(uid: String): Flow<DadosDoPerfil?> = callbackFlow {
        val ouvinte = documento(uid).addSnapshotListener { doc, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            if (doc == null || !doc.exists()) {
                trySend(null)
                return@addSnapshotListener
            }
            trySend(
                DadosDoPerfil(
                    nome = doc.getString("nome"),
                    telefone = doc.getString("telefone"),
                    rendaMensal = doc.getDouble("rendaMensal"),
                )
            )
        }
        awaitClose { ouvinte.remove() }
    }

    /*
     * SetOptions.merge(): atualiza so estes campos e preserva o resto.
     * Sem o merge, o set() substituiria o documento inteiro -- e se um
     * dia guardarmos outro campo aqui, ele seria apagado sem aviso.
     */
    fun salvar(uid: String, perfil: Perfil) {
        documento(uid).set(
            mapOf(
                "nome" to perfil.nome,
                "telefone" to perfil.telefone,
                "rendaMensal" to perfil.rendaMensal,
            ),
            SetOptions.merge()
        )
    }
}
