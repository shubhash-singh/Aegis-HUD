# RISK_REGISTER.md — AEGIS HUD Launcher

**Last Updated:** 2026-10-25

---

## Active Risks

| ID | Risk | Category | Severity | Likelihood | Impact | Mitigation | Status |
|---|---|---|---|---|---|---|---|
| RISK-001 | AGP version incompatibility with Kotlin Compose plugin | Dependency | High | Medium | Build fails entirely | Pin AGP to 8.7.3 and Kotlin to 2.0.21; test on first build | Open |
| RISK-002 | Package visibility restrictions (Android 11+ API 30) | Platform | Medium | High | Apps not discoverable | Use `<queries>` element in manifest; test on API 30+ device | Open |
| RISK-003 | Launcher not selected as default by Android | Platform/UX | Low | Medium | Core functionality blocked | Implement clear first-run prompt using `RoleManager` or `ACTION_HOME_SETTINGS` | Open |
| RISK-004 | System toggle APIs deprecated (Wi-Fi, Bluetooth) | Platform | Medium | High (already happened) | Quick-action feature limited | Use intent-based flows only; document limitation clearly in UI | Open |
| RISK-005 | Gradle wrapper download failure on NixOS | Build Environment | Medium | Low | Build cannot proceed without wrapper | Pin specific Gradle version; ensure network access; use local cache if needed | Open |
| RISK-006 | Compose animation performance on target device | Performance | Medium | Medium | Janky HUD; battery drain | Profile early (Phase 6); provide animation disable setting; use Canvas-only rendering | Open |
| RISK-007 | Memory leak from registered BroadcastReceiver | Reliability | High | Medium | App crashes or system instability | Always unregister receivers in `onStop`/`onDestroy`; use lifecycle-aware registration | Open |
| RISK-008 | NetworkCallback not unregistered on activity destroy | Reliability | High | Medium | App crashes or network stack issues | Always unregister in `onStop`; use `lifecycleScope.launch` with proper cancellation | Open |
| RISK-009 | Recomposition storms in complex HUD layout | Performance | Medium | Medium | Poor performance, dropped frames | Profile with Layout Inspector; use `remember`, stable keys, `derivedStateOf` | Open |
| RISK-010 | Incorrect back press handling (launcher finish) | Reliability/UX | High | Medium | User cannot exit other apps | Use `BackHandler { /* no-op */ }` in home screen; test carefully | Open |
| RISK-011 | APK signed with debug key only — no release signing setup | Security/Distribution | Low | High (expected) | Cannot distribute outside sideloading | Acceptable for personal use; document and address in Phase 15 | Open — Deferred |
| RISK-012 | `stateNotNeeded=true` causes unexpected state loss | Platform | Low | Low | Edge case UX issues | Design launcher to rebuild state from fresh sources, not saved state | Open |
| RISK-013 | Font assets bloat APK size | Build | Low | Low | Slightly larger APK | Bundle only required font weights; use subset if possible | Open |

---

## Resolved Risks

| ID | Risk | Resolution | Phase |
|---|---|---|---|
| (none) | — | — | — |

---

## Risk Notes

### RISK-001: AGP / Kotlin Compatibility

The reference project (`GlassMorphismTest`) uses AGP 9.2.1 which is very new. AEGIS will use AGP 8.7.3 for stability. If 8.7.x cannot be resolved (e.g., incompatibility with Compose BOM 2024.11.00), escalate to 9.0.x stable and update `docs/DECISIONS.md`.

### RISK-004: Wi-Fi / Bluetooth Toggle

`WifiManager.setWifiEnabled()` was deprecated in Android 10 (API 29).
`BluetoothAdapter.enable()` was deprecated in Android 13 (API 33).
Quick Actions will use `ACTION_WIFI_SETTINGS` and `ACTION_BLUETOOTH_SETTINGS` intents. This is documented clearly in the UI.

### RISK-007 and RISK-008: Lifecycle-Aware Registration

All `BroadcastReceiver` and `NetworkCallback` registrations must be tied to the Activity or ViewModel lifecycle. This will be enforced in code review for every phase that adds such registrations.
