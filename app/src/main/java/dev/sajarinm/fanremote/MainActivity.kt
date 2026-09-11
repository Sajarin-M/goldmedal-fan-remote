package dev.sajarinm.fanremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.sajarinm.fanremote.ir.IrTransmitter
import dev.sajarinm.fanremote.ui.FanRemoteTheme
import dev.sajarinm.fanremote.ui.RemoteScreen
import dev.sajarinm.fanremote.ui.RemoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val factory = viewModelFactory {
            initializer { RemoteViewModel(IrTransmitter(applicationContext)) }
        }
        setContent {
            val model: RemoteViewModel = viewModel(factory = factory)
            FanRemoteTheme {
                RemoteScreen(model.state.collectAsStateWithLifecycle().value, model::send)
            }
        }
    }
}
