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

**Decision:** Set `minSdk = 26`.

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
**Status:** Accepted

**Decision:** Set `targetSdk = 35`.

**Reasoning:** API 35 is the latest stable release. Targeting it ensures the app uses the latest Android behavior contract and avoids compatibility modes.

**Consequences:** All behavior changes from API 27 through API 35 apply. Key changes to verify: predictive back gesture (API 33), photo picker (API 33), exact alarms (API 33), health connect (not used), overlay permissions, and foreground service type requirements.

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
