plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //Hilt
    alias(libs.plugins.dagger.hilt.android)
    //Annotation kapt
    alias(libs.plugins.kotlin.kapt)
    //Annotation ksp
    alias(libs.plugins.ksp)
    //navigation safe args
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.pinao.panchitaapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pinao.panchitaapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        javaCompileOptions {
//            annotationProcessorOptions {
//                arguments += ["room.schemaLocation:" "$projectDir/schemas".toString()]
//            }
//        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            applicationVariants.all { variant ->
//                variant.outputs.all {
//                    outputFileName.set("panchitaapp-${variant.name}.apk")
//                }
//            }
            applicationVariants.all {
                val variant = this
                variant.outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        val outputFileName =
                            "panchitaapp - ${variant.baseName} - ${variant.versionName} ${variant.versionCode}.apk"
                        println("OutputFileName: $outputFileName")
                        output.outputFileName = outputFileName
                    }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.9"
    }
    buildFeatures {
        compose = true
    }
}


tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}
kapt {
    correctErrorTypes = true
    arguments {
        arg("dagger.fastInit", "enabled")
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        arg("dagger.hilt.android.internal.projectType", "APP")
        arg("dagger.hilt.internal.useAggregatingRootProcessor", "true")
        arg(
            "kapt.kotlin.generated",
            layout.buildDirectory.dir("generated/source/kaptKotlin").get().asFile.absolutePath
        )
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //Navigation
    implementation(libs.navigation.compose)
    implementation(libs.androidx.material)

    //Room
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit.jupiter)
    //implementation(libs.play.services.ads.lite)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    //Dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //Koin android
//    implementation(libs.koin.android)
//    implementation(libs.koin.core)
//    implementation(libs.koin.compose)
//    implementation(libs.koin.coroutines)
//    implementation(libs.koin.compose.viewmodel)
//    implementation(libs.koin.android.compose)
    //implementation(libs.koin.annotation)
//    implementation(libs.koin.ksp.compiler)

    //LiveData
    implementation(libs.runtime.livedata)

    // QR
    implementation(libs.zxing.android.embedded)
    implementation(libs.core)

    //Grafic report
    //implementation(libs.mpandroidchart)

    //Admod
    //implementation(libs.play.services.ads)
    implementation(libs.firebase.analytics)

    //Library
    //implementation(libs.librery.pcs)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    //SplashScreen
    implementation(libs.splash.screen)
    //Corrutinas
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)

    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.hamcrest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.hamcrest)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}