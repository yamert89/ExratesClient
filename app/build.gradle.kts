plugins{
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

val kotlinVersion = "1.3.61"
android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.2"
    defaultConfig {
        applicationId = "ru.exrates.mobile"
        minSdkVersion(26)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_7
        targetCompatibility = JavaVersion.VERSION_1_7
    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies() {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("com.android.support:support-annotations:28.0.0")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
    implementation("com.squareup.retrofit2:converter-jackson:2.6.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.google.android.material:material:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")
    //implementation("com.github.AnyChart:AnyChart-Android:1.1.2")
    compile("com.github.lecho:hellocharts-library:1.5.8@aar")
    //compile("com.github.lecho:hellocharts-android:v1.5.8")


}





