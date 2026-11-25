package com.example.finansmart.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

/*
 * AUTH REPOSITORY
 *
 * O unico arquivo do app que conversa com o Firebase Authentication.
 * As telas nunca chamam o Firebase direto: elas pedem ao ViewModel,
 * e o ViewModel pede aqui. Assim, se um dia trocarmos o Firebase por
 * outra coisa, so este arquivo muda.
 *
 * As funcoes "suspend" esperam a resposta do Firebase (que vem pela
 * internet) sem travar a tela. Se algo der errado (senha errada,
 * e-mail ja usado, sem internet), elas lancam uma excecao -- quem
 * trata e traduz o erro para o usuario e o ViewModel (parte 2).
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /** Quem esta logado agora, ou null se ninguem. O Firebase lembra
     *  o login mesmo depois de fechar o app. */
    val usuarioAtual: FirebaseUser?
        get() = auth.currentUser

    /** Login com e-mail e senha de uma conta que ja existe. */
    suspend fun entrar(email: String, senha: String): FirebaseUser {
        val resultado = auth
            .signInWithEmailAndPassword(email.trim(), senha)
            .await()   // <- espera o Firebase responder
        return resultado.user
            ?: error("O Firebase não retornou o usuário.")
    }

    /** Cria a conta e já grava o nome do usuário no perfil do Firebase. */
    suspend fun criarConta(nome: String, email: String, senha: String): FirebaseUser {
        val resultado = auth
            .createUserWithEmailAndPassword(email.trim(), senha)
            .await()
        val usuario = resultado.user
            ?: error("O Firebase não retornou o usuário.")

        // O Firebase Auth guarda só e-mail e senha; o nome vai no "perfil".
        val perfil = UserProfileChangeRequest.Builder()
            .setDisplayName(nome.trim())
            .build()
        usuario.updateProfile(perfil).await()

        return usuario
    }

    /** Envia o e-mail de "esqueci minha senha". */
    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    /** Desloga. Não precisa esperar: é instantâneo e local. */
    fun sair() {
        auth.signOut()
    }
}
