plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val releaseStoreFilePath = providers.environmentVariable("AEGIS_RELEASE_STORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("AEGIS_RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("AEGIS_RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("AEGIS_RELEASE_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(
    releaseStoreFilePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { !it.isNullOrBlank() }

val aegisVersionCode = providers.gradleProperty("aegisVersionCode").orNull?.toIntOrNull() ?: 1
val aegisVersionName = providers.gradleProperty("aegisVersionName").orNull ?: "0.1.0-dev"

android {
    namespace = "dev.ragnar.aegis"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.ragnar.aegis"
        minSdk = 26
        targetSdk = 35
        versionCode = aegisVersionCode
        versionName = aegisVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseSigningConfig = if (hasReleaseSigning) {
        signingConfigs.create("release") {
            storeFile = file(requireNotNull(releaseStoreFilePath))
            storePassword = requireNotNull(releaseStorePassword)
            keyAlias = requireNotNull(releaseKeyAlias)
            keyPassword = requireNotNull(releaseKeyPassword)
        }
    } else {
        null
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = releaseSigningConfig
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM — pins all Compose library versions
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Material 3
    implementation(libs.androidx.compose.material3)

    // Animation
    implementation(libs.androidx.compose.animation)

    // Activity
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore Preferences (F-010 Customization)
    implementation(libs.androidx.datastore.preferences)

    // Core
    implementation(libs.androidx.core.ktx)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumentation testing
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
