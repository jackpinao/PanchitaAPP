
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinx.kover)
}

// Configuración de Cobertura de Código (Kover 0.9.0)
kover {
    reports {
        total {
            filters {
                excludes {
                    // Ignorar clases de Inyección de Dependencias (Koin)
                    classes("*.di.*", "*ModuleKt*")
                    // Ignorar Actividades y código de UI puro
                    classes("*Activity*", "*ScreenKt*", "*Composable*")
                    // Ignorar clases generadas (Room, BuildConfigs, etc.)
                    classes("com.pinao.panchitaapp.data.source.local.entity.*")
                    classes("*.dto.*")
                    // También es buena práctica excluir el código autogenerado por Room/Hilt
                    classes("*_Impl*")
                    classes("*_ViewBinding*", "*BuildConfig*", "*_Factory*", "*_MembersInjector*", "*_**")
                    // Ignorar modelos de datos y estados
                    classes("*.domain.model.*", "*UiState*", "*Event*")
                    // Ignorar clases de Firebase/GMS generadas
                    packages("com.google.firebase.**", "com.google.android.gms.**")
                }
            }
            
            verify {
                rule {
                    // En Kover 0.9.0, minBound es la forma recomendada de establecer el límite mínimo
                    minBound(60)
                }
            }
        }
    }
}

// Cargar local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Tarea para generar kotzilla.json automáticamente
tasks.register("generateKotzillaJson") {
    val appId = localProperties.getProperty("KOTZILLA_APP_ID") ?: ""
    val keyId = localProperties.getProperty("KOTZILLA_KEY_ID") ?: ""
    val apiKey = localProperties.getProperty("KOTZILLA_API_KEY") ?: ""
    
    val jsonContent = """
    {
      "sdkVersion": "1.1.0",
      "keys": [
        {
          "appId": "$appId",
          "applicationPackageName": "com.pinao.panchitaapp",
          "keyId": "$keyId",
          "apiKey": "$apiKey"
        }
      ]
    }
    """.trimIndent()

    val outputFile = file("kotzilla.json")
    outputs.file(outputFile)
    
    doLast {
        if (appId.isNotEmpty()) {
            outputFile.writeText(jsonContent)
            println("Kotzilla JSON generado exitosamente.")
        } else {
            println("ADVERTENCIA: KOTZILLA_APP_ID no encontrado en local.properties")
        }
    }
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
        versionCode = 4
        versionName = "4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KOTZILLA_APP_ID", "\"${localProperties.getProperty("KOTZILLA_APP_ID") ?: ""}\"")
        buildConfigField("String", "KOTZILLA_KEY_ID", "\"${localProperties.getProperty("KOTZILLA_KEY_ID") ?: ""}\"")
        buildConfigField("String", "KOTZILLA_API_KEY", "\"${localProperties.getProperty("KOTZILLA_API_KEY") ?: ""}\"")
    }

    // Asegurar que el JSON se genere antes de compilar
    applicationVariants.all {
        val variantName = name.replaceFirstChar { it.uppercase() }
        val generateTask = tasks.named("generateKotzillaJson")
        tasks.named("pre${variantName}Build").configure {
            dependsOn(generateTask)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = false
        checkDependencies = true
        textReport = true
        textOutput = file("stdout")
    }
}

configurations.all {
    /*resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(libs.versions.kotlin.get())
        }
    }*/
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    targetCompatibility = "17"
    sourceCompatibility = "17"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
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
    implementation(libs.androidx.material3.windowSizeClass)

    implementation(libs.navigation.compose)
    implementation(libs.androidx.material)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.google.firebase.firestore)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.compose.material3)
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
    implementation(libs.androidx.work.runtime.ktx)

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
    testImplementation(libs.mockk)
    testImplementation(platform(libs.koin.bom))
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.junit4)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.hamcrest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_LOG_TIMES", "true")
}
