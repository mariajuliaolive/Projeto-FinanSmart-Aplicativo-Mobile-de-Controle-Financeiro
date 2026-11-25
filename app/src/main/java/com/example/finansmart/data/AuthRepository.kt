package com.example.finansmart.data

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.finansmart.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

        // Manda o e-mail "confirme seu endereco". Nao esperamos: se falhar,
        // a conta continua criada normalmente.
        usuario.sendEmailVerification()

        return usuario
    }

    /**
     * Pede ao Firebase os dados mais novos do usuario logado. O currentUser
     * fica guardado no celular -- sem isto, quem confirmou o e-mail ontem
     * continuaria aparecendo como "nao verificado".
     */
    suspend fun recarregar(): FirebaseUser? {
        auth.currentUser?.reload()?.await()
        return auth.currentUser
    }

    /** Grava o nome no perfil do Firebase Auth (o mesmo do cadastro). */
    fun atualizarNome(nome: String) {
        val perfil = UserProfileChangeRequest.Builder()
            .setDisplayName(nome.trim())
            .build()
        auth.currentUser?.updateProfile(perfil)
    }

    /*
     * LOGIN COM GOOGLE, em 3 etapas:
     *
     *   1. Credential Manager abre a janela "Escolha uma conta" do Android.
     *   2. A conta escolhida devolve um ID TOKEN (um "comprovante" assinado
     *      pelo Google dizendo "esta pessoa e fulana@gmail.com").
     *   3. Entregamos o token ao Firebase, que cria a conta (na primeira
     *      vez) ou entra nela -- e a partir dai tudo funciona igual ao
     *      login por e-mail: mesmo uid, mesmo Firestore.
     *
     * Precisa de uma Activity porque a janela de contas e desenhada
     * por cima da tela do app.
     */
    suspend fun entrarComGoogle(activity: Activity): FirebaseUser {
        // Etapa 1: o que pedir ao Credential Manager.
        val opcaoGoogle = GetGoogleIdOption.Builder()
            // false = mostra TODAS as contas Google do celular, nao so as
            // que ja entraram no app antes (senao, uma conta nova ficaria de fora).
            .setFilterByAuthorizedAccounts(false)
            // O ID do cliente WEB (client_type 3 do google-services.json).
            // O plugin google-services cria esta string sozinho.
            .setServerClientId(activity.getString(R.string.default_web_client_id))
            // false = sempre deixa a pessoa escolher, nunca entra sozinho.
            .setAutoSelectEnabled(false)
            .build()

        val pedido = GetCredentialRequest.Builder()
            .addCredentialOption(opcaoGoogle)
            .build()

        // Abre a janela e espera a pessoa escolher (ou cancelar).
        val resposta = CredentialManager.create(activity)
            .getCredential(context = activity, request = pedido)

        // Etapa 2: confere se veio mesmo uma credencial do Google.
        val credencial = resposta.credential
        if (credencial !is CustomCredential ||
            credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            error("A credencial recebida não é do Google.")
        }
        val tokenDoGoogle = GoogleIdTokenCredential.createFrom(credencial.data).idToken

        // Etapa 3: troca o token do Google por uma sessao do Firebase.
        val credencialFirebase = GoogleAuthProvider.getCredential(tokenDoGoogle, null)
        return auth.signInWithCredential(credencialFirebase).await().user
            ?: error("O Firebase não retornou o usuário.")
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
