# Eden Controller 🎮

A two-part project to turn your Android phone into a Nintendo Switch Pro Controller (or generic gamepad) over UDP.

It consists of:
1. **Android App:** A Jetpack Compose app acting as the controller.
2. **Receiver Daemon:** A Python script (Linux/Windows) that receives UDP inputs and injects them into your OS as a native Virtual Gamepad (on Linux) or virtual keyboard (on Windows).

This was built primarily to play games on emulators (like Yuzu or Ryujinx) using a phone as a full-featured controller with analog sticks.

## Documentation

For detailed instructions and technical documentation, please refer to the `docs/` folder:

- [Android App Setup & Usage](docs/android_app.md)
- [Receiver Daemon Setup & Usage](docs/receiver_daemon.md)
- [UDP Protocol Specification](docs/udp_protocol.md)

## Quick Start (Linux)

1. **Install dependencies:**
   ```bash
   cd receiver
   pip install evdev
   ```

2. **Start the receiver:**
   ```bash
   python receiver.py --bind 0.0.0.0 --port 9876 --verbose
   ```
   *(Ensure you have `/dev/uinput` permissions configured. See the [Receiver docs](docs/receiver_daemon.md) for details).*

3. **Install and run the Android app:**
   Build the APK from the `/android` folder or run it via Android Studio. 
   Go to the app's settings (gear icon), enter your PC's IP address, and tap Save.

4. **Map in your Game/Emulator:**
   In your emulator's input configuration, change the Input Device to **Eden Virtual Gamepad**. Click on the button slots and press the corresponding buttons on your phone to map them.

## Quick Start (Windows)

Windows doesn't natively support virtual gamepads out-of-the-box like Linux, so the script will automatically fall back to injecting standard keyboard strokes (WASD/IJKL for joysticks, C/X/V/Z for face buttons).

1. **Install dependencies:**
   ```cmd
   cd receiver
   pip install keyboard
   ```

2. **Start the receiver as Administrator:**
   Open a terminal as **Administrator** (required for games to detect the injected keys) and run:
   ```cmd
   python receiver.py --bind 0.0.0.0 --port 9876 --verbose
   ```
   *(You can also pass `--deadzone 0.25` to adjust the sensitivity of the analog-to-digital joystick translation).*

3. **Install and run the Android app:**
   Enter your Windows PC's local IP address into the app's settings.

4. **Map in your Game/Emulator:**
   Leave your emulator's Input Device set to **Keyboard Only**. See the [Receiver docs](docs/receiver_daemon.md) for the full default key mapping layout.
