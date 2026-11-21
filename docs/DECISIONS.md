# DECISIONS.md — AEGIS HUD Launcher

**Last Updated:** 2026-10-25

---

## Decision Log

Significant architectural and implementation decisions are recorded here with alternatives considered, reasoning, and consequences.

---

### DEC-001: Minimum SDK — API 26 (Android 8.0 Oreo)

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Set `minSdk = 26`. `Accepted: 2026-10-25 01:09`

**Alternatives Considered:**
- API 21 (Android 5.0): Broadest coverage but excludes several modern APIs and requires additional compatibility shims.
- API 28 (Android 9): Would simplify some permission handling but reduces device coverage unnecessarily.
- API 30 (Android 11): Would simplify package visibility handling but excludes many still-active devices.

**Reasoning:**
- API 26 provides access to `BatteryManager` APIs, reliable `ConnectivityManager` callbacks, and modern Compose-compatible lifecycle behavior.
- Covers approximately 97%+ of active Android devices as of 2024.
- Is a practical minimum for a Compose-based application.

**Consequences:**
- Requires `<queries>` manifest element for package visibility (enforced from API 30, harmless on lower).
- `RoleManager.ROLE_HOME` requires API 29+; a fallback is needed for API 26-28.

---

### DEC-002: Target SDK — API 35 (Android 15)

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted (rationale clarified 2026-10-25)

**Decision:** Set `targetSdk = 35`.

**Reasoning:**
- API 35 (Android 15) is the stable release used in the development environment's primary SDK platform.
- API 36 (Android 16) is also stable as of March 2025, but AEGIS begins development with API 35 to remain on a well-tested platform.
- Google Play requires API 36 targeting for new and updated apps from August 31, 2026. This project is for personal use; Play Store submission is not planned before Phase 15.
- Upgrading to targetSdk 36 can be evaluated before Phase 15 with minimal code changes.

**Consequence:** All behavior changes from API 27 through API 35 apply. Key changes to verify during implementation: predictive back gesture (API 33), overlay permissions, foreground service type requirements (API 34).

**Correction note (2026-10-25):** The previous statement "API 35 is the latest stable release" was inaccurate. API 36 is also stable. The decision to target API 35 is deliberate, not based on API 35 being the only option.

---

### DEC-003: Compose BOM — 2024.11.00

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Use Compose BOM `2024.11.00` to pin all Compose library versions.

**Reasoning:** Stable BOM from November 2024. Avoids version mismatches between Compose libraries. BOM updates atomically for all Compose artifacts.

**Consequences:** Must update BOM together when upgrading Compose. Individual library overrides are possible but discouraged.

---

### DEC-004: Android Gradle Plugin — 8.7.3

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Use AGP `8.7.3`.

**Alternatives Considered:**
- AGP 9.2.1: Used by the reference project (`GlassMorphismTest`) but is bleeding-edge and may have undocumented breaking changes for Compose projects.
- AGP 8.5.x: Older stable, fewer features.

**Reasoning:** AGP 8.7.x is the latest LTS-aligned stable release that pairs with Gradle 8.9 and Kotlin 2.0.21. More stable than 9.x at the time of planning.

**Update if evidence shows 9.x is necessary.**

**Consequences:** Gradle wrapper pinned to 8.9.

---

### DEC-005: Single Module Architecture

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Use a single `app` Gradle module for Phase 1-10.

**Alternatives Considered:**
- Multi-module from the start: Higher setup complexity; slower initial development velocity; premature abstraction.

**Reasoning:** The project is a personal launcher, not a large team codebase. A single module is simpler to build, test, and understand. Module boundaries can be introduced in Phase 11+ if the codebase warrants it.

**Consequences:** All source code in `app/src/main/`. Package structure within the module provides logical separation.

---

### DEC-006: No Dependency Injection Framework (Initial Phases)

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Use manual dependency wiring (no Hilt, no Koin) for Phases 1-5.

**Alternatives Considered:**
- Hilt: Powerful but adds annotation processing overhead and boilerplate for a small initial scope.
- Koin: Lighter, but still adds runtime dependency lookup complexity.

**Reasoning:** For 3-5 ViewModels and 4-6 repositories, manual wiring via `ViewModelProvider.Factory` is simpler and more transparent. Revisit if the number of dependencies grows significantly.

**Trigger for re-evaluation:** More than 8 repositories or cross-cutting concerns that would benefit from injection.

---

### DEC-007: DataStore Preferences over SharedPreferences

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Use `androidx.datastore.preferences` for all persistent settings.

**Reasoning:** DataStore is the AndroidX-recommended replacement for SharedPreferences. It is coroutine-based, type-safe for simple key-value stores, and avoids the ANR risks of synchronous SharedPreferences I/O.

**Consequences:** Preferences are accessed as `Flow<Preferences>`, which requires coroutines and lifecycle awareness.

---

### DEC-008: No External Image Loading Library (Initial)

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Accepted

