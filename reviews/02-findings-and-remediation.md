# Findings and Remediation

**Disposition (2026-10-25):** All documentation corrections described below were applied. The findings remain as the audit record; application build, test, lint, and device validation remain pending.

## FINDING-001 — Tracker does not name the current commit

**Severity:** Medium  
**Affected files:** `docs/PROGRESS_TRACKER.md`, `docs/PHASE_LOG.md`, `docs/NEXT_STEPS.md`

`docs/PROGRESS_TRACKER.md` identifies `68175a4` as the latest commit. The active `main` branch is actually at `7b5d588` (`docs(phase-0): final tracker consistency check`). Commit `68175a4` exists but `git fsck --unreachable` reports it as unreachable.

**Remediation:** Update the tracker with the current `HEAD` at the time of the documentation update, or replace the fixed hash with a dated reference to `main` to avoid immediate staleness. Record this review outcome in the handoff files.

## FINDING-002 — Phase 1 cannot bootstrap Gradle as documented

**Severity:** High  
**Affected files:** `docs/phases/PHASE-01-FOUNDATION.md`, `docs/NEXT_STEPS.md`, `docs/RISK_REGISTER.md`

Phase 1 requires creation of the Gradle wrapper, while this repository has no `gradlew` and the development machine has no system `gradle` executable. The current risk only covers a wrapper downloading its distribution after the wrapper already exists.

**Remediation:** Before implementation, select and document one reproducible bootstrap method: generate a project with Android Studio; fetch the official Gradle 8.9 distribution and generate the wrapper; or copy the wrapper from a vetted Gradle 8.9 project with a verified checksum. Record the distribution URL, SHA-256, and exact command path. Treat this as an explicit Phase 1 prerequisite.

## FINDING-003 — Test plan mixes JVM, Compose, and Android framework tests

**Severity:** High  
**Affected files:** `docs/TEST_PLAN.md`, `docs/phases/PHASE-01-FOUNDATION.md`, `docs/NEXT_STEPS.md`

The test strategy correctly assigns Compose tests to `app/src/androidTest/`, but several checks labelled `UT-*` require a composable, `Intent`, `ResolveInfo`, `NetworkCapabilities`, or `CameraManager`. Those are not pure JVM unit tests as currently planned. The plan also calls for the obsolete `TestCoroutineDispatcher` rather than `runTest` with `StandardTestDispatcher` or `UnconfinedTestDispatcher`.

**Remediation:**

1. Keep pure mapping, filtering, sorting, and formatting logic under `app/src/test/` behind platform-neutral models or adapters.
2. Move composable and Android API checks to `app/src/androidTest/`, or explicitly adopt Robolectric where it provides value.
3. Replace `TestCoroutineDispatcher` language with `runTest`, `TestScope`, and a chosen test dispatcher.
4. State whether the Phase 1 physical-device manifest checks are required for `Complete`; otherwise use `Implementation Complete — Validation Pending` until they run.

## FINDING-004 — Three platform statements need correction

**Severity:** Medium  
**Affected files:** `docs/FEATURE_PLAN.md`, `docs/ARCHITECTURE.md`, `docs/UI_UX_SPECIFICATION.md`

- `ACTION_BATTERY_CHANGED`, not `BatteryManager`, is the sticky broadcast. `BatteryManager` defines relevant extras and exposes property APIs.
- Widget binding requires declaring `BIND_APPWIDGET` for the custom host flow and obtaining explicit user authorization when binding is not already allowed. The plan's suggestion that the permission may require system-level elevation is misleading.
- `FLAG_DIM_BEHIND` only dims windows behind the current window. It does not express reduced-motion preference or power-saving state.

**Remediation:** Rename F-006-P1 to reference `ACTION_BATTERY_CHANGED`; replace the widget risk with the declaration-plus-user-consent flow; remove `FLAG_DIM_BEHIND` from animation policy and instead implement the planned reduce-motion preference plus `PowerManager.isPowerSaveMode` behaviour.

## FINDING-005 — Architecture guidance has two implementation mismatches

**Severity:** Medium  
**Affected files:** `docs/ARCHITECTURE.md`, `docs/PROJECT_PLAN.md`, `docs/FEATURE_PLAN.md`

- The architecture diagram says `collectAsState`, while the project rule and the battery-flow example use `collectAsStateWithLifecycle`.
- `viewModel { MyViewModel(repository) }` is not the documented Compose `viewModel` API shape. A ViewModel with dependencies needs a `ViewModelProvider.Factory` passed to `viewModel(factory = …)` or an equivalent factory-based path.
- The roadmap makes Phase 7 depend on Phases 3 and 6, but F-008's declared feature dependencies omit F-007 (the Phase 6 radar work).

**Remediation:** Standardize on lifecycle-aware state collection, show the actual factory pattern, and either add F-007 as an F-008 dependency or remove Phase 6 from the Phase 7 dependency diagram.

## FINDING-006 — Changelog metadata is stale

**Severity:** Low  
**Affected files:** `docs/CHANGELOG.md`

The changelog says the feature plan has “55+ phases” and nine architectural decisions. The feature plan now defines 102 phases (86 active, 16 deferred) and `DECISIONS.md` contains DEC-001 through DEC-011.

**Remediation:** Add a correction-task changelog entry and update the two counts.

## FINDING-007 — Untracked artifacts need an ownership decision

**Severity:** Low  
**Affected files:** Future `.gitignore`, `docs/NEXT_STEPS.md`

The pre-existing `AR_HUD_Luncher.tar.gz` is a documentation archive and `diff.txt` is a Git diff artifact. They are untracked and were not changed by this review. Without `.gitignore`, they could be staged accidentally.

**Remediation:** Decide whether to keep and track, move to a named `artifacts/` location, or remove them. Do not add a broad ignore pattern without deciding whether the archive is a project deliverable. Create `.gitignore` before introducing build or local configuration files as the plan already requires.

## Recommended Correction Order

1. Resolve FINDING-002 and FINDING-007 before creating project files.
2. Correct FINDING-001, FINDING-003, FINDING-004, and FINDING-005 in the planning documents.
3. Update FINDING-006 and the Phase 0 handoff records.
4. Obtain explicit authorization, then begin Phase 1 with the documented bootstrap path.
