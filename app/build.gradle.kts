import java.util.Properties

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
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
    localProperties.load(localPropertiesFile.inputStream())
}
val tmdbApiKey = localProperties.getProperty("TMDB_API_KEY", "")

android {
    namespace = "azari.amirhossein.filmora"
    compileSdk = 35


    defaultConfig {
        applicationId = "azari.amirhossein.filmora"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildTypes {
            debug {
                applicationIdSuffix = "azari.amirhossein.filmora.debug" // نسخه جانبی
            }
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            type = "String",
            name = "TMDB_API_KEY",
            value = "\"$tmdbApiKey\""
        )
    }


    buildTypes {

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
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

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
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
    implementation(libs.androidx.hilt.navigation.fragment)

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

    implementation(libs.flexbox)
    implementation(libs.simpleratingbar)
    implementation(libs.androidx.browser)

}
kapt {
    correctErrorTypes = true
}