**Decision:** Load app icons directly via `PackageManager.getApplicationIcon()` without Coil or Glide.

**Reasoning:** App icon loading is the only image use case in initial phases. `PackageManager.getApplicationIcon()` returns a `Drawable` that can be converted to a `BitmapPainter` for Compose rendering. No external dependency is needed.

**Trigger for re-evaluation:** If memory management of many icons becomes problematic, or if other image types are needed.

---

### DEC-009: No Fake Telemetry

**Date:** 2026-10-25
**Phase:** PHASE-00
**Status:** Permanent

**Decision:** All telemetry displayed on the HUD must come from real Android platform APIs. No simulated or hardcoded telemetry values will be displayed as if they are real readings.

**Reasoning:** User trust; design integrity; the prompt specification explicitly requires this.

**Consequences:** If a telemetry value is unavailable, it must be displayed as "N/A", "—", or equivalent — never as a fabricated number.

---

### DEC-010: Launcher Activity Manifest Attributes

**Date:** 2026-10-25
**Phase:** PHASE-00 (Correction Task)
**Status:** Accepted

**Decision:** Use `launchMode="singleTask"`, `stateNotNeeded="true"`, and `excludeFromRecents="true"` on `MainActivity`. Each attribute is justified independently below.

**`launchMode="singleTask"` — Accepted**
Without this, pressing Home while the launcher is already displayed could push a second `MainActivity` instance onto the back stack, causing nested launcher screens and broken navigation. A single guaranteed instance is required for correct home-screen behavior. Standard practice for Android launcher activities.

**`stateNotNeeded="true"` — Accepted**
The launcher home screen is always reconstructed from live data sources (PackageManager, BatteryManager, DataStore); it does not depend on a saved UI bundle. Declaring `stateNotNeeded="true"` makes this design contract explicit to the OS and avoids unnecessary `onSaveInstanceState()` serialization. This is a deliberate design choice suited to AEGIS's stateless-reconstruction model, not a universal launcher requirement. Trade-off: transient UI positions (e.g., drawer scroll offset) are lost on process death, which is acceptable because the launcher always returns the user to the home screen after a restart.

**`excludeFromRecents="true"` — Accepted**
The home screen should not appear as a card in the Android Overview (Recents) screen. Appearing in Recents would allow the user to accidentally swipe away the launcher task, which is unexpected behavior for a home screen application.

**Alternative considered:** Using Android defaults (standard launch mode, stateNotNeeded=false, no excludeFromRecents). Rejected: would allow stacked launcher instances, unnecessary saved-state overhead, and launcher appearance in Recents.

**Test requirements:** See ARCHITECTURE.md Section 6 and TEST_PLAN.md (DT-MANIFEST-01 through DT-MANIFEST-04).

---

### DEC-011: Toolchain Versions Confirmed via Official Documentation

**Date:** 2026-10-25
**Phase:** PHASE-00 (Correction Task)
**Status:** Accepted — Unverified by build

**Decision:** Retain the planned toolchain: AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.11.00, Gradle 8.9, compileSdk 35, targetSdk 35, minSdk 26, JDK 17/21.

**Verification source:** Official Android and Kotlin documentation (reviewed 2026-10-25).
- AGP 8.7.3 requires Gradle 8.9 minimum — confirmed via Android developer documentation.
- Kotlin 2.0+ bundles the Compose Compiler plugin; `kotlinCompilerExtensionVersion` is no longer used — confirmed.
- Compose BOM 2024.11.00 is compatible with Kotlin 2.0 and AGP 8.7 — confirmed.
- JDK 21 is compatible with AGP 8.7 and Kotlin 2.0 — confirmed.

**Important note:** No application build has been run. Compatibility is based on official documentation. Actual build success will be confirmed in Phase 1. If a conflict is discovered, update this decision and TECH_STACK.md accordingly.

---

### DEC-012: Initial Gradle Wrapper Bootstrap

**Date:** 2026-10-25
**Phase:** PHASE-00 (Planning Review Corrections)
**Status:** Accepted

**Decision:** Bootstrap the initial wrapper from the official Gradle 8.9 binary distribution, then use only `./gradlew` for project builds.

**Method:** Download `https://services.gradle.org/distributions/gradle-8.9-bin.zip`, verify SHA-256 `d725d707bfabd4dfdc958c624003b3c80accc03f7037b5122c4b1d0ef15cecab` against `https://gradle.org/release-checksums/`, unpack outside this repository, and run `bin/gradle :wrapper --gradle-version 8.9 --distribution-type bin` from the repository root. Set the generated wrapper's `distributionSha256Sum` to the same value.

**Reasoning:** The repository has no wrapper and the development machine has no system Gradle. Gradle documents that the wrapper is generated by an installed Gradle distribution; the verified official distribution is the reproducible bootstrap source.

**Consequences:** Do not commit the downloaded ZIP or unpacked temporary distribution. Commit the generated wrapper scripts, JAR, and properties only when the user explicitly requests a commit. If the download or checksum validation fails, record RISK-005 as active and stop before generating project files.

