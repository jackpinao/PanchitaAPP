// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    //Hilt
    alias(libs.plugins.dagger.hilt.android) apply false
    //Annotation kapt
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    //Annotation ksp
    alias(libs.plugins.ksp) apply false
    //navigation safe args
    alias(libs.plugins.navigation.safe.args) apply false
}