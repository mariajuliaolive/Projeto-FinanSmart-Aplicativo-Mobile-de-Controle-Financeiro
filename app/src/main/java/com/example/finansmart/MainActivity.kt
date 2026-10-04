package com.example.finansmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.finansmart.data.Repositorio
import com.example.finansmart.ui.navigation.FinanSmartNavHost
import com.example.finansmart.ui.theme.FinanSmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deixa o app desenhar atrás da barra de status e dos botões do
        // sistema -- é o que permite o fundo escuro da Splash e do topo
        // do detalhe da transação ocuparem a tela inteira.
        enableEdgeToEdge()

        setContent {
            // A tela 09 pode forçar claro ou escuro. Quando ninguém
            // escolheu (null), seguimos o modo do celular.
            val escolhido = Repositorio.temaEscuroForcado.value

            FinanSmartTheme(darkTheme = escolhido ?: isSystemInDarkTheme()) {
                FinanSmartNavHost()
            }
        }
    }
}
