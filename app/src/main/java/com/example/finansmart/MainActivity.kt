package com.example.finansmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finansmart.ui.navigation.FinanSmartNavHost
import com.example.finansmart.ui.theme.FinanSmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deixa o app desenhar atrás da barra de status e dos botões
        // do sistema — é o que permite o fundo escuro da Splash
        // ocupar a tela inteira.
        enableEdgeToEdge()

        setContent {
            FinanSmartTheme {
                FinanSmartNavHost()
            }
        }
    }
}