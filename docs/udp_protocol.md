# UDP Protocol Specification

The Eden Controller uses a lightweight, custom, stateless UDP protocol to transmit inputs with minimal latency. 

Packets are exactly **12 bytes** long and are encoded in Little-Endian byte order.

## Packet Structure

| Offset | Type | Description |
|--------|------|-------------|
| 0 | `Byte` | Magic byte, always `0xC7` (199). Used to ignore garbage traffic. |
| 1 | `Byte` | Protocol version, always `0x01`. |
| 2-3 | `UInt16` | Sequence number. Increments per packet. Used by the receiver to ignore out-of-order/duplicate packets. |
| 4-7 | `UInt32` | Button bitmask. See mappings below. |
| 8 | `Int8` | Left stick X-axis (`-127` to `127`). |
| 9 | `Int8` | Left stick Y-axis (`-127` to `127`). |
| 10 | `Int8` | Right stick X-axis (`-127` to `127`). |
| 11 | `Int8` | Right stick Y-axis (`-127` to `127`). |

*Note: For the Y-axes, negative values represent UP, and positive values represent DOWN. For X-axes, negative is LEFT, positive is RIGHT.*

## Button Bitmask

Buttons are packed into a 32-bit integer. If a bit is `1`, the button is pressed.

```
0: A
1: B
2: X
3: Y
4: L
5: R
6: ZL
7: ZR
8: MINUS
9: PLUS
10: L3 (Left Stick Click)
11: R3 (Right Stick Click)
12: DPAD UP
13: DPAD DOWN
14: DPAD LEFT
15: DPAD RIGHT
16: WALK (Custom modifier)
```

## Transmission Strategy

- **Heartbeat:** The Android app continuously broadcasts its state at ~30Hz (every 33ms) to ensure UDP reliability and prevent the receiver from entering a timeout state.
- **Immediate Push:** When any button's state changes, or when the stick moves significantly, a packet is dispatched immediately, bypassing the 30Hz heartbeat interval to achieve sub-millisecond input responsiveness.
- **Timeout:** If the receiver does not receive a valid packet for 500ms, it automatically releases all pressed buttons and zeroes all axes to prevent stuck inputs in the game.
