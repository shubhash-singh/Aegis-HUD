# PROJECT_PLAN.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Created:** 2026-10-25
**Last Updated:** 2026-10-25
**Status:** Phase 4 Implementation Complete — Build Verified (Device Validation Pending)

---

## 1. Project Summary

AEGIS HUD Launcher is a personal Android home-screen launcher built with Kotlin and Jetpack Compose. It presents a sci-fi engineering console interface with live system telemetry, animated HUD graphics, application management, and developer-grade diagnostics.

The project is built for personal daily use on a physical Android device. It is designed to be stable, battery-conscious, and fully functional as a primary launcher.

---

## 2. Development Stages

| Stage | Phase | Deliverable | Status |
|---|---|---|---|
| Planning | Phase 0 | Requirements, architecture, feature plan, documentation | Complete |
| Foundation | Phase 1 | Minimal working Android launcher, home activity registered | Implementation complete — device validation pending |
| Design System | Phase 2 | HUD design system, static dashboard shell | Implementation complete — build verified |
| Live Data | Phase 3 | Live clock and date dashboard shell | Implementation complete — build verified |
| App Discovery | Phase 4 | Application discovery and launching | Implementation complete — build verified (device validation pending) |
| Drawer & Search | Phase 5 | Search and application drawer | Implementation complete — build verified (device validation pending) |
| HUD Graphics | Phase 6 | Animated radar and technical HUD graphics | Implementation complete — build verified (device validation pending) |
| Telemetry | Phase 7 | Network and permitted system telemetry | Implementation complete — build verified (device validation pending) |
| Quick Actions | Phase 8 | Quick-action shortcuts | Not started |
| Customization | Phase 9 | Persistent customization settings | Not started |
| App Org | Phase 10 | Favorites, sorting, app organization | Not started |
| Widgets/Gestures | Phase 11 | Widget hosting feasibility and gesture support | Not started |
| Dev Console | Phase 12 | Developer console and optional automation | Not started |
| QA | Phase 13 | Performance, privacy, reliability, regression testing | Not started |
| Device Testing | Phase 14 | Personal device acceptance testing | Not started |
| Release Prep | Phase 15 | Documentation cleanup and release preparation | Not started |

---

## 3. Technical Architecture Summary

### Single-module Application (Initial)

The application starts as a single Gradle module (`app`). Module boundaries will be introduced when complexity justifies it.

### Key Architectural Layers

```
UI Layer (Compose)
    |
ViewModel Layer (AndroidX ViewModel + StateFlow)
    |
Domain / Repository Layer (Kotlin classes + coroutines)
    |
Data / Platform Layer (Android APIs, DataStore)
```

### State Management

- `StateFlow` and `collectAsStateWithLifecycle()` for all live data.
- No global shared mutable state.
- ViewModel scoped to Activity or navigation back-stack entry.

---

## 4. Feature Priority Summary

| Priority | Features |
|---|---|
| P0 (Essential) | F-001 Launcher Foundation, F-002 Design System, F-003 HUD Home, F-004 App Discovery, F-005 Search, F-006 Battery Telemetry |
| P1 (Developer HUD) | F-007 Radar Graphics, F-008 System Telemetry, F-009 Quick Actions, F-010 Customization, F-011 App Organization |
| P2 (Advanced) | F-012 Widgets, F-013 Context-Aware HUD, F-014 Developer Console, F-015 Gesture Navigation, F-016 Performance |
| P3 (Future) | F-017 Advanced Themes, F-018 Automation, F-019 Open-Source Prep |

---

## 5. Android Platform Constraints

### Launcher Registration

- Must declare `android.intent.action.MAIN` with `android.intent.category.HOME` and `android.intent.category.DEFAULT`.
- Must handle `ACTION_PACKAGE_ADDED`, `ACTION_PACKAGE_REMOVED`, `ACTION_PACKAGE_CHANGED` broadcasts to keep app list fresh.

### Package Visibility (Android 11+, API 30+)

- Must declare `<queries>` in `AndroidManifest.xml` to discover launchable apps.
- Use `Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)` query.
- Cannot enumerate all installed packages without `QUERY_ALL_PACKAGES` (restricted permission).

### System Settings / Toggles

- Cannot toggle Wi-Fi or Bluetooth directly (deprecated/removed APIs).
- Can open Settings panels via intents.
- Flashlight control requires `CAMERA` permission or `android.hardware.camera2` API.

### Permissions Required (Initial)

- `RECEIVE_BOOT_COMPLETED` — optional, for launcher persistence.
- No special permissions required for app discovery with `<queries>`.
- `ACCESS_NETWORK_STATE` — network telemetry.
- `CHANGE_NETWORK_STATE` is restricted; intent-only for toggles.

### Battery Telemetry

- Available through `BatteryManager` and sticky broadcast `ACTION_BATTERY_CHANGED`.
- No permission required for basic battery info.
- No background service needed; use sticky broadcast on demand.

---

## 6. Risk Summary

| Risk | Severity | Mitigation |
|---|---|---|
| AGP / Compose version incompatibility | High | Pin to stable, tested versions; see TECH_STACK.md |
| Package visibility restrictions on API 30+ | Medium | Use queries manifest element; test on API 30+ |
| Launcher not selected as default | Low (UX) | Implement clear first-run prompt |
| System setting toggle APIs deprecated | Medium | Use intent-based flows; document limitations |
| Gradle wrapper download fails on NixOS | Medium | Pin Gradle version; verify network access |
| Compose performance on budget devices | Medium | Profile early; minimize recompositions |

---

## 7. Delivery Goals (Initial Personal Release)

The initial release (v0.1) is considered ready when:

- AEGIS can be selected and operates as the Android default launcher.
- The home screen displays the HUD with working clock and battery.
- Application discovery, drawer, and search work correctly.
- Applications launch successfully.
- Settings and permissions behave predictably.
- The APK builds reproducibly.
- Required tests and known limitations are documented.

---

## 8. Dependencies Between Phases

```
Phase 0 (Planning) -> Phase 1 (Foundation) -> Phase 2 (Design System)
Phase 2 -> Phase 3 (Live Data)
Phase 2 -> Phase 4 (App Discovery)
Phase 4 -> Phase 5 (Search + Drawer)
Phase 2 -> Phase 6 (HUD Graphics)
Phase 3 -> Phase 7 (Telemetry)
Phase 3 -> Phase 8 (Quick Actions)
Phase 2 -> Phase 9 (Customization)
Phase 4 + Phase 9 -> Phase 10 (App Organization)
Phase 5 -> Phase 11 (Widgets / Gestures)
Phase 7 -> Phase 12 (Dev Console)
All P0+P1 -> Phase 13 (QA)
Phase 13 -> Phase 14 (Device Testing)
Phase 14 -> Phase 15 (Release)
```
