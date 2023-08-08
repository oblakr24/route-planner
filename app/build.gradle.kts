import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.rokoblak.routeplanner"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rokoblak.routeplanner"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        var hasApiKeys = false
        if (project.rootProject.file("local.properties").exists()) {
            val prop = Properties().apply {
                load(FileInputStream(File(rootProject.rootDir, "local.properties")))
            }
            val mapsApiKey = prop.getProperty("MAPS_API_KEY", "") ?: ""
            manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

            val geoapifyApiKey = prop.getProperty("GEOAPIFY_API_KEY", "") ?: ""
            buildConfigField("String", "GEOAPIFY_API_KEY", "\"" + geoapifyApiKey + "\"")

            hasApiKeys = mapsApiKey.isNotBlank() && geoapifyApiKey.isNotBlank()
        }
        buildConfigField("Boolean", "HAS_API_KEYS", hasApiKeys.toString())

        testInstrumentationRunner = "com.rokoblak.routeplanner.CustomTestRunner"

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
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
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

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.1")
    // Compose constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    // Compose extended material icons
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    // Compose Shimmer
    implementation("com.valentinilk.shimmer:compose-shimmer:1.0.5")
    // Compose Navigation
    val composeHiltNavVersion = "1.0.0"
    val navVersion = "2.6.0"
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$composeHiltNavVersion")
    // Maps Compose
    implementation("com.google.maps.android:maps-compose:2.12.0")
    implementation("com.google.maps.android:maps-compose-utils:2.12.0")
    implementation("com.google.maps.android:maps-compose-widgets:2.12.0")

    // Hilt
    val hiltVersion = "2.46.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // KotlinX immutable collections
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    // Java8 desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Hilt testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Turbine
    testImplementation("app.cash.turbine:turbine:0.13.0")

    implementation("androidx.tracing:tracing:1.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    androidTestUtil("androidx.test:orchestrator:1.4.2")

    testImplementation("io.mockk:mockk:1.13.5")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
    api("androidx.arch.core:core-testing:2.2.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

apply {
    plugin("kotlinx-serialization")
}