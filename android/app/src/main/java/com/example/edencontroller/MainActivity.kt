package com.example.edencontroller

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    private lateinit var udpSender: UdpSender
    private lateinit var prefs: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fullscreen immersive & keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        prefs = PreferencesManager(applicationContext)
        udpSender = UdpSender()

        setContent {
            var showSettings by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                val ip = prefs.targetIp.first()
                val port = prefs.targetPort.first()
                udpSender.targetIp = ip
                udpSender.targetPort = port
                udpSender.start()
            }

            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSettings) {
                        SettingsScreen(
                            prefs = prefs,
                            onClose = { showSettings = false },
                            onSave = { ip, port -> 
                                udpSender.targetIp = ip
                                udpSender.targetPort = port
                            }
                        )
                    } else {
                        ControllerScreen(
                            sender = udpSender,
                            prefs = prefs,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        udpSender.stop()
    }
}
