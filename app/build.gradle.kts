plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Ensure this plugin is applied
}

android {
    namespace = "com.example.gymapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gymapp"
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

tasks.whenTaskAdded {
    if (name == "mergeReleaseResources" || name == "mergeDebugResources") {
        dependsOn("processReleaseGoogleServices")
    }
}


dependencies {
    implementation("com.google.android.material:material:1.2.1")
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10") // Correct Kotlin version
    implementation("com.google.firebase:firebase-auth:21.0.1") // Firebase Auth example
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
