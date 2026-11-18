
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

import java.util.Base64

val keystoreFile = layout.buildDirectory.file("m-anak-pintar.jks").get().asFile

val keystoreBase64 = System.getenv("KEYSTORE_BASE64")
if (!keystoreBase64.isNullOrBlank()) {
    keystoreFile.writeBytes(Base64.getDecoder().decode(keystoreBase64))
}

android {
    namespace = "com.m.anakpintar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.m.anakpintar"
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }

    signingConfigs {
        create("permanent") {
            storeFile = keystoreFile
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("permanent")
        }
        release {
            signingConfig = signingConfigs.getByName("permanent")
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
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
}
