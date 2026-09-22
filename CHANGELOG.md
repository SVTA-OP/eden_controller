# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased] - Main Branch

### Added
- **Multiplayer Support:** The receiver script now supports up to 4 simultaneous phones connecting via UDP. On Linux, it dynamically generates separate virtual gamepads (`Eden Virtual Gamepad 1`, `2`, etc.) for true multi-controller multiplayer.
- **Windows Support & Documentation:** Added fallback logic for Windows via the `keyboard` module, converting joystick inputs into digital keystrokes (WASD/IJKL) and face buttons into C/X/V/Z. Added a quick start section for Windows in the main README and detailed docs in `docs/receiver_daemon.md`.

### Changed
- **Instant Joystick Response:** Re-engineered the `FloatingJoystick` in the Android app. It now calculates and transmits the offset position the instant a finger touches down, eliminating the need to drag before a position is registered.
- **Classic Nintendo Button Mapping:** Reverted the UI layout back to the classic Nintendo style (X top, Y left, B bottom, A right). Updated the receiver script to correctly map these actions logically for emulators (A/X map to jump, B/Y map to action/throw).

### Fixed
- **Windows Joystick Deadzones:** Fixed an issue where the Windows fallback keyboard injector failed to register joystick centering correctly, preventing the WASD/IJKL keys from being released.
- **Android Build:** Fixed Android build script errors by ensuring `local.properties` contains the correct `sdk.dir` environment variable.
