# PHASE-01-FOUNDATION.md — AEGIS HUD Launcher

**Phase ID:** PHASE-01
**Title:** Minimal Working Android Launcher
**Status:** Not started — awaiting authorization
**Dependency:** PHASE-00 complete

---

## Objective

Create a complete, buildable Android Gradle project configured as a valid Android home-screen launcher with Jetpack Compose enabled. The application must build successfully, render a minimal HUD placeholder on screen, and be selectable as the default Android home app.

---

## Scope

- Android Gradle project scaffold (Kotlin DSL, TOML version catalog)
- `AndroidManifest.xml` with launcher registration and `<queries>` element
- `MainActivity.kt` with `enableEdgeToEdge()` and `setContent`
- Minimal `AegisTheme` stub (correct colors from spec, basic typography)
- `HomeScreen.kt` placeholder composable ("AEGIS" label, black background)
- `BackHandler` to prevent launcher from finishing on back press
- `local.properties` with correct SDK path
- Gradle wrapper configuration
- Stub unit test and instrumented test
- Build verification: `./gradlew assembleDebug`
- Test verification: `./gradlew testDebugUnitTest`
- Lint verification: `./gradlew lintDebug`

## Non-Goals

- No real telemetry in this phase.
- No application discovery in this phase.
- No animation in this phase.
- No navigation in this phase (single screen only).
- No DataStore / settings in this phase.

---

## Dependencies

- PHASE-00: All planning documents must exist.
- Android SDK API 35 available.
- JDK 17+ (JDK 21 available on this machine).

---

## Subtasks

| # | Task | Status |
|---|---|---|
| 1 | Create `.gitignore` (local.properties, build/, .gradle/, .idea/, *.keystore, *.jks) | Not started |
| 2 | Create `settings.gradle.kts` | Not started |
| 3 | Create root `build.gradle.kts` | Not started |
| 4 | Create `gradle/libs.versions.toml` | Not started |
| 5 | Create `gradle.properties` | Not started |
| 6 | Create `local.properties` (sdk.dir=<your Android SDK path> — not committed) | Not started |
| 7 | Create Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) | Not started |
| 8 | Create `app/build.gradle.kts` with Compose config | Not started |
| 9 | Create `AndroidManifest.xml` with launcher + queries | Not started |
| 10 | Create `MainActivity.kt` | Not started |
| 11 | Create `AegisTheme.kt`, `Color.kt`, `Typography.kt` stubs | Not started |
| 12 | Create `HomeScreen.kt` minimal placeholder | Not started |
| 13 | Create `strings.xml` | Not started |
| 14 | Create stub `ExampleUnitTest.kt` | Not started |
| 15 | Create stub `ExampleInstrumentedTest.kt` | Not started |
| 16 | Run `./gradlew assembleDebug` | Not started |
| 17 | Fix build errors | Not started |
| 18 | Run `./gradlew testDebugUnitTest` | Not started |
| 19 | Run `./gradlew lintDebug` | Not started |
| 20 | Verify .gitignore excludes local.properties before any commit | Not started |
| 21 | Update PROGRESS_TRACKER.md | Not started |
| 22 | Update PHASE_LOG.md | Not started |
| 23 | Update NEXT_STEPS.md | Not started |

---

## Files Expected to Change

| File | Action |
|---|---|
| `.gitignore` | Create (before any code; must exclude local.properties) |
| `settings.gradle.kts` | Create |
| `build.gradle.kts` | Create |
| `gradle/libs.versions.toml` | Create |
| `gradle.properties` | Create |
| `local.properties` | Create (not committed; machine-specific SDK path) |
| `gradlew` | Create |
| `gradlew.bat` | Create |
| `gradle/wrapper/gradle-wrapper.jar` | Create |
| `gradle/wrapper/gradle-wrapper.properties` | Create |
| `app/build.gradle.kts` | Create |
| `app/src/main/AndroidManifest.xml` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/MainActivity.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/AegisTheme.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Color.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Typography.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt` | Create |
| `app/src/main/res/values/strings.xml` | Create |
| `app/src/main/res/mipmap-*/ic_launcher*.xml` | Create (adaptive icon stub) |
| `app/src/test/kotlin/.../ExampleUnitTest.kt` | Create |
| `app/src/androidTest/kotlin/.../ExampleInstrumentedTest.kt` | Create |
| `docs/PROGRESS_TRACKER.md` | Update |
| `docs/PHASE_LOG.md` | Update |
| `docs/NEXT_STEPS.md` | Update |

---

## Acceptance Criteria

- [ ] `.gitignore` excludes `local.properties`, `build/`, `.gradle/`, `.idea/`, `*.keystore`, `*.jks`.
- [ ] `./gradlew assembleDebug` exits with code 0.
- [ ] APK exists at `app/build/outputs/apk/debug/app-debug.apk`.
- [ ] `./gradlew testDebugUnitTest` exits with code 0.
- [ ] `./gradlew lintDebug` exits with code 0 (no errors; warnings acceptable).
- [ ] `AndroidManifest.xml` declares `HOME` and `DEFAULT` intent categories.
- [ ] `AndroidManifest.xml` contains `<queries>` for launcher intent.
- [ ] `MainActivity` calls `enableEdgeToEdge()` and `setContent { AegisTheme { HomeScreen() } }`.
- [ ] `HomeScreen` renders at minimum an "AEGIS" text label on a dark background.
- [ ] `BackHandler { }` prevents the launcher from finishing on back press.
- [ ] Activity `launchMode` is `singleTask` (justified in DEC-010 and ARCHITECTURE.md).
- [ ] Activity `stateNotNeeded` is `true` (justified in DEC-010 and ARCHITECTURE.md).
- [ ] Activity `excludeFromRecents` is `true` (justified in DEC-010 and ARCHITECTURE.md).
- [ ] DT-MANIFEST-01: Repeated Home presses do not stack multiple Activity instances.
- [ ] DT-MANIFEST-02: AEGIS does not appear in the Android Recents screen.

---

## Test Strategy

- Unit test: Confirm basic Kotlin test runs (placeholder only in this phase).
- Instrumentation test: Placeholder only; actual Compose tests in Phase 2+.
- Build verification is the primary validation for this phase.

---

## Risks

- AGP version incompatibility (RISK-001) — if 8.7.3 fails, escalate to 9.0.x.
- Gradle wrapper download (RISK-005) — verify network access before starting.
- `local.properties` SDK path must match actual SDK location.

---

## Completion Criteria

This phase is complete when:
- All 23 subtasks are marked done.
- `./gradlew assembleDebug` succeeds.
- `./gradlew testDebugUnitTest` succeeds.
- `./gradlew lintDebug` produces no errors.
- `.gitignore` verified to exclude `local.properties` before any commit.
- `PROGRESS_TRACKER.md` is updated.
- `PHASE_LOG.md` records the outcome.
- `NEXT_STEPS.md` points to PHASE-02.

---

## Next Phase After Completion

**PHASE-02:** HUD Design System — implement full color palette, typography, HUD panel components, and static dashboard shell.
