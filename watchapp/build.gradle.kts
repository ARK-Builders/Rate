plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
}
android {
    namespace = "dev.arkbuilders.rate.watchapp"
    compileSdk = libs.versions.compileSdk.get().toInt()

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "dev.arkbuilders.rate.watchapp"
        minSdk = libs.versions.minSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        val cryptoRatesLastModified =
            rootProject.file("core/data/src/main/assets/crypto-rates.json").lastModified()
        val fiatRatesLastModified =
            rootProject.file("core/data/src/main/assets/fiat-rates.json").lastModified()

        buildConfigField(
            "long",
            "CRYPTO_LAST_MODIFIED",
            cryptoRatesLastModified.toString(),
        )
        buildConfigField(
            "long",
            "FIAT_LAST_MODIFIED",
            fiatRatesLastModified.toString(),
        )

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun collectCurrencyIcons(moduleDir: File): List<String> {
    val drawableDir = moduleDir.resolve("src/main/res/drawable")
    return drawableDir.listFiles()?.map { it.nameWithoutExtension.uppercase() }
        ?.map { if (it == "curr_try") "try" else it } ?: emptyList()
}

dependencies {
    implementation(project(":core:db"))
    implementation(project(":core:data"))

    implementation(project(":cryptoicons"))
    implementation(project(":fiaticons"))
    implementation(project(":feature:quick"))
    implementation(project(":core:domain"))
    implementation(project(":core:presentation"))
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.play.services.wearable)
//    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation (libs.androidx.compose.navigation )// Or the latest version
    implementation("androidx.wear.compose:compose-material3:1.6.1") // Or current version

    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.material3)
    implementation(libs.navigation.compose)

    implementation(libs.androidx.wear.watchface)
    implementation(libs.androidx.wear.watchface.complications.data)
    implementation(libs.androidx.wear.watchface.complications.data.source)
    implementation(libs.androidx.wear.watchface.complications.data.source.ktx)
    implementation(libs.androidx.wear.watchface.complications.rendering)
    implementation(libs.androidx.wear.watchface.editor)
    implementation(libs.androidx.wear.watchface.style)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
