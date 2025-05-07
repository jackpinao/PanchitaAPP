plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //Hilt
    //alias(libs.plugins.dagger.hilt.android)
    //Annotation kapt
    //alias(libs.plugins.kotlin.kapt)
    //Annotation ksp
    alias(libs.plugins.ksp)
    //navigation safe args
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
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
        //languageVersion = "1.9"
    }
    buildFeatures {
        compose = true
    }
}


tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}
//kapt {
//    correctErrorTypes = true
//    arguments {
//        arg("dagger.fastInit", "enabled")
//        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
//        arg("dagger.hilt.android.internal.projectType", "APP")
//        arg("dagger.hilt.internal.useAggregatingRootProcessor", "true")
//        arg(
//            "kapt.kotlin.generated",
//            layout.buildDirectory.dir("generated/source/kaptKotlin").get().asFile.absolutePath
//        )
//    }
//}

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
    //implementation(libs.play.services.ads.lite)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    //Dagger hilt
//    implementation(libs.hilt.android)
//    kapt(libs.hilt.android.compiler)
//    kapt(libs.hilt.compiler)
//    implementation(libs.hilt.navigation.compose)

    //Koin android
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    //implementation(libs.koin.android)
    implementation(platform(libs.koin.annotations.bom))
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
//    implementation(libs.koin.android)
//    implementation(libs.koin.core)
//    implementation(libs.koin.compose)
//    implementation(libs.koin.coroutines)
//    implementation(libs.koin.compose.viewmodel)
//    implementation(libs.koin.android.compose)
//    implementation(libs.koin.annotation)
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

    //Library
    //implementation(libs.librery.pcs)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    //SplashScreen
    implementation(libs.splash.screen)
    //Corrutinas
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    //Test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockito)
    androidTestImplementation(libs.hamcrest)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    //testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockito)
    //testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.hamcrest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter)
}


ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_LOG_TIMES", "true")
}


// Custom task to run the module check test
/*
tasks.register<Test>("runModuleCheckTest") {
    group = "verification"
    description = "Runs the module check test"

    val testTask = tasks.named<Test>("testDebugUnitTest").get()
    testClassesDirs = testTask.testClassesDirs
    classpath = testTask.classpath

    filter {
        includeTestsMatching("com.pinao.panchitaapp.ModuleCheck")
    }

    doLast {
        executeTests()
    }
}

tasks.matching { it.name.startsWith("assemble") }.configureEach {
    dependsOn("runModuleCheckTest")
}*/
