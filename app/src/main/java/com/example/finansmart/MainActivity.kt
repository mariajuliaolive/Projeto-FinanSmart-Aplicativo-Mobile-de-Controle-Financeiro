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


        enableEdgeToEdge()

        setContent {
            val escolhido = Repositorio.temaEscuroForcado.value

            FinanSmartTheme(darkTheme = escolhido ?: isSystemInDarkTheme()) {
                FinanSmartNavHost()
            }
        }
    }
}
