plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.careercrew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.careercrew"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)
    implementation(libs.volley)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    implementation(libs.picasso)
    implementation(libs.work.runtime.ktx)
    implementation(libs.circleImageView)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.ui.database)
    implementation(libs.preference)
    implementation(libs.firebase.auth)
    implementation(libs.json)
    implementation (libs.guava)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}