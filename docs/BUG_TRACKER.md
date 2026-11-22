# BUG_TRACKER.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-22

---

## Active Bugs

| ID | Title | Severity | Status | Phase Discovered | Phase Resolved | Description / Resolution |
|---|---|---|---|---|---|---|
| B-004 | HUD telemetry values and callsign configuration | Medium | In Progress — validation pending | Maintenance | — | CPU could remain unavailable where `/proc/stat` is restricted; storage data was calculated but rendered as static `ONLINE`; voltage rendered an unavailable value; header callsign was hard-coded. Added process-CPU fallback, free/total storage display, removed voltage display, and persisted editable callsign. Build/device validation intentionally pending. |
| B-005 | Neural HUD causes sustained multi-core CPU load, jank, and severe skin thermal state | High | Open — diagnosis complete; remediation required | Hardware validation / PHASE-15 | — | On OnePlus CPH2401 (Android 14, 90 Hz), AEGIS used about 2.5 CPU cores while foregrounded; frame median was 36 ms with 10.69% jank; total PSS was 160.97 MB/RSS 301.70 MB; skin temperature reached 52.0°C (severity 3). Root cause is display-rate redraw of overlapping 3D and 2D neural meshes plus dynamic glow/pulse/ring/code-rain layers. Full evidence and remediation scope: `docs/reports/2026-11-22-device-performance-diagnosis.md`. |

---

## Resolved Bugs

| ID | Title | Severity | Status | Phase Discovered | Phase Resolved | Description / Resolution |
|---|---|---|---|---|---|---|
| B-003 | AAPT2 runtime failure on NixOS | High | Resolved | PHASE-04 | PHASE-04 | Prebuilt AAPT2 missing `/lib64/ld-linux-x86-64.so.2` on NixOS. Resolved by patching the ELF interpreter with `patchelf` pointing to NixOS glibc loader. (DEC-013) |

---

## Bug Report Template

When recording a bug, use this format:

| Field | Value |
|---|---|
| Bug ID | BUG-NNN |
| Title | Short description |
| Severity | Critical / High / Medium / Low |
| Status | Open / In Progress / Resolved / Deferred |
| Phase Discovered | PHASE-XX |
| Phase Resolved | PHASE-XX or — |
| Description | Full reproduction steps |
| Expected Behavior | What should happen |
| Actual Behavior | What happens instead |
| Device / API Level | Device and Android version |
| Related Feature | F-XXX |
| Root Cause | (when known) |
| Fix Applied | (description of fix) |
| Commit | (git commit reference when applicable) |
