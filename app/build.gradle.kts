plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.roteiroviagem"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.roteiroviagem"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val lifecycle_version = "2.8.7"
    val room_version = "2.6.1" // Verifique a versão mais recente

    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    //botton navigation
    implementation("androidx.compose.material:material:1.7.8")

    //para o banco de dados.
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // Para compilar as anotações do Room
    implementation("androidx.room:room-ktx:$room_version") // Extensão para Kotlin Coroutines
    implementation ("androidx.navigation:navigation-compose:2.5.1")

// ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    implementation("androidx.navigation:navigation-compose:2.7.5") // ou a versão mais recente
    implementation("androidx.compose.material3:material3:1.2.0") // ou a versão mais recente
    implementation("androidx.compose.material:material:1.5.4") // ou a versão mais recente
    implementation("androidx.compose.ui:ui:1.5.4") // ou a versão mais recente
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4") // ou a versão mais recente
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4") // ou a versão mais recente
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4") // ou a versão mais recente
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4") // ou a versão mais recente

// ViewModel utilities for Compose
    implementation("androidx.compose.material:material-icons-extended:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}