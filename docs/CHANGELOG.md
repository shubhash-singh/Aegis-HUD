# CHANGELOG.md — AEGIS HUD Launcher

---

## [Unreleased] — Development

### [0.1.1-planning] — Planning Review Corrections — 2026-10-25

**Changed:**
- Added a verified Gradle 8.9 wrapper-bootstrap procedure and associated decision/risk handling.
- Corrected battery, widget-binding, animation, ViewModel-factory, test-boundary, and Phase 7 dependency documentation.
- Corrected the tracker commit reference and updated the planning review handoff.
- Updated the feature-plan total to 102 phases and the decision-log count to 12.
- Added the `reviews/` planning-audit and Phase 1 readiness reports.

### [0.1.0-dev] — Phase 0 Complete — 2026-10-25

**Added:**
- Full project planning documentation:
  - `AGENTS.md` — Agent session instructions and workflow rules
  - `README.md` — Project overview and setup guide
  - `docs/PROJECT_PLAN.md` — Full roadmap and milestones
  - `docs/FEATURE_PLAN.md` — 19 features across P0-P3 with 102 phases
  - `docs/ARCHITECTURE.md` — Layered architecture and package structure
  - `docs/TECH_STACK.md` — Technology choices and version catalog TOML
  - `docs/UI_UX_SPECIFICATION.md` — HUD visual design system specification
  - `docs/REQUIREMENTS.md` — Functional and non-functional requirements
  - `docs/TEST_PLAN.md` — Testing strategy and acceptance criteria
  - `docs/PROGRESS_TRACKER.md` — Authoritative project tracker
  - `docs/PHASE_LOG.md` — Chronological phase log
  - `docs/DECISIONS.md` — 12 architectural decisions recorded
  - `docs/BUG_TRACKER.md` — Bug tracking log (initially empty)
  - `docs/RISK_REGISTER.md` — 13 technical and platform risks documented
  - `docs/NEXT_STEPS.md` — Session handoff and Phase 1 task definition
  - `docs/phases/PHASE-00-PLANNING.md` — Phase 0 detail document
  - `docs/phases/PHASE-01-FOUNDATION.md` — Phase 1 detail document (plan only)

**Decided:**
- Min SDK 26, Target SDK 35
- Compose BOM 2024.11.00
- AGP 8.7.3 with Gradle 8.9
- Single `app` module for initial phases
- No DI framework for initial phases
- DataStore Preferences for settings
- No external image loading library initially
- No fake telemetry, ever

---

## [Future] — Planned

### [0.1.0-alpha] — Phase 1-3 Complete (Planned)
- Minimal launcher registered as home app
- HUD design system
- Live clock and battery telemetry

### [0.1.0-beta] — Phase 4-6 Complete (Planned)
- Application discovery and drawer
- Search
- Animated radar graphics

### [0.1.0] — Initial Personal Release (Planned)
- All P0 and P1 features complete
- Performance and reliability tested on physical device
- Documentation finalized
