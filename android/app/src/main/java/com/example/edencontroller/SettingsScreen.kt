package com.example.edencontroller

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    prefs: PreferencesManager,
    onClose: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val targetIp by prefs.targetIp.collectAsState(initial = "127.0.0.1")
    val targetPort by prefs.targetPort.collectAsState(initial = 9876)
    val haptics by prefs.hapticsEnabled.collectAsState(initial = true)
    val opacity by prefs.opacity.collectAsState(initial = 0.8f)

    var ipInput by remember { mutableStateOf(targetIp) }
    var portInput by remember { mutableStateOf(targetPort.toString()) }

    LaunchedEffect(targetIp) { ipInput = targetIp }
    LaunchedEffect(targetPort) { portInput = targetPort.toString() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(32.dp)) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row {
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("Target IP") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = portInput,
                    onValueChange = { portInput = it },
                    label = { Text("Target Port") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Haptics")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = haptics, onCheckedChange = { scope.launch { prefs.setHapticsEnabled(it) } })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Controls Opacity: ${(opacity * 100).toInt()}%")
            Slider(
                value = opacity,
                onValueChange = { scope.launch { prefs.setOpacity(it) } },
                valueRange = 0.1f..1.0f
            )
            
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Button(onClick = onClose) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    scope.launch {
                        prefs.setTargetIp(ipInput)
                        val port = portInput.toIntOrNull() ?: 9876
                        prefs.setTargetPort(port)
                        onSave(ipInput, port)
                        onClose()
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}
