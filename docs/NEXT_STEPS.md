# NEXT_STEPS.md — AEGIS HUD Launcher

**Last Updated:** 2026-10-25
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

---

## Files to Create in Phase 1

```
AR_HUD_Luncher/
├── local.properties                           (sdk.dir=/home/ragnar/Android/Sdk)
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

- [ ] `./gradlew assembleDebug` succeeds with no errors.
- [ ] APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.
- [ ] `./gradlew testDebugUnitTest` passes.
- [ ] `./gradlew lintDebug` produces no errors.
- [ ] `AndroidManifest.xml` contains `HOME` and `DEFAULT` intent categories.
- [ ] `AndroidManifest.xml` contains `<queries>` element for app discovery.
- [ ] `MainActivity.kt` uses `setContent` and renders `HomeScreen`.
- [ ] `HomeScreen.kt` displays a minimal placeholder with "AEGIS" text.
- [ ] Back press on the home screen does not finish the Activity (via `BackHandler`).

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

```bash
cd /home/ragnar/AndroidStudioProjects/AR_HUD_Luncher

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

---

## Outstanding Blockers

None at this time.

---

## Session Handoff (Phase 0 → Phase 1)

**Completed in Phase 0:**
- All planning and documentation created.
- Environment inspected and documented.
- Architecture, feature plan, UI/UX spec, test plan, and risk register established.

**Next action after reviewing the plan:**
- Authorize Phase 1 implementation.
- Create the Android project scaffold.
- Run `./gradlew assembleDebug` and resolve any build errors.
- Update `PROGRESS_TRACKER.md` with results.
