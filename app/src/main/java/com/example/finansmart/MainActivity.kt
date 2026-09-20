package com.example.finansmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finansmart.ui.screens.splash.SplashScreen
import com.example.finansmart.ui.theme.FinanSmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deixa o app desenhar atras da barra de status e dos botoes
        // de navegacao -- e o que permite o fundo escuro da splash
        // ocupar a tela inteira.
        enableEdgeToEdge()

        setContent {
            FinanSmartTheme {
                SplashScreen(
                    onTerminou = {
                        // Etapa 5: aqui vai a navegacao para a tela de Login.
                        // Por enquanto a barra enche e a tela fica parada.
                    }
                )
            }
        }
    }
}
