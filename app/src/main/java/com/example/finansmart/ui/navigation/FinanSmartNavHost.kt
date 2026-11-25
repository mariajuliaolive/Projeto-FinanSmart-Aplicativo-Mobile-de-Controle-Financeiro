package com.example.finansmart.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finansmart.data.Repositorio
import com.example.finansmart.ui.auth.AuthViewModel
import com.example.finansmart.ui.screens.cadastro.CadastroScreen
import com.example.finansmart.ui.screens.config.ConfiguracoesScreen
import com.example.finansmart.ui.screens.config.AlterarSenhaScreen
import com.example.finansmart.ui.screens.config.NotificacoesScreen
import com.example.finansmart.ui.screens.config.PerfilScreen
import com.example.finansmart.ui.screens.config.PrivacidadeScreen
import com.example.finansmart.ui.screens.config.SobreScreen
import com.example.finansmart.ui.screens.dashboard.DashboardScreen
import com.example.finansmart.ui.screens.login.LoginScreen
import com.example.finansmart.ui.screens.metas.MetasScreen
import com.example.finansmart.ui.screens.metas.NovaMetaScreen
import com.example.finansmart.ui.screens.relatorios.RelatoriosScreen
import com.example.finansmart.ui.screens.splash.SplashScreen
import com.example.finansmart.ui.screens.transacoes.DetalheTransacaoScreen
import com.example.finansmart.ui.screens.transacoes.NovaTransacaoScreen
import com.example.finansmart.ui.screens.transacoes.TransacoesScreen
import com.example.finansmart.ui.theme.FinanSmartTheme

/**
 * O mapa do aplicativo: quais telas existem e o que cada botão faz.
 *
 * As telas não sabem para onde levam. Elas avisam ("clicaram em Entrar")
 * e é aqui que se decide o destino. Por isso a navegação inteira cabe
 * num arquivo só, e as telas continuam independentes.
 */
