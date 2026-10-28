package com.crossbow.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.crossbow.translator.ui.navigation.CrossbowNavGraph
import com.crossbow.translator.ui.theme.CrossbowTranslatorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CrossbowApplication

        setContent {
            val themeMode by app.appPreferences.themeMode.collectAsState(initial = "SYSTEM")

            CrossbowTranslatorTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CrossbowNavGraph()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val app = application as? CrossbowApplication
        app?.textToSpeechHelper?.stop()
        app?.speechRecognizerHelper?.stopListening()
    }
}
