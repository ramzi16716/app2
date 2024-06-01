plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.googleGmsGoogleServices)


}

android {
    namespace = "com.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-location-license:12.0.1")
    implementation("com.karumi:dexter:6.2.1")
    implementation ("com.google.zxing:core:3.3.3")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("com.google.firebase:firebase-auth:21.0.3")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation ("com.google.firebase:firebase-storage:20.0.3")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
// For Realtime Database
    implementation ("com.google.firebase:firebase-storage:20.0.0") // If you need to upload images
    implementation ("com.squareup.picasso:picasso:2.71828") // For loading images


}