@Composable
fun FinanSmartNavHost() {
    val navController = rememberNavController()

    // UM ViewModel de autenticacao para o app inteiro: Login, Cadastro e
    // Configuracoes enxergam o mesmo estado (quem esta logado, erros...).
    val authViewModel: AuthViewModel = viewModel()
    val authEstado by authViewModel.estado.collectAsStateWithLifecycle()

    // Sempre que o usuario logado muda (login, cadastro, app reaberto),
    // o perfil passa a mostrar o nome e o e-mail reais. Dashboard,
    // Configuracoes e Perfil leem do Repositorio, entao atualizam sozinhos.
    LaunchedEffect(authEstado.usuario) {
        val usuario = authEstado.usuario
        if (usuario != null) {
            Repositorio.usarContaLogada(
                nome = usuario.nome,
                email = usuario.email,
                verificada = usuario.emailVerificado,
            )
            Repositorio.conectar(usuario.uid)    // passa a usar as transacoes do Firestore
        } else {
            Repositorio.desconectar()            // saiu: para de ouvir e limpa
        }
    }

    // Qual rota está na tela agora -- usado para acender a aba certa.
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val mostrarAbas = rotaAtual in ROTAS_COM_ABAS

    Scaffold(
        containerColor = FinanSmartTheme.colors.bg,
        bottomBar = {
            if (mostrarAbas) {
                BarraNavegacaoInferior(
                    rotaAtual = rotaAtual,
                    aoSelecionar = { rota -> irParaAba(navController, rota) }
                )
            }
        }
    ) { espacoDasAbas ->

        NavHost(
            navController = navController,
            startDestination = Rotas.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .background(FinanSmartTheme.colors.bg)
        ) {

            /* ---------- entrada ---------- */

            composable(Rotas.SPLASH) {
                SplashScreen(
                    onTerminou = {
                        navController.navigate(Rotas.LOGIN) {
                            // Tira a Splash da pilha: o Voltar não deve traze-la de novo.
                            popUpTo(Rotas.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rotas.LOGIN) {
                // Mesmo "vigia" do Cadastro: login deu certo -> Dashboard.
                // Bonus: se o Firebase ja lembra de alguem logado, o usuario
                // ja existe ao abrir esta tela e o app pula direto para dentro.
                LaunchedEffect(authEstado.usuario) {
                    if (authEstado.usuario != null) entrarNoApp(navController)
                }

                LoginScreen(
                    onEntrar = { email, senha -> authViewModel.entrar(email, senha) },
                    onCriarConta = {
                        authViewModel.limparMensagens()
                        navController.navigate(Rotas.CADASTRO)
                    },
                    onEsqueciSenha = { email -> authViewModel.recuperarSenha(email) },
                    carregando = authEstado.carregando,
                    erro = authEstado.erro,
                    mensagem = authEstado.mensagem,
                )
            }

            composable(Rotas.CADASTRO) {
                // Quando a conta e criada, o estado ganha um "usuario".
                // Este bloco percebe a mudanca e leva ao Dashboard.
                LaunchedEffect(authEstado.usuario) {
                    if (authEstado.usuario != null) entrarNoApp(navController)
                }

                CadastroScreen(
                    onVoltar = {
                        authViewModel.limparMensagens()
                        navController.popBackStack()
                    },
                    onCriarConta = { nome, email, senha ->
                        authViewModel.criarConta(nome, email, senha)
                    },
                    carregando = authEstado.carregando,
                    erro = authEstado.erro,
                )
            }

            /* ---------- as 5 abas ---------- */

            composable(Rotas.DASHBOARD) {
                DashboardScreen(
                    onNovaTransacao = { navController.navigate(Rotas.NOVA_TRANSACAO) },
                    onNotificacoes = { navController.navigate(Rotas.NOTIFICACOES) },
                    onVerTodas = { irParaAba(navController, Rotas.TRANSACOES) },
                    onAbrirTransacao = { id ->
                        navController.navigate(Rotas.detalheTransacao(id))
                    },
                    modifier = Modifier.padding(espacoDasAbas).statusBarsPadding()
                )
            }

            composable(Rotas.TRANSACOES) {
                TransacoesScreen(
                    onNovaTransacao = { navController.navigate(Rotas.NOVA_TRANSACAO) },
                    onAbrirTransacao = { id ->
                        navController.navigate(Rotas.detalheTransacao(id))
                    },
                    modifier = Modifier.padding(espacoDasAbas).statusBarsPadding()
                )
            }

            composable(Rotas.RELATORIOS) {
                RelatoriosScreen(
                    modifier = Modifier.padding(espacoDasAbas).statusBarsPadding()
                )
            }

            composable(Rotas.METAS) {
                MetasScreen(
                    onNovaMeta = { navController.navigate(Rotas.NOVA_META) },
                    modifier = Modifier.padding(espacoDasAbas).statusBarsPadding()
                )
            }

            composable(Rotas.CONFIGURACOES) {
                ConfiguracoesScreen(
                    onAbrirPerfil = { navController.navigate(Rotas.PERFIL) },
                    onAlterarSenha = { navController.navigate(Rotas.ALTERAR_SENHA) },
                    onPrivacidade = { navController.navigate(Rotas.PRIVACIDADE) },
                    onSobre = { navController.navigate(Rotas.SOBRE) },
                    onSair = {
                        authViewModel.sair()   // desloga do Firebase de verdade
                        navController.navigate(Rotas.LOGIN) {
                            // Limpa tudo: sair da conta não deve deixar
                            // nenhuma tela do app logado na pilha.
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(espacoDasAbas).statusBarsPadding()
                )
            }

            /* ---------- telas abertas a partir das abas ---------- */

            composable(Rotas.NOVA_TRANSACAO) {
                NovaTransacaoScreen(
                    onVoltar = { navController.popBackStack() },
                    onSalvou = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.NOVA_META) {
                NovaMetaScreen(
                    onVoltar = { navController.popBackStack() },
                    onCriou = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.NOTIFICACOES) {
                NotificacoesScreen(
                    onVoltar = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.ALTERAR_SENHA) {
                AlterarSenhaScreen(
                    onVoltar = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.PRIVACIDADE) {
                PrivacidadeScreen(
                    onVoltar = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.SOBRE) {
                SobreScreen(
                    onVoltar = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(Rotas.PERFIL) {
                PerfilScreen(
                    onVoltar = { navController.popBackStack() },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            composable(
                route = Rotas.DETALHE_TRANSACAO,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                // O id veio dentro do endereço: "detalhe_transacao/7".
                val id = entrada.arguments?.getLong("id") ?: 0L
                DetalheTransacaoScreen(
                    transacaoId = id,
                    onVoltar = { navController.popBackStack() },
                    onExcluiu = { navController.popBackStack() }
                )
            }
        }
    }
}

/** As telas que mostram a barra de 5 abas no rodapé. */
private val ROTAS_COM_ABAS = setOf(
    Rotas.DASHBOARD,
    Rotas.TRANSACOES,
    Rotas.RELATORIOS,
    Rotas.METAS,
    Rotas.CONFIGURACOES,
)

/**
 * Troca de aba sem empilhar.
 *
 * Sem isso, ir Início -> Metas -> Início -> Metas deixaria quatro telas
 * na pilha, e o Voltar teria que desfazer uma a uma. Com launchSingleTop
 * e popUpTo no Dashboard, a pilha fica sempre curta e o Voltar leva ao
 * Início, como a pessoa espera.
 */
private fun irParaAba(navController: NavHostController, rota: String) {
    navController.navigate(rota) {
        popUpTo(Rotas.DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Entrar pelo Login ou pelo Cadastro leva ao Dashboard, sem volta. */
private fun entrarNoApp(navController: NavHostController) {
    navController.navigate(Rotas.DASHBOARD) {
        popUpTo(0) { inclusive = true }
    }
}
