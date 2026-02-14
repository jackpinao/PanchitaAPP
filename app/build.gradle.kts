
plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.kotlin.android) // No longer required in AGP 9.0
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.room)
    alias(libs.plugins.kotzilla)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.pinao.panchitaapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pinao.panchitaapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.kotzilla.sdk)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.navigation.compose)
    implementation(libs.androidx.material)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.google.firebase.firestore)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.firebase.auth)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(platform(libs.koin.annotations.bom))
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.runtime.livedata)
    implementation(libs.play.services.code.scanner)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    implementation(libs.splash.screen)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.androidx.compose.material.icons.extended)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.hamcrest)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.hamcrest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.truth)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_LOG_TIMES", "true")
}
