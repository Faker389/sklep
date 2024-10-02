buildscript {
    repositories {
        google()
        mavenCentral()  // Ensure Maven Central is here
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Adjust based on your project
        classpath("com.google.gms:google-services:4.3.10")
    }
}
plugins{
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.android.application) apply false
}

