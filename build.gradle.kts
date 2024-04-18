buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.google.services)
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
//    id("androidx.navigation.safeargs") version "2.7.7" apply false
}