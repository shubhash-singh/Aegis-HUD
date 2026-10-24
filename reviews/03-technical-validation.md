# Technical Validation

## Method

Selected time-sensitive and platform-sensitive claims were checked against official Android and Kotlin documentation on 2026-10-25. Version compatibility remains unverified until the first actual Gradle build.

## Confirmed Statements

| Topic | Result | Review conclusion |
|---|---|---|
| Google Play target API policy | Confirmed | The policy requires API 36 for new apps and updates from 2026-08-31. The documents correctly state that API 35 is a deliberate personal-use target, not the latest requirement. |
| `android:stateNotNeeded` | Confirmed | Android says a true value allows restart without saved state; `onSaveInstanceState()` may not be called and `onCreate()` can receive `null`. DEC-010 documents this trade-off correctly. |
| Battery status | Confirmed with wording correction | `Intent.ACTION_BATTERY_CHANGED` is the sticky broadcast. Battery data is then read from intent extras defined by `BatteryManager`. |
| Widget host binding | Confirmed with wording correction | The host flow declares `BIND_APPWIDGET`; when binding is not already permitted, it must ask the user through the system binding flow. |
| `FLAG_DIM_BEHIND` | Confirmed as incorrect usage | The flag dims windows behind the current window; it is not an accessibility or power-saving animation signal. |
| Compose ViewModel factory | Confirmed with wording correction | Compose `viewModel()` accepts a `ViewModelProvider.Factory`; constructor-injected ViewModels need that factory path. |
| Coroutine testing | Confirmed with wording correction | Current recommended APIs use `runTest`, a `TestScope`, and `StandardTestDispatcher` or `UnconfinedTestDispatcher`, not `TestCoroutineDispatcher`. |

## Primary Sources

- [Google Play target API requirements](https://developer.android.com/google/play/requirements/target-sdk)
- [`<activity>` manifest attributes](https://developer.android.com/guide/topics/manifest/activity-element)
- [Monitor battery level and charging state](https://developer.android.com/training/monitoring-device-state/battery-monitoring)
- [`Intent.ACTION_BATTERY_CHANGED` reference](https://developer.android.com/reference/android/content/Intent#ACTION_BATTERY_CHANGED)
- [Build a widget host](https://developer.android.com/develop/ui/views/appwidgets/host)
- [`WindowManager.LayoutParams.FLAG_DIM_BEHIND` reference](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_DIM_BEHIND)
- [Compose `viewModel` API reference](https://developer.android.com/reference/kotlin/androidx/lifecycle/viewmodel/compose/package-summary)
- [Testing Kotlin coroutines on Android](https://developer.android.com/kotlin/coroutines/test)

## Environment Facts Checked Locally

| Check | Result |
|---|---|
| Java runtime | OpenJDK 21.0.12 present |
| Android SDK | API 35 and API 36 platform directories present |
| ADB | Version 1.0.41 available |
| `local.properties` | Absent, as expected before Phase 1 |
| System `gradle` | Not available |
| Gradle wrapper | Not present |
| Application source/build output | Not present |

## Still Unverified

- AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.11.00, Gradle 8.9, and JDK 21 working together on this machine.
- Debug APK generation, unit tests, lint, default-home registration, Recents exclusion, and repeated-Home behaviour.
- Physical-device acceptance and performance targets.

Those claims must remain unverified until the documented Phase 1 commands and device checks have actually run.
