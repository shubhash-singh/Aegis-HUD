# AGENTS.md — AEGIS HUD Launcher

## Agent Session Instructions

This file is the primary instruction document for any coding agent working on the AEGIS HUD Launcher project. It must be read at the beginning of every session before any code is inspected or written.

---

## 1. Session Startup Procedure

At the beginning of every session, perform the following steps in order:

1. Read this file (`AGENTS.md`).
2. Read `README.md` and `docs/README.md`.
3. Read `docs/PROJECT_PLAN.md`.
4. Read `docs/FEATURE_PLAN.md`.
5. Read `docs/ARCHITECTURE.md` and `docs/TECH_STACK.md`.
6. Read `docs/REQUIREMENTS.md` and `docs/UI_UX_SPECIFICATION.md`.
7. Read `docs/TEST_PLAN.md`.
8. Read `docs/PROGRESS_TRACKER.md`.
9. Read `docs/PHASE_LOG.md`, `docs/DECISIONS.md`, and `docs/NEXT_STEPS.md`.
10. Read the specific phase document in `docs/phases/` associated with the next authorized task.
11. Review `docs/BUG_TRACKER.md` for open defects relevant to the current phase.
12. Review `docs/RISK_REGISTER.md` for risks relevant to the current phase.
13. Run `git status` and `git log --oneline -10` to inspect the working tree and confirm no uncommitted changes are pending.
14. Compare the documented state in `PROGRESS_TRACKER.md` with the actual repository (source files and build output). If they disagree, investigate and reconcile the difference before continuing.

**Additional rules:**
- Do **not** rely on conversation history to determine project state. Use repository files as the single source of truth.
- Do **not** assume that a previous agent's claimed work was completed or validated. Re-check actual files and build outputs.
- If the tracker and repository disagree, document the discrepancy and resolve it before continuing implementation.
- Resume only the next authorized, unfinished task identified in `NEXT_STEPS.md` and `PROGRESS_TRACKER.md`.

---

## 2. Core Workflow Rules

### Rule: Plan Before Coding

Do not implement features without reading the relevant planning documents first. If planning documents are missing or stale, restore them before continuing.

### Rule: One Phase at a Time

Implement only the currently defined phase. Do not begin the next phase until the current phase satisfies its completion criteria or an explicit blocker is documented.

### Rule: Validation Is Not Optional

Never mark a phase complete without:
- A successful `./gradlew assembleDebug` (or documenting why it could not run).
- Relevant unit tests passing (or documenting which tests are pending).
- Acceptance criteria evaluated.
- Documentation updated.

If a build or test cannot run, mark the phase as **Implementation Complete - Validation Pending** and document the blocker.

### Rule: No Fake Success

Never claim a build succeeded, a feature works, or a test passes unless the relevant command was actually executed and its output observed. Distinguish between: Implemented / Build Verified / Unit Tested / Instrumentation Tested / Manually Tested / Fully Accepted.

### Rule: Documentation Must Stay Synchronized

After every phase, update:
- `docs/PROGRESS_TRACKER.md`
- `docs/PHASE_LOG.md`
- `docs/NEXT_STEPS.md`
- The current phase document in `docs/phases/`
- `docs/DECISIONS.md` (if a significant decision was made)
- `docs/BUG_TRACKER.md` (if a defect was found)
- `docs/CHANGELOG.md` (when a meaningful milestone warrants an entry)

---

## 3. Project Identity

| Property | Value |
|---|---|
| Project name | AEGIS HUD Launcher |
| Codename | RAGNAR HUD / AEGIS |
| Package name | dev.ragnar.aegis |
| Platform | Android |
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 (Android 15) |
| Repository dir | (this repository's root directory) |
| Documentation root | docs/ |
| Git branch | main |

> **Current development machine path:** `/home/ragnar/AndroidStudioProjects/AR_HUD_Luncher`. This is environment-specific and may differ on another machine.

---

## 4. Build Environment

| Tool | Version / Location |
|---|---|
| OS | NixOS 26.11 |
| JDK | OpenJDK 21.0.12 |
| Android SDK | ~/Android/Sdk/ |
| Platforms available | android-35, android-36, android-36.1 |
| Build tools | 34.0.0, 35.0.0, 36.0.0, 36.1.0, 37.0.0 |
| AGP version | 8.7.x (stable with SDK 35 target) |
| Android Studio | Installed via JetBrains Toolbox |
| adb | /run/current-system/sw/bin/adb (v1.0.41) |
| System Gradle | Not available - always use ./gradlew wrapper |
| ANDROID_HOME | Not exported; set in local.properties |

### Required local.properties

`local.properties` is machine-specific and must **not** be committed to version control. It must exist at the repository root before building. Contents:

```
sdk.dir=<absolute path to your Android SDK>
```

Example for the primary development machine:
```
sdk.dir=/home/ragnar/Android/Sdk
```

On a different machine, set the path to the actual Android SDK location. Android Studio sets this file automatically when you open the project.

---

## 5. Standard Build Commands

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew installDebug
./gradlew clean assembleDebug
```

Always run from the repository root. Always use ./gradlew, not a system Gradle.

---

## 6. Session Handoff Template

At the end of each session, record in docs/NEXT_STEPS.md:

1. What was completed.
2. What was changed (list files).
3. What was validated (commands run and outcomes).
4. What failed or remains unverified.
5. Outstanding blockers.
6. Exact next task.
7. Files to inspect before resuming.
8. Commands to run when resuming.

---

## 7. Phase Completion Definition

A phase is complete when ALL of the following are true:

- Scope fully implemented.
- ./gradlew assembleDebug succeeds (or blocker is documented).
- Relevant unit tests pass (or pending tests are documented).
- Acceptance criteria evaluated.
- No critical regressions unresolved.
- Documentation updated.
- Progress tracker reflects real outcome.
- Next step clearly identified.

---

## 8. Key File Index

| File | Purpose |
|---|---|
| AGENTS.md | Agent session instructions (this file) |
| README.md | Project overview and setup guide |
| docs/README.md | Documentation index |
| docs/PROJECT_PLAN.md | Roadmap, milestones, risks |
| docs/FEATURE_PLAN.md | Feature inventory and phase breakdown |
| docs/ARCHITECTURE.md | Architecture, packages, data flow |
| docs/TECH_STACK.md | Technology choices and versions |
| docs/UI_UX_SPECIFICATION.md | HUD visual design specification |
| docs/REQUIREMENTS.md | Functional and non-functional requirements |
| docs/TEST_PLAN.md | Testing strategy and acceptance criteria |
| docs/PROGRESS_TRACKER.md | **Authoritative overall progress tracker** |
| docs/PHASE_LOG.md | Chronological log of phases |
| docs/DECISIONS.md | Architecture and implementation decisions |
| docs/BUG_TRACKER.md | Defect log |
| docs/RISK_REGISTER.md | Technical and platform risks |
| docs/NEXT_STEPS.md | Current next step and session handoff |
| docs/CHANGELOG.md | Milestone changelog |
| docs/phases/PHASE-00-PLANNING.md | Phase 0 detail document |
| docs/phases/PHASE-01-FOUNDATION.md | Phase 1 detail document |
| local.properties | Local SDK path (not committed; machine-specific) |
