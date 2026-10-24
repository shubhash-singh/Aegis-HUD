# Findings

## Required Before Phase Acceptance

| ID | Severity | Finding | Evidence | Required action |
|---|---|---|---|---|
| P1-01 | High | The entire Phase 1 Android project is untracked. | `git ls-files --others --exclude-standard` lists 23 Phase 1 project files, including the Gradle wrapper, module configuration, manifest, source, resources, and tests. | Review and commit the Phase 1 implementation in its own commit. Do not treat build artifacts as a substitute for versioned source. |
| P1-02 | High | Project-status documentation conflicts with the repository. | `docs/PROGRESS_TRACKER.md`, `docs/NEXT_STEPS.md`, and `docs/PHASE_LOG.md` state that Phase 1 has not started, no source exists, and no build or test has run. | Update the phase document, tracker, phase log, next steps, bug tracker as applicable, and changelog to state the actual implementation and validation status. |
| P1-03 | Medium | Required physical-device validation is pending. | `adb devices -l` returned no connected devices. DT-MANIFEST-01 and DT-MANIFEST-02 are explicit Phase 1 acceptance criteria. | Connect an Android device, make AEGIS the default home app, verify repeated Home presses do not stack activities, and verify AEGIS is excluded from Recents. Record the result. |

## Non-Blocking Improvements

| ID | Severity | Finding | Evidence | Recommendation |
|---|---|---|---|---|
| P1-04 | Low | The adaptive launcher icons retain an unnecessary `-v26` qualifier and omit a monochrome layer. | The app has `minSdk = 26`; lint reports `ObsoleteSdkInt` and two `MonochromeLauncherIcon` warnings for `mipmap-anydpi-v26`. | Move the adaptive-icon XML files to `mipmap-anydpi` and add a suitable monochrome layer before release. |
| P1-05 | Low | The instrumented test validates only the application ID. | `ExampleInstrumentedTest` asserts `targetContext.packageName`; it does not exercise home-launcher selection or back navigation. | Add behaviour-focused instrumentation coverage when a device or emulator is available; retain the manual manifest tests as the acceptance authority. |

## Accepted Decisions

- The Gradle 8.9 wrapper is pinned with the documented SHA-256 checksum.
- `targetSdk = 35` and the declared AGP/library versions match the approved project technology plan. Lint's newer-version notices are informational, not a review defect.
- The app intentionally uses `singleTask`, `stateNotNeeded`, and `excludeFromRecents`, consistent with DEC-010 and the Phase 1 plan.
