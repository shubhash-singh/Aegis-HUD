# AEGIS HUD Launcher

**Codename:** RAGNAR HUD / AEGIS
**Platform:** Android (Kotlin + Jetpack Compose)
**Package:** dev.ragnar.aegis
**Status:** Phase 0 — Planning Complete, Phase 1 Pending

---

## Overview

AEGIS is a personal, developer-grade Android home-screen launcher with a sci-fi HUD interface. It is designed to look and feel like a real technical engineering console — not a conventional launcher with neon colors applied on top.

AEGIS displays live system telemetry, provides a fully functional application drawer and search, and is built to be customized for everyday use on a personal Android device.

---

## Current Status

- Phase 0 (Planning) — **Complete**
- Phase 1 (Launcher Foundation) — **Not started**
- Full feature list: see `docs/FEATURE_PLAN.md`
- Progress: see `docs/PROGRESS_TRACKER.md`
- Next step: see `docs/NEXT_STEPS.md`

---

## Features (Planned)

- Register as Android default home-screen launcher
- Futuristic HUD home screen with live clock, date, and battery telemetry
- Animated radar rings and scan-line graphics
- Full application drawer with search
- Developer-grade system telemetry panel
- Configurable quick-launch slots and favorites
- Customizable accent colors, animation speed, and visible panels
- Optional developer console with genuine launcher diagnostics
- Gesture navigation

---

## Prerequisites

| Requirement | Version |
|---|---|
| Android Studio | Latest stable (Toolbox) |
| JDK | 17+ (21 recommended) |
| Android SDK | API 35+ |
| Kotlin | 2.0+ |
| AGP | 8.7+ |
| Min Android | 8.0 (API 26) |
| Target Android | Android 15 (API 35) |

---

## Setup

1. Clone the repository.
2. Open in Android Studio.
3. Ensure `local.properties` contains:
   ```
   sdk.dir=/home/ragnar/Android/Sdk
   ```
4. Sync Gradle.

---

## Build Commands

```bash
# Debug APK
./gradlew assembleDebug

# Unit tests
./gradlew testDebugUnitTest

# Lint
./gradlew lintDebug

# Install on device
./gradlew installDebug
```

---

## Documentation

| Document | Purpose |
|---|---|
| [AGENTS.md](AGENTS.md) | Agent session startup instructions |
| [docs/PROJECT_PLAN.md](docs/PROJECT_PLAN.md) | Full roadmap and milestones |
| [docs/FEATURE_PLAN.md](docs/FEATURE_PLAN.md) | Feature inventory |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Architecture and package structure |
| [docs/TECH_STACK.md](docs/TECH_STACK.md) | Technology choices |
| [docs/UI_UX_SPECIFICATION.md](docs/UI_UX_SPECIFICATION.md) | HUD design specification |
| [docs/PROGRESS_TRACKER.md](docs/PROGRESS_TRACKER.md) | **Authoritative progress tracker** |
| [docs/NEXT_STEPS.md](docs/NEXT_STEPS.md) | Current next implementation step |

---

## Design Principles

- Deep black backgrounds with neon cyan accents
- Monospaced technical typography
- Animated radar and telemetry graphics
- Dense but readable technical panels
- No fake telemetry presented as real data
- No excessive animations that compromise battery life

---

## License

Personal use. License to be determined before any public release.
