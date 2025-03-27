import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.maps.secret)

}

val localProperties = Properties().apply {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        load(propsFile.inputStream())
    }
}

val apiKey = localProperties.getProperty("MAPS_KEY").orEmpty()

android {
    namespace = "com.eminesa.getmylocationapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eminesa.getmylocationapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            buildConfigField("String", "MAPS_KEY", apiKey)
        }
        release {
            buildConfigField("String", "MAPS_KEY", apiKey)

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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


    //google maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

}