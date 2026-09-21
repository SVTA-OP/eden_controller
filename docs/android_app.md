# Android App

The Android app is built using modern Android development practices with **Kotlin** and **Jetpack Compose**. It provides a fully functional controller layout with two analog sticks, a D-Pad, face buttons (A, B, X, Y), shoulder bumpers, triggers, and utility buttons (+, -).

## Building the App

The source code is located in the `/android` directory. It targets SDK 34.

To build a debug APK from the command line:
```bash
cd android
./gradlew assembleDebug
```
The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`. 
You can also open the `/android` folder in Android Studio and run it directly on your device.

## Usage

1. **Launch the App:** The app runs in landscape mode and hides the system UI for an immersive, distraction-free experience.
2. **Configure Connection:** Tap the **Settings** (gear) icon in the top center. 
   - **Target IP:** Enter the local IP address of the PC running the receiver (e.g., `192.168.1.11`).
   - **Target Port:** Usually `9876`.
   - Tap **Save**. The app will immediately begin streaming UDP packets to this address.
3. **Customize Aesthetics:** In the settings, you can also toggle haptic feedback and adjust the opacity of the controller interface.

## Internals
- **UdpSender.kt**: Manages a background coroutine that sends state packets at ~30Hz, and immediately flushes packets when button states change.
- **ControllerState**: A singleton object holding the current bitmask of buttons and stick axes (-127 to 127).
- **PreferencesManager**: Uses Android Jetpack DataStore to persistently save your IP and port settings.
