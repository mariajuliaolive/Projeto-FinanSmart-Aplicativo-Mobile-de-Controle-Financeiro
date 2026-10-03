package com.example.finansmart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finansmart.ui.screens.login.LoginScreen
import com.example.finansmart.ui.screens.splash.SplashScreen

/**
 * O mapa do aplicativo: quais telas existem e o que cada
 * botão faz quando é tocado.
 */
@Composable
fun FinanSmartNavHost() {

    // Guarda em que tela estamos e sabe levar para outra.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rotas.SPLASH
    ) {

        composable(Rotas.SPLASH) {
            SplashScreen(
                onTerminou = {
                    navController.navigate(Rotas.LOGIN) {
                        // Tira a Splash da pilha: depois de sair dela, o
                        // botão Voltar do celular não deve trazê-la de volta.
                        popUpTo(Rotas.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Rotas.LOGIN) {
            LoginScreen(
                onEntrar = {
                    // Etapa 8: leva ao Dashboard, quando ele existir.
                },
                onCriarConta = {
                    // Etapa 7: leva ao Cadastro, a próxima tela que faremos.
                },
                onEsqueciSenha = {
                    // Não existe como tela no protótipo. Sem ação por ora.
                }
            )
        }
    }
}

