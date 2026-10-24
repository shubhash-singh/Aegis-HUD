# Validation Evidence

## Fresh Validation

Command run from the repository root:

```text
./gradlew assembleDebug testDebugUnitTest lintDebug --console=plain
```

Observed result:

```text
BUILD SUCCESSFUL in 24s
51 actionable tasks: 4 executed, 47 up-to-date
Configuration cache entry reused.
```

The command executed the required `assembleDebug`, `testDebugUnitTest`, and `lintDebug` tasks. The debug APK exists at `app/build/outputs/apk/debug/app-debug.apk`.

The JVM report contains two tests with zero failures and zero errors. Lint reports **0 errors and 34 warnings**. Most warnings are intentionally deferred dependency-version notices; the actionable icon warnings are captured as P1-04.

## Device Availability

Command:

```text
adb devices -l
```

Observed result: ADB started successfully but reported no attached devices. Physical-device tests were not run.

## Acceptance Matrix

| Criterion | Result | Evidence |
|---|---|---|
| Required ignore rules | Pass | `.gitignore` ignores `local.properties`, `build/`, `.gradle/`, `.idea/`, `*.keystore`, and `*.jks`. |
| Verified Gradle 8.9 wrapper | Pass | `gradle-wrapper.properties` pins Gradle 8.9 and the documented `distributionSha256Sum`. |
| Debug assembly and APK | Pass | Fresh Gradle task succeeds; debug APK exists at the planned path. |
| JVM unit tests | Pass | Fresh Gradle task succeeds; 2 tests, 0 failures, 0 errors. |
| Lint | Pass with warnings | Fresh `lintDebug` succeeds with 0 errors and 34 warnings. |
| `HOME` and `DEFAULT` categories | Pass | Source and merged manifest contain both categories. |
| Launcher package-visibility query | Pass | Source and merged manifest contain the `MAIN` plus `LAUNCHER` query. |
| Compose activity bootstrap | Pass | `MainActivity` calls `enableEdgeToEdge()` and `setContent { AegisTheme { HomeScreen() } }`. |
| Dark AEGIS placeholder | Pass | `AegisTheme` provides the dark background and `HomeScreen` renders `AEGIS`. |
| Back handling | Pass | `HomeScreen` declares a no-op `BackHandler`. |
| Launcher task flags | Pass | Manifest sets `singleTask`, `stateNotNeeded=true`, and `excludeFromRecents=true`. |
| DT-MANIFEST-01 | Pending | No Android device is attached. |
| DT-MANIFEST-02 | Pending | No Android device is attached. |
| Synchronized phase records | Fail | Authoritative phase documents still describe the pre-implementation state. |
| Version-controlled implementation | Fail | All Phase 1 project files are untracked at review time. |
