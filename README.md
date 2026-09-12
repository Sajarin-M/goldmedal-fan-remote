# Fan Remote

A small Android infrared remote built with Kotlin, Jetpack Compose and Material 3. Power, speeds 1–5, boost, fan LED and 2/4/8-hour timers. Dark-only, with no accounts, ads, analytics or internet permission.

Requires Android 8 or later and an IR blaster. The owner reports these controls working on their ceiling fan using an iQOO 12. Compatibility with other models is not established. Timer expiry over the full 2/4/8-hour durations has not been independently measured here.

Aim the phone’s IR emitter toward the fan and keep wall power on. Commands run off the UI thread; overlapping taps are ignored. Haptics acknowledge accepted taps and respect the phone’s touch-feedback setting. IR provides no acknowledgment, so the UI does not claim to know the fan’s state. Timers are commands sent to the fan, not countdowns maintained by the app; there is no timer-cancel key.

## Project structure

One `app` module: `ir/` owns commands, pulse encoding and hardware access; `ui/` owns the screen, theme and ViewModel. Tests protect the command values and actual transmitted frames. See [AGENTS.md](AGENTS.md) for contribution conventions.

The visible name is **Fan Remote**. The application ID and Kotlin namespace are `dev.sajarinm.fanremote`; the conventional source path is `dev/sajarinm/fanremote/`. Keep the application ID stable after publishing. There is no claim of affiliation with a fan manufacturer.

## Development

Install JDK 17 and Android SDK platform 36/build tools 35.0.0. Set `JAVA_HOME` and `ANDROID_HOME`, or set `sdk.dir=/your/android/sdk` in untracked `local.properties`. Kotlin and other build dependencies download automatically. Use the checked-in wrapper; no global Gradle or Kotlin install is needed.

| Task | Command |
| --- | --- |
| Format Kotlin and Gradle scripts | `./gradlew ktfmtFormat` |
| Check formatting | `./gradlew ktfmtCheck` |
| Compile Kotlin (like a type check) | `./gradlew :app:compileDebugKotlin` |
| Unit tests | `./gradlew :app:testDebugUnitTest` |
| Android lint | `./gradlew :app:lintDebug` |
| Development APK | `./gradlew :app:assembleDebug` |
| Build and install on connected phone | `./gradlew :app:installDebug` |
| Optimized unsigned APK | `./gradlew :app:assembleRelease` |
| Unsigned Play App Bundle | `./gradlew :app:bundleRelease` |
| Remove build outputs | `./gradlew clean` |

Typical verification:

```sh
./gradlew ktfmtCheck
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb shell am start -n dev.sajarinm.fanremote/.MainActivity
```

Install manually with `adb install -r app/build/outputs/apk/debug/app-debug.apk`. Kotlin source edits require rebuilding/installing; there is no Metro server. Android Studio provides Compose previews and development tooling. Debug and release outputs are under `app/build/outputs/`; reports are under `app/build/reports/`.

### Bounded Linux builds

Gradle uses one worker, a 512 MiB heap and 512 MiB metadata limit. Kotlin compiles in-process; the single-use daemon exits afterward. On this workstation, enforce the total process limit as well:

```sh
systemd-run --user --wait --pipe \
  -p MemoryMax=1536M -p MemorySwapMax=0 -p CPUQuota=150% \
  --setenv=JAVA_HOME="$JAVA_HOME" --working-directory="$PWD" \
  "$PWD/gradlew" :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Run formatting and release builds separately under the same limit. Diagnose failures before increasing memory. No emulator or parallel builds are needed.

## Protocol notes

`FanCommand` is the active command table. `IrProtocol.frames` is the only frame-generation entry point. Power and LED are toggles and send one frame; speeds, boost and timers send two. Carrier is 38 kHz; 32-bit values are sent most-significant bit first. Header is 9,000/4,500 µs, marks are 560 µs, and zero/one spaces are 560/1,690 µs. First/second trailers are 560/1,000 µs, with a requested 2 ms gap between calls. Preserve this tested behavior when refactoring.

Three inactive values are retained for future compatibility work, not exposed or verified: MOP `0x1B7E6897` (meaning unknown), breeze `0x1B7EE817`, and sleep `0x1B7EA857` (duration unconfirmed, 4h/5h). No investigation is needed merely to retain these values.

## GitHub and distribution

Generated files, local SDK paths and signing material are ignored. The Gradle wrapper JAR is intentionally tracked. The repository is [Sajarin-M/goldmedal-fan-remote](https://github.com/Sajarin-M/goldmedal-fan-remote), licensed under the [MIT License](LICENSE). No CI or Git hooks are configured. The hosted privacy policy is at https://sajarin-m.github.io/goldmedal-fan-remote/.

Debug APKs are signed with a local development key. Release builds here are **unsigned**. To distribute an APK, sign the optimized APK with a private release key. To publish on Play, create a signed release App Bundle using Android Studio’s **Generate Signed Bundle / APK**, or configure private signing locally. Keep signing credentials outside this repository and back them up securely. [Android signing guide](https://developer.android.com/studio/publish/app-signing)

Google Play publication is a separate step: establish the final supported-device claims, prepare screenshots/listing and Data safety declarations, provide a public privacy policy and an in-app way to access it, then upload the signed bundle to a test track before production. The in-app privacy-policy entry and hosted policy are not included yet. New personal developer accounts may need a closed test with 12 testers continuously opted in for 14 days before applying for production access. [Upload guide](https://developer.android.com/studio/publish/upload-bundle), [User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311), [testing requirements](https://support.google.com/googleplay/android-developer/answer/14151465)

Use manufacturer names only for substantiated compatibility descriptions; do not imply official affiliation or use their branding as this app’s identity. [Play impersonation policy](https://support.google.com/googleplay/android-developer/answer/9888374)
