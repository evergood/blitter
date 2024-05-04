plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "es.soutullo.blitter"
    compileSdk = 34

    defaultConfig {
        applicationId = "es.soutullo.blitter"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.databinding:databinding-runtime:8.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2") {
        exclude(mapOf("group" to "com.android.support", "module" to "support-annotations"))
    }
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:2.0.4")
    implementation("com.android.support:design:28.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.android.support:preference-v7:28.0.0")
    implementation("com.github.sevar83:indeterminate-checkbox:1.0.5@aar")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    implementation("com.github.kobakei:MaterialFabSpeedDial:1.1.5")
    implementation("com.github.koliong:BubbleSeekBar:2.1.1")
    implementation("com.alvinhkh:TextDrawable:c1c2b5b")
    implementation("com.github.MrBIMC:MaterialSeekBarPreference:2.2.0")
    implementation("com.bignerdranch.android:recyclerview-multiselect:0.2")
    testImplementation("junit:junit:4.13.2")
}