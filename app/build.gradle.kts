plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.androidxNavigationSafeargs)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vanphong.foodnfit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vanphong.foodnfit"
        minSdk = 29
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.gson)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kodein.di)
    kapt(libs.room.compiler)
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("org.kodein.di:kodein-di-framework-android-x:7.1.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.github.elsunhoty:Ruler-picker:1.0")
    implementation("com.google.android.libraries.healthdata:health-data-api:1.0.0-alpha01")
    implementation("com.github.mukeshsolanki.android-otpview-pinview:otpview:3.1.0")
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.kizitonwose:CalendarView:1.1.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation ("com.github.ArjunGupta08:Horizontal-CalendarDate-With-Click-listener:1.1.0")
    implementation ("com.github.hadibtf:SemiCircleArcProgressBar:1.1.1")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    implementation ("androidx.camera:camera-core:1.4.2")
    implementation ("androidx.camera:camera-camera2:1.4.2")
    implementation ("androidx.camera:camera-lifecycle:1.4.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("io.github.ShawnLin013:number-picker:2.4.13")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.android.gms:play-services-fitness:21.2.0")
    implementation ("androidx.work:work-runtime:2.7.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
}