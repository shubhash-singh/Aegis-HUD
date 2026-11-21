# BUG_TRACKER.md — AEGIS HUD Launcher

**Last Updated:** 2026-11-21

---

## Active Bugs

*No active defects recorded.*

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
