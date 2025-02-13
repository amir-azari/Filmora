plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.navigation.serialization)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.dagger.hilt.android)
    id("kotlin-parcelize")
}

android {
    namespace = "azari.amirhossein.filmora"
    compileSdk = 35

    defaultConfig {
        applicationId = "azari.amirhossein.filmora"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "DEBUG", "true")
        }

        release {
            buildConfigField("Boolean", "DEBUG", "false")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // OkHttp
    implementation(libs.okHttp)
    implementation(libs.logging.interceptor)
    // Gson
    implementation(libs.gson)
    // Coroutines
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    // DataStore
    implementation(libs.datastore.preferences)
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    //DynamicSizes
    implementation(libs.dynamicSizes)
    //Lottie
    implementation(libs.lottie)
    //Shimmer
    implementation(libs.shimmer)
    //Pagination:
    implementation(libs.androidx.paging.runtime)
    implementation(libs.carouselrecyclerview)
}
kapt {
    correctErrorTypes = true
}