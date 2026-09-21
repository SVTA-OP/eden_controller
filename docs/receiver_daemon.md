# Receiver Daemon

The receiver is a Python script (`receiver.py`) that listens for UDP packets from the Android app and injects them into the host operating system.

## Linux (Virtual Gamepad)

On Linux, the receiver uses the `evdev` library to create a native **Eden Virtual Gamepad**. This acts exactly like a real physical controller plugged into your computer. It features:
- Standard face buttons (A, B, X, Y, mapped internally as South, East, North, West)
- Standard D-Pad
- Triggers, Bumpers, and Stick clicks
- True analog ABS axes for the left and right joysticks (values -127 to 127).

This is ideal for emulators like Yuzu/Ryujinx/Steam, as they will natively recognize the analog sticks and buttons.

### Setup and Permissions
1. Install evdev: `pip install evdev`
2. Ensure you have access to `/dev/uinput`. To run without `sudo`:
   ```bash
   sudo modprobe uinput
   sudo usermod -aG input $USER
   echo 'KERNEL=="uinput", GROUP="input", MODE="0660", OPTIONS+="static_node=uinput"' | sudo tee /etc/udev/rules.d/99-uinput.rules
   sudo udevadm control --reload-rules && sudo udevadm trigger
   ```
   *(Log out and back in for group changes to take effect).*

## Windows (Keyboard Fallback)

On Windows, `evdev` is not available. The script falls back to injecting standard keyboard keys using the `keyboard` module. It maps controller buttons and analog stick extremes to digital keyboard keys (e.g., Right stick maps to I, J, K, L).

1. Install keyboard: `pip install keyboard`
2. Run the script as Administrator, otherwise modern games will ignore the injected keyboard events.

## Running

Run the daemon in your terminal:
```bash
python receiver.py --bind 0.0.0.0 --port 9876 --verbose
```
- `--verbose`: Prints state changes to the terminal (useful for verifying connection).
- `--bind`: IP address to listen on (`0.0.0.0` listens on all network interfaces).

## Troubleshooting
- **No logs appear when pressing buttons:** Check your PC's firewall. You may need to run `sudo ufw allow 9876/udp` on Linux to allow incoming UDP traffic from your phone.
- **Game doesn't respond (Linux):** Go to your emulator's input settings and change the Input Device from "Keyboard Only" to **Eden Virtual Gamepad**. Then, click the mapping slots and press the corresponding buttons on your phone to map them.
