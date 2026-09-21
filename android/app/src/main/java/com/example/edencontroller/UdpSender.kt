package com.example.edencontroller

import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ControllerState {
    var buttonMask: Int = 0
    var leftX: Byte = 0
    var leftY: Byte = 0
    var rightX: Byte = 0
    var rightY: Byte = 0

    const val BTN_A = 1 shl 0
    const val BTN_B = 1 shl 1
    const val BTN_X = 1 shl 2
    const val BTN_Y = 1 shl 3
    const val BTN_L = 1 shl 4
    const val BTN_R = 1 shl 5
    const val BTN_ZL = 1 shl 6
    const val BTN_ZR = 1 shl 7
    const val BTN_MINUS = 1 shl 8
    const val BTN_PLUS = 1 shl 9
    const val BTN_L3 = 1 shl 10
    const val BTN_R3 = 1 shl 11
    const val BTN_DPAD_UP = 1 shl 12
    const val BTN_DPAD_DOWN = 1 shl 13
    const val BTN_DPAD_LEFT = 1 shl 14
    const val BTN_DPAD_RIGHT = 1 shl 15
    const val BTN_WALK = 1 shl 16
    
    fun updateButton(mask: Int, pressed: Boolean): Boolean {
        val oldMask = buttonMask
        if (pressed) {
            buttonMask = buttonMask or mask
        } else {
            buttonMask = buttonMask and mask.inv()
        }
        return oldMask != buttonMask
    }
}

class UdpSender(var targetIp: String = "127.0.0.1", var targetPort: Int = 9876) {
    private var socket: DatagramSocket? = null
    private var sequenceNumber: Short = 0
    private var job: Job? = null

    fun start() {
        if (socket != null) return
        socket = DatagramSocket()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                sendPacket()
                delay(33) // ~30Hz heartbeat
            }
        }
    }

    fun triggerImmediateUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            sendPacket()
        }
    }

    private fun sendPacket() {
        val sock = socket ?: return
        val buffer = ByteBuffer.allocate(12)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(0xC7.toByte())
        buffer.put(1.toByte())
        
        synchronized(this) {
            buffer.putShort(sequenceNumber++)
        }
        
        buffer.putInt(ControllerState.buttonMask)
        buffer.put(ControllerState.leftX)
        buffer.put(ControllerState.leftY)
        buffer.put(ControllerState.rightX)
        buffer.put(ControllerState.rightY)

        val bytes = buffer.array()
        try {
            val address = InetAddress.getByName(targetIp)
            val packet = DatagramPacket(bytes, bytes.size, address, targetPort)
            sock.send(packet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        job?.cancel()
        socket?.close()
        socket = null
    }
}
