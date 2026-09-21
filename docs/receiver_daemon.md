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

On Windows, `evdev` is not available for virtual gamepads. Instead, the script falls back to injecting standard keyboard keys using the `keyboard` module. It maps controller buttons to keyboard letters, and translates analog stick movements into digital key presses (WASD and IJKL).

### Setup and Running on Windows
1. Open your terminal/command prompt as **Administrator**. This is strictly required on Windows so that the injected keystrokes can be detected by emulators and modern games.
2. Install the required module: `pip install keyboard`
3. Run the script:
   ```cmd
   python receiver.py --bind 0.0.0.0 --port 9876 --verbose --deadzone 0.25
   ```
   - `--deadzone`: Controls the sensitivity of the analog sticks when translating to digital WASD/IJKL key presses (default `0.25`). Increase this if your phone's joystick is triggering keys too early.
   
### Windows Key Mappings
When using the Windows fallback, the emulator's **Input Device** should be set to "Keyboard Only". The script injects the following default keys:
- **Face Buttons:** `C`, `X`, `V`, `Z` (for A, B, X, Y)
- **Left Stick:** `W`, `A`, `S`, `D`
- **Right Stick:** `I`, `J`, `K`, `L`
- **D-Pad:** Arrow keys
- **Triggers/Bumpers:** `Q`, `E`, `R`, `T`
- **Minus/Plus:** `N`, `M`

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
