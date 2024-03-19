plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "androidx.compose.material3"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        consumerProguardFiles()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation("androidx.compose.material3:material3:1.2.1")
}