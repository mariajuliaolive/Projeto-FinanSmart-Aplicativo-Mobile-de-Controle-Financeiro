package com.example.finansmart.ui.navigation

/**
 * O endereço de cada tela do app.
 *
 * São textos simples que o Navigation usa para saber para onde ir.
 * Ficam reunidos aqui para não escrevermos "login" solto no meio do
 * código — um erro de digitação viraria um bug difícil de achar.
 */
object Rotas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"

    // As 5 abas do rodapé
    const val DASHBOARD = "dashboard"
    const val TRANSACOES = "transacoes"
    const val RELATORIOS = "relatorios"
    const val METAS = "metas"
    const val CONFIGURACOES = "configuracoes"

    // Telas abertas a partir das abas
    const val NOVA_TRANSACAO = "nova_transacao"
    const val NOVA_META = "nova_meta"
    const val PERFIL = "perfil"
    const val NOTIFICACOES = "notificacoes"
    const val ALTERAR_SENHA = "alterar_senha"
    const val PRIVACIDADE = "privacidade"
    const val SOBRE = "sobre"

    /**
     * O detalhe precisa saber QUAL transação mostrar, então a rota leva
     * o id junto: "detalhe_transacao/7". O {id} é o espaço reservado.
     */
    const val DETALHE_TRANSACAO = "detalhe_transacao/{id}"

    fun detalheTransacao(id: Long) = "detalhe_transacao/$id"
}
