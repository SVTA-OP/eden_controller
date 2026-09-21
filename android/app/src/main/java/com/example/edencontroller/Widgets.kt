package com.example.edencontroller

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun ControllerButton(
    text: String,
    buttonMask: Int,
    sender: UdpSender,
    hapticsEnabled: Boolean,
    modifier: Modifier = Modifier,
    isToggle: Boolean = false
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(if (isPressed) Color.DarkGray else Color.LightGray.copy(alpha = 0.3f), CircleShape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    
                    if (hapticsEnabled && !isPressed) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }

                    if (isToggle) {
                        isPressed = !isPressed
                        if (ControllerState.updateButton(buttonMask, isPressed)) {
                            sender.triggerImmediateUpdate()
                        }
                        waitForUpOrCancellation()
                    } else {
                        isPressed = true
                        if (ControllerState.updateButton(buttonMask, true)) {
                            sender.triggerImmediateUpdate()
                        }
                        waitForUpOrCancellation()
                        isPressed = false
                        if (ControllerState.updateButton(buttonMask, false)) {
                            sender.triggerImmediateUpdate()
                        }
                    }
                }
            }
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FloatingJoystick(
    isLeftStick: Boolean,
    sender: UdpSender,
    modifier: Modifier = Modifier
) {
    var center by remember { mutableStateOf<Offset?>(null) }
    var currentPosition by remember { mutableStateOf<Offset?>(null) }
    val maxRadius = 150f

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    center = down.position
                    currentPosition = down.position
                    
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.pressed) {
                            currentPosition = change.position
                            val dx = change.position.x - center!!.x
                            val dy = change.position.y - center!!.y
                            val distance = hypot(dx, dy)
                            
                            val clampedDistance = min(distance, maxRadius)
                            val ratio = if (distance > 0) clampedDistance / distance else 0f
                            val clampedX = dx * ratio
                            val clampedY = dy * ratio
                            
                            val mappedX = ((clampedX / maxRadius) * 127).toInt().toByte()
                            val mappedY = ((clampedY / maxRadius) * 127).toInt().toByte()
                            
                            var changed = false
                            if (isLeftStick) {
                                if (ControllerState.leftX != mappedX || ControllerState.leftY != mappedY) {
                                    ControllerState.leftX = mappedX
                                    ControllerState.leftY = mappedY
                                    changed = true
                                }
                            } else {
                                if (ControllerState.rightX != mappedX || ControllerState.rightY != mappedY) {
                                    ControllerState.rightX = mappedX
                                    ControllerState.rightY = mappedY
                                    changed = true
                                }
                            }
                            if (changed) sender.triggerImmediateUpdate()
                        }
                    } while (change != null && change.pressed)
                    
                    center = null
                    currentPosition = null
                    if (isLeftStick) {
                        ControllerState.leftX = 0
                        ControllerState.leftY = 0
                    } else {
                        ControllerState.rightX = 0
                        ControllerState.rightY = 0
                    }
                    sender.triggerImmediateUpdate()
                }
            }
    ) {
        center?.let { c ->
            drawCircle(Color.White.copy(alpha = 0.2f), radius = maxRadius, center = c)
            
            currentPosition?.let { p ->
                val dx = p.x - c.x
                val dy = p.y - c.y
                val distance = hypot(dx, dy)
                
                val clampedDistance = min(distance, maxRadius)
                val ratio = if (distance > 0) clampedDistance / distance else 0f
                val thumbX = c.x + dx * ratio
                val thumbY = c.y + dy * ratio
                
                drawCircle(Color.White.copy(alpha = 0.5f), radius = 50f, center = Offset(thumbX, thumbY))
            }
        }
    }
}
