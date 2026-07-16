plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jlleitschuh.gradle.ktlint")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21,
        )
    }
}

android {
    namespace = "dev.arkbuilders.rate"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.arkbuilders.rate"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 9
        versionName = "2.1.2"
        setProperty("archivesBaseName", "ark-rate")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("testRelease") {
            storeFile = project.rootProject.file("keystore.jks")
            storePassword = "sw0rdf1sh"
            keyAlias = "ark-builders-test"
            keyPassword = "rybamech"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            addManifestPlaceholders(
                mapOf(
                    "appLabel" to "@string/app_name_debug",
                ),
            )
        }

        release {
            postprocessing {
                isRemoveUnusedCode = true
                isObfuscate = false
                isOptimizeCode = true
                proguardFiles("proguard-rules.pro")
            }
            signingConfig = signingConfigs.getByName("testRelease")

            addManifestPlaceholders(
                mapOf(
                    "appLabel" to "@string/app_name",
                ),
            )
        }
    }

    flavorDimensions += "publishTarget"
    productFlavors {
        create("github") {
            dimension = "publishTarget"
            buildConfigField("boolean", "GOOGLE_PLAY_BUILD", "false")
        }

        create("googleplay") {
            dimension = "publishTarget"
            buildConfigField("boolean", "GOOGLE_PLAY_BUILD", "true")
        }
    }

    productFlavors.all {
        val cryptoIcons = collectCurrencyIcons(project.rootDir.resolve("cryptoicons"))
        val fiatIcons = collectCurrencyIcons(project.rootDir.resolve("fiaticons"))
        val allIcons = (cryptoIcons + fiatIcons).distinct()

        buildConfigField(
            "String[]",
            "ICON_CODES",
            allIcons.joinToString(
                prefix = "new String[] {",
                postfix = "}",
                separator = ", ",
            ) {
                "\"$it\""
            },
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:presentation"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:quick"))
    implementation(project(":feature:quickwidget"))
    implementation(project(":feature:portfolio"))
    implementation(project(":feature:search"))
    implementation(project(":feature:settings"))
    implementation(project(":fiaticons"))
    implementation(project(":cryptoicons"))

    implementation(libs.ark.about)

    implementation(libs.androidx.ui)
    implementation(libs.navigation.compose)
    implementation(libs.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.concurrent.futures.ktx)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.timber)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.tagcloud)

    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)

    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    implementation(libs.qrgenerator)

    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.getByPath(":app:preBuild").dependsOn("ktlintCheck")

tasks.getByPath(":app:preBuild").dependsOn("ktlintFormat")

fun collectCurrencyIcons(moduleDir: File): List<String> {
    val drawableDir = moduleDir.resolve("src/main/res/drawable")
    if (!drawableDir.exists()) {
        return emptyList()
    }
    return drawableDir
        .listFiles()
        .map { it.nameWithoutExtension.uppercase() }
        .map {
            when {
                it == "CURR_TRY" -> "TRY"
                it.startsWith("COIN_") -> it.removePrefix("COIN_")
                else -> it
            }
        }
}
