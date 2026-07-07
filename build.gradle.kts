// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}

allprojects {
    plugins.apply("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
}

tasks.getByPath("ktlintCheck").shouldRunAfter("ktlintFormat")
