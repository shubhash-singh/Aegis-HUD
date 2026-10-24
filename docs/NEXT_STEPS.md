# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-10-25 (Planning review corrections applied)
**Current Phase:** PHASE-00 complete
**Next Phase:** PHASE-01 — Android Launcher Foundation

---

## Immediate Next Task

### Task: Create the Android Project Scaffold

**Phase:** PHASE-01 — F-001-P1, F-001-P2

**Objective:**
Create a complete, buildable Android project with Jetpack Compose enabled and the application registered as a valid Android home-screen launcher. The project must build successfully with `./gradlew assembleDebug`.

---

## Prerequisites

Before starting Phase 1:

- [x] PHASE-00 complete: all planning documents exist
- [x] Android SDK API 35 available at `~/Android/Sdk/`
- [x] JDK 21 available
- [x] adb available
- [x] Planning review corrections applied; see `reviews/README.md`
- [x] Gradle 8.9 wrapper bootstrap method defined in DEC-012
- [ ] .gitignore does not yet exist — **must be created first** (B-001)

---

## Gradle Wrapper Bootstrap

The repository has no `gradlew` and the machine has no system `gradle`. After creating the root Gradle settings and build files, bootstrap the wrapper from the official Gradle 8.9 binary distribution:

1. Download `https://services.gradle.org/distributions/gradle-8.9-bin.zip` outside the repository.
2. Verify SHA-256 `d725d707bfabd4dfdc958c624003b3c80accc03f7037b5122c4b1d0ef15cecab` against `https://gradle.org/release-checksums/`.
3. Unpack the verified archive outside the repository.
4. From the repository root, run the unpacked `bin/gradle :wrapper --gradle-version 8.9 --distribution-type bin`.
5. Confirm `gradle/wrapper/gradle-wrapper.properties` pins the same distribution URL and `distributionSha256Sum`.

Do not commit the downloaded archive or unpacked bootstrap distribution. See DEC-012, RISK-005, and PHASE-01-FOUNDATION.md.

---

## Files to Create in Phase 1

```
AR_HUD_Luncher/
├── .gitignore                                (FIRST — must exclude local.properties, build/, .gradle/, .idea/)
├── local.properties                           (sdk.dir=<your Android SDK path> — NOT committed)
├── settings.gradle.kts
├── build.gradle.kts                           (root, plugins only)
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
└── app/
    ├── build.gradle.kts                       (Compose, launcher dependencies)
    ├── src/
    │   ├── main/
    │   │   ├── AndroidManifest.xml            (launcher intent-filter, queries)
    │   │   ├── kotlin/dev/ragnar/aegis/
    │   │   │   ├── MainActivity.kt
    │   │   │   └── ui/
    │   │   │       ├── theme/
    │   │   │       │   ├── AegisTheme.kt      (stub HUD theme)
    │   │   │       │   ├── Color.kt
    │   │   │       │   └── Typography.kt
    │   │   │       └── home/
    │   │   │           └── HomeScreen.kt      (minimal placeholder)
    │   │   └── res/
    │   │       ├── values/
    │   │       │   └── strings.xml
    │   │       └── mipmap-*/                  (launcher icons — stub)
    │   ├── test/
    │   │   └── kotlin/dev/ragnar/aegis/
    │   │       └── ExampleUnitTest.kt
    │   └── androidTest/
    │       └── kotlin/dev/ragnar/aegis/
    │           └── ExampleInstrumentedTest.kt
```

---

## Acceptance Criteria for Phase 1

- [ ] `.gitignore` exists and excludes: `local.properties`, `build/`, `.gradle/`, `.idea/`, `*.keystore`, `*.jks`.
- [ ] `./gradlew assembleDebug` succeeds with no errors.
- [ ] APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.
- [ ] `./gradlew testDebugUnitTest` passes.
- [ ] `./gradlew lintDebug` produces no errors.
- [ ] The generated wrapper pins Gradle 8.9 and `distributionSha256Sum=d725d707bfabd4dfdc958c624003b3c80accc03f7037b5122c4b1d0ef15cecab`.
- [ ] `AndroidManifest.xml` contains `HOME` and `DEFAULT` intent categories.
- [ ] `AndroidManifest.xml` contains `<queries>` element for app discovery.
- [ ] `MainActivity.kt` uses `setContent` and renders `HomeScreen`.
- [ ] `HomeScreen.kt` displays a minimal placeholder with "AEGIS" text.
- [ ] Back press on the home screen does not finish the Activity (via `BackHandler`).
- [ ] AEGIS does not appear in the Recents screen (DT-MANIFEST-02).
- [ ] Repeated Home presses do not stack multiple instances (DT-MANIFEST-01).

---

## Key Technical Notes for Phase 1

### AndroidManifest.xml launcher registration

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:stateNotNeeded="true"
    android:excludeFromRecents="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Package visibility queries

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

### Edge-to-edge in MainActivity

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
        AegisTheme {
            HomeScreen()
        }
    }
}
```

### Back press handling in HomeScreen

```kotlin
BackHandler { /* no-op: do not exit the launcher */ }
```

---

## Validation Commands for Phase 1

> Run all commands from the repository root.

```bash
# Build
./gradlew assembleDebug

# Unit tests
./gradlew testDebugUnitTest

# Lint
./gradlew lintDebug

# Install (requires connected device or emulator)
./gradlew installDebug
# or
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Files to Inspect When Resuming

1. `AGENTS.md` — session instructions
2. `docs/PROGRESS_TRACKER.md` — overall status
3. `docs/NEXT_STEPS.md` — this file
4. `docs/phases/PHASE-01-FOUNDATION.md` — detailed Phase 1 plan
5. `docs/TECH_STACK.md` — dependency versions
6. `reviews/04-phase-1-readiness.md` — review disposition and readiness gates

---

## Outstanding Blockers

**B-001 — `.gitignore` missing**

No `.gitignore` file exists in the repository. `local.properties` (which contains the machine-specific SDK path) and IDE/build directories would be accidentally committed without it. `.gitignore` must be the very first file created in Phase 1, before any other file is staged or committed.

Required entries: `local.properties`, `build/`, `.gradle/`, `.idea/`, `*.keystore`, `*.jks`.

---

## Session Handoff (Phase 0 → Phase 1)

**Completed in Phase 0:**
- All planning and documentation created.
- Environment inspected and documented.
- Architecture, feature plan, UI/UX spec, test plan, and risk register established.
- Correction task applied (see PHASE_LOG.md).

**Next action after authorization:**
1. Create `.gitignore` (B-001) — before any other file is committed.
2. Decide whether to track, relocate, or remove the existing untracked `diff.txt` artifact.
3. Bootstrap the verified Gradle 8.9 wrapper according to DEC-012.
4. Create the Android project scaffold (Gradle, manifest, MainActivity, stubs).
5. Run `./gradlew assembleDebug` from the repository root and resolve any build errors.
6. Run the required physical-device manifest tests when a device is available; otherwise record `Implementation Complete — Validation Pending`.
7. Update `PROGRESS_TRACKER.md` with observed results.
