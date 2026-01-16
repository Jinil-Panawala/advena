## Supported Platforms / Devices
Advena is supported on standard Android devices and emulators.

### Tested Configuration
- Device: Medium Phone (IntelliJ/Android Studio default)
- OS Version: Android 16.0 (“Baklava”)
- Architecture: arm64
- Environment: Android Emulator (via IntelliJ IDEA / Android Studio)

This configuration was used for testing during development.

## Installation Instructions (Android)

### 1. Set Up the Emulator (Medium Phone)
1. Open Android Studio or IntelliJ IDEA.
2. Go to Tools → Device Manager.
3. Create a new virtual device:
    - Device: Medium Phone
    - System Image: Android 16.0 ("Baklava")
    - Architecture: arm64
4. Launch the emulator.

### 2. Install the APK

#### Option A — Drag & Drop (recommended)
1. Locate the file `Releases/Advena-v1.0.0.apk` in the repository.
2. Start the Medium Phone emulator (Android 16.0).
3. Drag the APK file directly onto the emulator window.
4. The emulator will install it automatically.

#### Option B — ADB Command
Run this from the project root:
`adb install Releases/Advena-v1.0.0.apk`

## Launching the App
1. Open the emulator home screen.
2. Locate the **Advena** app icon.
3. Tap to launch.

The login screen should appear immediately.

## Additional Notes
- The app runs fully locally; no backend server setup is required.
- If installation fails:
    - Ensure your emulator is Android 16.0 (Baklava) or API 29+.
    - Check emulator storage is sufficient.
- Grant location permissions inside the emulator if required.



