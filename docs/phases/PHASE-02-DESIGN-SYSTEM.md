# PHASE-02-DESIGN-SYSTEM.md — AEGIS HUD Launcher

**Phase ID:** PHASE-02
**Title:** HUD Design System
**Status:** Implementation Complete — Build Verified (2026-11-01)
**Dependency:** PHASE-01 complete

---

## Objective

Implement the full AEGIS HUD design system: CompositionLocal-based color/typography/spacing tokens, bundled JetBrains Mono and Inter font assets, HUD panel composable with corner tick decorations, status indicator composable, and responsive layout handling.

---

## Subtasks

| # | Task | Status |
|---|---|---|
| 1 | Download and bundle JetBrains Mono font files (Regular, Bold) | ✅ Done |
| 2 | Download and bundle Inter font files (Regular, Medium, SemiBold) | ✅ Done |
| 3 | Refactor Color.kt to data class AegisColorScheme with CompositionLocal | ✅ Done |
| 4 | Refactor Typography.kt to use bundled fonts + CompositionLocal | ✅ Done |
| 5 | Create Spacing.kt with AegisSpacing data class + CompositionLocal | ✅ Done |
| 6 | Refactor AegisTheme.kt to provide all CompositionLocals and AegisTheme accessor object | ✅ Done |
| 7 | Create HudPanel composable with borders, corner ticks, section header | ✅ Done |
| 8 | Create StatusIndicator composable (dot with status colors) | ✅ Done |
| 9 | Create HudDivider composable | ✅ Done |
| 10 | Update HomeScreen.kt to use AegisTheme.* accessors and demonstrate components | ✅ Done |
| 11 | Create design system unit tests | ✅ Done (5 tests) |
| 12 | Run ./gradlew assembleDebug | ✅ BUILD SUCCESSFUL (13s) |
| 13 | Run ./gradlew testDebugUnitTest | ✅ BUILD SUCCESSFUL (7 tests) |
| 14 | Run ./gradlew lintDebug | ✅ 0 errors, 34 warnings |
| 15 | Update documentation | ✅ Done |

---

## Files Expected to Change

| File | Action |
|---|---|
| `app/src/main/res/font/jetbrains_mono_regular.ttf` | Create |
| `app/src/main/res/font/jetbrains_mono_bold.ttf` | Create |
| `app/src/main/res/font/inter_regular.ttf` | Create |
| `app/src/main/res/font/inter_medium.ttf` | Create |
| `app/src/main/res/font/inter_semibold.ttf` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Color.kt` | Refactor to data class + CompositionLocal |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Typography.kt` | Refactor with bundled fonts + CompositionLocal |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/Spacing.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/theme/AegisTheme.kt` | Refactor with CompositionLocal providers |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/components/HudPanel.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/components/StatusIndicator.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/components/HudDivider.kt` | Create |
| `app/src/main/kotlin/dev/ragnar/aegis/ui/home/HomeScreen.kt` | Update to use theme accessors |
| `app/src/test/kotlin/dev/ragnar/aegis/ui/theme/ThemeTokensTest.kt` | Create |
| `docs/PROGRESS_TRACKER.md` | Update |
| `docs/PHASE_LOG.md` | Update |
| `docs/NEXT_STEPS.md` | Update |

---

## Acceptance Criteria

- [ ] AegisTheme provides CompositionLocal for colors, typography, and spacing.
- [ ] All composables access design tokens via `AegisTheme.colors`, `AegisTheme.typography`, `AegisTheme.spacing`.
- [ ] JetBrains Mono (Regular, Bold) is bundled and used for clock/telemetry styles.
- [ ] Inter (Regular, Medium, SemiBold) is bundled and used for UI labels/body.
- [ ] No network font loading — all fonts embedded in APK.
- [ ] HudPanel composable renders with 1dp border, 2dp corner radius, and corner tick decorations.
- [ ] StatusIndicator composable renders 6dp filled circle with status-appropriate color.
- [ ] Spacing tokens match UI_UX_SPECIFICATION.md Section 4.
- [ ] No hardcoded colors, font sizes, or dimensions in composables.
- [ ] `./gradlew assembleDebug` succeeds.
- [ ] `./gradlew testDebugUnitTest` succeeds.
- [ ] `./gradlew lintDebug` — 0 errors.