---

### DEC-013: AAPT2 Dynamic Loader Patching on NixOS

**Date:** 2026-11-21
**Phase:** PHASE-04
**Status:** Accepted

**Decision:** Patch the AAPT2 binary downloaded by AGP into Gradle transforms cache using `patchelf --set-interpreter <nixos-glibc-ld>` to enable execution on NixOS.

**Reasoning:** Standard prebuilt Linux binaries from Google (including AAPT2) request the standard dynamic linker `/lib64/ld-linux-x86-64.so.2`, which does not exist in standard locations on NixOS. Running `patchelf` with the path to the system glibc loader allows AGP tasks (`:app:processDebugResources`, `assembleDebug`, `lintDebug`) to execute natively without requiring full FHS wrapper environments or modifying build configuration files.

**Consequences:** Resolves B-003 and RISK-014. If Gradle caches are cleared or a newer AGP/AAPT2 version is fetched, the patchelf command must be re-run on the newly unpacked AAPT2 binary.

---

### DEC-014: Full-Screen Neural AI Core Replaces Radar Composable

**Date:** 2026-11-21
**Phase:** PHASE-10
**Status:** Accepted

**Decision:** Replace the existing `RadarDisplay` composable with a new full-screen `AICoreVisualization` composable rendered via Compose `Canvas`. The visualization fills the entire screen behind all floating information panels.

**Alternatives Considered:**
- Enhance the existing radar composable: Limited visual impact; still confined to a card container.
- Use an external GL/Vulkan surface: Higher complexity, harder Compose integration.
- Animated Lottie file: External dependency; insufficient control over data-driven visual state.

**Reasoning:** The design brief specifies a "living AI intelligence core" as the dominant visual element that occupies almost the entire home screen. The existing radar widget inside a card does not achieve this. A full-screen Canvas composable gives full artistic and performance control.

**Consequences:**
- `RadarDisplay.kt` is archived (not deleted) — superseded by `AICoreVisualization.kt`.
- Static geometry must be cached to avoid per-frame allocation.
- Animation state must be isolated in `NeuralAnimationState` to prevent floating panel data updates from causing `AICoreVisualization` recomposition.
- `RadarMath.kt` utility class remains valid and may be referenced for geometric calculations.

---

### DEC-015: Floating Transparent Panels Replace Stacked Cards

**Date:** 2026-11-21
**Phase:** PHASE-10
**Status:** Accepted

**Decision:** Replace all stacked Telemetry, System Status, and Quick Actions card rows with seven independent floating semi-transparent HUD panels (`FloatingCorePanel`, `FloatingAiStatusPanel`, `FloatingPerformancePanel`, `FloatingDevicePanel`, `FloatingIntelligencePanel`, `FloatingTelemetryPanel`, `FloatingSystemStatusPanel`) positioned using fractional responsive coordinates over the neural visualization.

**Alternatives Considered:**
- Keep stacked cards below the visualization: Contradicts the full-screen design intent.
- Overlay panels using fixed `dp` coordinates: Not responsive; breaks on different screen sizes.

**Reasoning:** Floating panels over the full-screen visualization match the holographic HUD aesthetic and ensure the neural brain remains the dominant visual element. Using `BoxWithConstraints` fractional positioning ensures responsiveness across screen sizes.

**Consequences:**
- Each panel composable must be isolated (stable parameters) to avoid causing `AICoreVisualization` recomposition.
- No `Material Card` or `Surface` elevation. Panels use direct `Canvas` border drawing via `HudBorder`.
- Background blur (`BlurMaskFilter` or `Modifier.blur()`) is optional and must respect user "reduce effects" setting.

---

### DEC-016: Swipe-Up Gesture Replaces Launcher Dock

**Date:** 2026-11-21
**Phase:** PHASE-10
**Status:** Accepted

**Decision:** Remove the persistent bottom launcher dock row (5 app shortcuts), home-screen search bar, and APPS button. Replace with an `AppDrawerGestureIndicator` (breathing chevrons + "SWIPE UP" text) and a swipe-up gesture using `detectVerticalDragGestures` to open the app drawer.

**Alternatives Considered:**
- Keep the dock row for fast access: Contradicts full-screen design intent; clutters the bottom.
- Floating action button to open drawer: Less intuitive than gesture; adds UI clutter.

**Reasoning:** A swipe-up gesture is a modern, well-established launcher interaction (as used by Pixel Launcher, Samsung One UI, etc.). It is consistent with Android platform conventions and clears the bottom of the home screen for the neural visualization. The breathing chevron indicator provides discoverability without permanent clutter.

**Consequences:**
- Gesture detection zone must exclude the system navigation bar inset (`WindowInsets.navigationBars`).
- Quick-launch app shortcuts and search are still accessible inside the drawer.
- Existing `AppDrawerScreen`, search, and app-discovery code remain unchanged.
