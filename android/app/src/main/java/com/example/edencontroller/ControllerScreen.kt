package com.example.edencontroller

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ControllerScreen(
    sender: UdpSender,
    prefs: PreferencesManager,
    onOpenSettings: () -> Unit
) {
    val haptics by prefs.hapticsEnabled.collectAsState(initial = true)
    val opacity by prefs.opacity.collectAsState(initial = 0.8f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(opacity)
    ) {
        // Top Buttons (Shoulders and Triggers)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                ControllerButton("ZL", ControllerState.BTN_ZL, sender, haptics, Modifier.size(80.dp, 40.dp))
                Spacer(Modifier.width(8.dp))
                ControllerButton("L", ControllerState.BTN_L, sender, haptics, Modifier.size(80.dp, 40.dp))
            }
            
            Row {
                ControllerButton("-", ControllerState.BTN_MINUS, sender, haptics, Modifier.size(50.dp, 30.dp))
                Spacer(Modifier.width(16.dp))
                ControllerButton("+", ControllerState.BTN_PLUS, sender, haptics, Modifier.size(50.dp, 30.dp))
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            Row {
                ControllerButton("R", ControllerState.BTN_R, sender, haptics, Modifier.size(80.dp, 40.dp))
                Spacer(Modifier.width(8.dp))
                ControllerButton("ZR", ControllerState.BTN_ZR, sender, haptics, Modifier.size(80.dp, 40.dp))
            }
        }

        // Left controls (Stick top, D-Pad bottom)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp, top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ControllerButton("L3", ControllerState.BTN_L3, sender, haptics, Modifier.size(40.dp))
                Spacer(Modifier.width(8.dp))
                FloatingJoystick(true, sender, Modifier.size(150.dp))
            }
            Spacer(Modifier.height(16.dp))
            DPad(sender, haptics)
            Spacer(Modifier.height(8.dp))
            ControllerButton("WALK", ControllerState.BTN_WALK, sender, haptics, Modifier.size(80.dp, 40.dp), isToggle = true)
        }

        // Right controls (ABXY top, Stick bottom)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp, top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButtons(sender, haptics)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FloatingJoystick(false, sender, Modifier.size(150.dp))
                Spacer(Modifier.width(8.dp))
                ControllerButton("R3", ControllerState.BTN_R3, sender, haptics, Modifier.size(40.dp))
            }
        }
    }
}

@Composable
fun DPad(sender: UdpSender, haptics: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(120.dp), contentAlignment = Alignment.Center) {
        ControllerButton("▲", ControllerState.BTN_DPAD_UP, sender, haptics, Modifier.size(40.dp).align(Alignment.TopCenter))
        ControllerButton("▼", ControllerState.BTN_DPAD_DOWN, sender, haptics, Modifier.size(40.dp).align(Alignment.BottomCenter))
        ControllerButton("◀", ControllerState.BTN_DPAD_LEFT, sender, haptics, Modifier.size(40.dp).align(Alignment.CenterStart))
        ControllerButton("▶", ControllerState.BTN_DPAD_RIGHT, sender, haptics, Modifier.size(40.dp).align(Alignment.CenterEnd))
    }
}

@Composable
fun ActionButtons(sender: UdpSender, haptics: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(150.dp), contentAlignment = Alignment.Center) {
        ControllerButton("X", ControllerState.BTN_X, sender, haptics, Modifier.size(50.dp).align(Alignment.TopCenter))
        ControllerButton("B", ControllerState.BTN_B, sender, haptics, Modifier.size(50.dp).align(Alignment.BottomCenter))
        ControllerButton("Y", ControllerState.BTN_Y, sender, haptics, Modifier.size(50.dp).align(Alignment.CenterStart))
        ControllerButton("A", ControllerState.BTN_A, sender, haptics, Modifier.size(50.dp).align(Alignment.CenterEnd))
    }
}
