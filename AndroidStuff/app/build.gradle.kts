@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.asadshamsiev.spotifyexplorationapplication"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.asadshamsiev.spotifyexplorationapplication"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["redirectSchemeName"] = "com.asadshamsiev.spotifyexplorationapplication"
        manifestPlaceholders["redirectHostName"] = "callback"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(files("/Users/asadbekshamsiev/Downloads/spotify-app-remote-release-0.8.0.aar"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    /*** CUSTOM ***/
    // Spotify

    //noinspection UseTomlInstead
    implementation("com.spotify.android:auth:2.0.2")
    //noinspection UseTomlInstead
    implementation("com.android.volley:volley:1.2.1")
    //noinspection UseTomlInstead
    implementation("com.google.code.gson:gson:2.10.1")

    // https://github.com/adamint/spotify-web-api-kotlin
    //noinspection UseTomlInstead
    implementation("com.adamratzman:spotify-api-kotlin-core:4.0.2")

    // Flipper

    //noinspection UseTomlInstead
    debugImplementation("com.facebook.flipper:flipper:0.211.1")

    //noinspection UseTomlInstead
    debugImplementation("com.facebook.soloader:soloader:0.10.5")

    //noinspection UseTomlInstead
    releaseImplementation("com.facebook.flipper:flipper-noop:0.211.1")
}