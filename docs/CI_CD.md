# CI/CD Release Pipeline

Every push to `production` runs `.github/workflows/release-production.yml`.
The workflow tests and lints the project, builds a uniquely versioned signed release APK, verifies
its signature, uploads it as a workflow artifact, and attaches the APK plus SHA-256 checksum to a
GitHub Release.

## Required GitHub Actions Secrets

Create these repository secrets before the first `production` push:

| Secret | Value |
|---|---|
| `AEGIS_RELEASE_KEYSTORE_BASE64` | Base64-encoded release `.jks` or `.keystore` file |
| `AEGIS_RELEASE_STORE_PASSWORD` | Keystore password |
| `AEGIS_RELEASE_KEY_ALIAS` | Signing-key alias |
| `AEGIS_RELEASE_KEY_PASSWORD` | Signing-key password |

On Linux, encode a keystore without line wrapping:

```bash
base64 -w 0 aegis-release.jks
```

Do not commit a keystore, its passwords, or its base64 representation. The workflow writes the
decoded keystore only to the ephemeral GitHub Actions runner. It sets the APK `versionCode` to the
GitHub Actions run number and `versionName` to `0.1.0-production.<run-number>`, ensuring releases
are installable upgrades rather than same-version replacements.

## Release Output

Each production push creates:

- `app-release.apk` — signed, verified Android release APK.
- `app-release.apk.sha256` — checksum for integrity verification.
- A GitHub Release tagged `production-<run-number>-<short-sha>`.
