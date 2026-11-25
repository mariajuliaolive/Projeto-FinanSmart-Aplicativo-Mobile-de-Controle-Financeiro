package com.example.finansmart.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finansmart.data.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
 * AUTH VIEWMODEL
 *
 * Fica entre as telas e o AuthRepository:
 *   Tela  --(clique)-->  ViewModel  --(pede)-->  AuthRepository --> Firebase
 *   Tela  <--(estado)--  ViewModel  <--(responde ou lanca erro)--
 *
 * A tela so faz duas coisas: chama funcoes daqui (entrar, criarConta...)
 * e desenha o que estiver em "estado". Ela nao sabe que o Firebase existe.
 *
 * O ViewModel sobrevive a rotacao do celular: se a pessoa girar a tela
 * no meio do login, o carregamento continua e o resultado nao se perde.
 */

/** Os dados do usuario logado que as telas precisam (sem expor o Firebase). */
data class UsuarioLogado(
    val uid: String,
    val nome: String,
    val email: String,
    val emailVerificado: Boolean,
)

/** Tudo o que as telas de autenticacao precisam saber para se desenhar. */
data class AuthUiState(
    val carregando: Boolean = false,       // mostra o "Entrando..." e trava o botao
    val usuario: UsuarioLogado? = null,    // != null quando alguem esta logado
    val erro: String? = null,              // mensagem vermelha, ja em portugues
    val mensagem: String? = null,          // aviso positivo (ex.: e-mail enviado)
)

class AuthViewModel(
    private val repositorio: AuthRepository = AuthRepository()
) : ViewModel() {

    // _estado: so o ViewModel altera.  estado: as telas so leem.
    private val _estado = MutableStateFlow(
        AuthUiState(usuario = repositorio.usuarioAtual?.paraUsuarioLogado())
    )
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        // Se ja tem alguem logado, busca os dados atualizados (nome,
        // e-mail verificado) sem travar a abertura do app.
        if (_estado.value.usuario != null) {
            viewModelScope.launch {
                val atualizado = runCatching { repositorio.recarregar() }.getOrNull()
                if (atualizado != null) {
                    _estado.update { it.copy(usuario = atualizado.paraUsuarioLogado()) }
                }
            }
        }
    }

    fun entrar(email: String, senha: String) = executar {
        val usuario = repositorio.entrar(email, senha)
        _estado.update { it.copy(usuario = usuario.paraUsuarioLogado()) }
    }

    fun criarConta(nome: String, email: String, senha: String) = executar {
        val usuario = repositorio.criarConta(nome, email, senha)
        // O nome acabou de ser gravado; usamos o digitado para nao depender
        // de o Firebase ja ter atualizado o perfil localmente.
        _estado.update {
            it.copy(usuario = usuario.paraUsuarioLogado().copy(nome = nome.trim()))
        }
    }

    fun recuperarSenha(email: String) {
        if (email.isBlank()) {
            _estado.update { it.copy(erro = "Digite seu e-mail para recuperar a senha.") }
            return
        }
        executar {
            repositorio.recuperarSenha(email)
            _estado.update {
                it.copy(mensagem = "Enviamos um link para $email. Confira também o spam.")
            }
        }
    }

    fun sair() {
        repositorio.sair()
        _estado.value = AuthUiState()   // volta ao estado inicial: ninguem logado
    }

    /** A tela chama depois de mostrar o erro/aviso, para ele nao reaparecer. */
    fun limparMensagens() {
        _estado.update { it.copy(erro = null, mensagem = null) }
    }

    /*
     * O "molde" usado por entrar, criarConta e recuperarSenha:
     * liga o carregando, roda a acao, e se der erro traduz a mensagem.
     * Assim nao repetimos o mesmo try/catch tres vezes.
     */
    private fun executar(acao: suspend () -> Unit) {
        if (_estado.value.carregando) return   // evita clique duplo

        viewModelScope.launch {
            _estado.update { it.copy(carregando = true, erro = null, mensagem = null) }
            try {
                acao()
            } catch (e: CancellationException) {
                throw e   // a tela foi fechada: deixa a coroutine parar normalmente
            } catch (e: Exception) {
                _estado.update { it.copy(erro = traduzirErro(e)) }
            } finally {
                _estado.update { it.copy(carregando = false) }
            }
        }
    }

    /*
     * Transforma a excecao do Firebase numa frase para o usuario.
     * ATENCAO a ordem: FirebaseAuthWeakPasswordException e um tipo de
     * FirebaseAuthInvalidCredentialsException, entao precisa vir antes.
     */
    private fun traduzirErro(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException ->
            "Senha fraca: use pelo menos 6 caracteres."
        is FirebaseAuthUserCollisionException ->
            "Já existe uma conta com este e-mail."
        is FirebaseAuthInvalidUserException ->
            "Não encontramos uma conta com este e-mail."
        is FirebaseAuthInvalidCredentialsException ->
            if (e.errorCode == "ERROR_INVALID_EMAIL") "Este e-mail não é válido."
            else "E-mail ou senha incorretos."
        is FirebaseNetworkException ->
            "Sem conexão com a internet. Verifique e tente de novo."
        is FirebaseTooManyRequestsException ->
            "Muitas tentativas seguidas. Aguarde um pouco e tente de novo."
        else ->
            "Algo deu errado. Tente novamente."
    }
}

/** Converte o usuario do Firebase no nosso modelo simples. */
private fun FirebaseUser.paraUsuarioLogado() = UsuarioLogado(
    uid = uid,
    nome = displayName?.takeIf { it.isNotBlank() } ?: "Usuário",
    email = email.orEmpty(),
    emailVerificado = isEmailVerified,
)
