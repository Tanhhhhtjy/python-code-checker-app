plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

apply(plugin = "dagger.hilt.android.plugin")

android {
    namespace = "com.example.codechecker"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.codechecker"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        // 添加Room的Schema导出
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }

    buildTypes {
        getByName("release") {
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
        freeCompilerArgs = listOf(
            "-Xjvm-default=all",  // 解决接口继承问题
            "-opt-in=kotlin.RequiresOptIn"  // 解决实验性API问题
        )
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    
    // Compose - 完全兼容的版本
    implementation("androidx.compose.ui:ui:1.1.1")
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.1")
    implementation("androidx.compose.ui:ui-graphics:1.1.1")
    implementation("androidx.compose.material:material-icons-extended:1.1.1")
    implementation("androidx.compose.material3:material3:1.0.0")

    // Compose扩展（解决 viewModel() 和状态管理）
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.1.1")

    // 导航（如果代码中使用）
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Kotlin标准库
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-compiler:2.38.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Room Database (新增)
    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    
    // DataStore (新增)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    
    // Coroutines (新增)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Lifecycle ViewModel KTX
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    
    // 测试
    //testImplementation("junit:junit:4.13.2")
    //androidTestImplementation("androidx.test.ext:junit:1.1.3")
    //androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")
    debugImplementation("androidx.compose.ui:ui-tooling:1.1.1")
}