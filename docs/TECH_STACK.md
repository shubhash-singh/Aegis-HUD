# TECH_STACK.md — AEGIS HUD Launcher

**Version:** 0.1.0-planning
**Last Updated:** 2026-10-25

---

## 1. Core Technologies

| Technology | Version | Rationale |
|---|---|---|
| Kotlin | 2.0.21 | Stable, current; required for Compose Compiler plugin |
| Jetpack Compose BOM | 2024.11.00 | Compose stable BOM; pins all Compose library versions |
| Android Gradle Plugin | 8.7.3 | Latest stable compatible with Gradle 8.9 and SDK 35 |
| Gradle Wrapper | 8.9 | Stable; matches AGP 8.7 requirements |
| Android compileSdk | 35 | Android 15 (latest stable API) |
| Android targetSdk | 35 | Targets Android 15 |
| Android minSdk | 26 | Android 8.0 Oreo; covers 97%+ of active devices |
| Java source compatibility | 17 | Required for modern Kotlin and AGP |

---

## 2. AndroidX and Jetpack Libraries

| Library | Version (via BOM or explicit) | Purpose |
|---|---|---|
| androidx.compose.bom | 2024.11.00 | Compose version alignment |
| androidx.compose.ui | (BOM) | Core Compose UI |
| androidx.compose.ui.graphics | (BOM) | Canvas and drawing APIs |
| androidx.compose.ui.tooling.preview | (BOM) | Android Studio previews |
| androidx.compose.material3 | (BOM) | Material 3 components (used selectively) |
| androidx.compose.animation | (BOM) | Compose animation APIs |
| androidx.activity.compose | 1.9.3 | `setContent`, `BackHandler`, `rememberLauncherForActivityResult` |
| androidx.lifecycle.viewmodel.compose | 2.8.7 | `viewModel()` in composables |
| androidx.lifecycle.runtime.compose | 2.8.7 | `collectAsStateWithLifecycle()` |
| androidx.navigation.compose | 2.8.4 | Navigation between screens |
| androidx.datastore.preferences | 1.1.1 | Small persistent settings (replaces SharedPreferences) |
| androidx.core.ktx | 1.13.1 | Kotlin extensions for Android core |

---

## 3. Testing Libraries

| Library | Version | Purpose |
|---|---|---|
| junit | 4.13.2 | Unit testing |
| androidx.test.ext.junit | 1.2.1 | AndroidX JUnit extension |
| androidx.compose.ui.test.junit4 | (BOM) | Compose UI testing |
| androidx.compose.ui.test.manifest | (BOM) | Test manifest configuration |
| androidx.espresso.core | 3.6.1 | Legacy UI test support (if needed) |
| kotlinx.coroutines.test | 1.9.0 | Testing coroutine-based code |

---

## 4. No-Add Dependencies (Explicitly Excluded)

| Library | Reason for Exclusion |
|---|---|
| Hilt / Dagger | Not justified for Phase 1-5 complexity; manual wiring used initially |
| Retrofit / OkHttp | No network API calls planned |
| Coil / Glide (for general images) | App icons loaded via PackageManager; no general image loading needed initially |
| Room | No structured local data in initial phases; DataStore sufficient |
| Accompanist | Most Accompanist libraries have been migrated into Compose core or AndroidX |
| Lottie | Custom Canvas animations preferred for HUD graphics |

---

## 5. Version Catalog (gradle/libs.versions.toml)

The project uses a Gradle Version Catalog (TOML format) for all dependency management.

```toml
[versions]
agp = "8.7.3"
kotlin = "2.0.21"
composeBom = "2024.11.00"
activityCompose = "1.9.3"
lifecycleViewmodelCompose = "2.8.7"
lifecycleRuntimeCompose = "2.8.7"
navigationCompose = "2.8.4"
datastorePreferences = "1.1.1"
coreKtx = "1.13.1"
junit = "4.13.2"
androidxTestExtJunit = "1.2.1"
espressoCore = "3.6.1"
coroutinesTest = "1.9.0"

[libraries]
# Compose BOM
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
# Compose UI
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
# Material 3
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
# Animation
androidx-compose-animation = { group = "androidx.compose.animation", name = "animation" }
# Activity
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
# Lifecycle
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeCompose" }
# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
# DataStore
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-test-ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidxTestExtJunit" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesTest" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

---

## 6. Technical Decisions Summary

| Decision | Choice | Rationale |
|---|---|---|
| Min SDK | 26 | Broad device coverage; API 26 required for some BatteryManager fields |
| Compose BOM | 2024.11.00 | Stable, tested set of Compose versions |
| No Coil initially | Use PackageManager APIs | App icons are loaded via `PackageManager.getApplicationIcon()`; no external image loading library needed in Phase 4 |
| Navigation | NavHost + named routes | Simple and standard for this app size |
| DataStore over SharedPreferences | DataStore Preferences | Type-safe, coroutines-based, no ANR risk |
| Single module | `app` module only | Premature modularization adds complexity without benefit at this stage |

---

## 7. Build Environment Compatibility

| Component | Confirmed Available |
|---|---|
| JDK 21 | Yes (OpenJDK 21.0.12) |
| Android SDK API 35 | Yes |
| Build Tools 35.0.0 | Yes |
| adb | Yes (v1.0.41) |
| System Gradle | No — always use ./gradlew |
| ANDROID_HOME env var | Not set; use local.properties |
