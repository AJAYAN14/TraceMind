import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val signingProperties = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}

android {
    namespace = "com.jian.tracemind"
    compileSdk = 37

    signingConfigs {
        create("release") {
            val storeFilePath = signingProperties.getProperty("tracemind.storeFile")
                ?: "keys/tracemind"
            val storePassword = signingProperties.getProperty("tracemind.storePassword")
                ?: error("Missing tracemind.storePassword in local.properties")
            val keyAlias = signingProperties.getProperty("tracemind.keyAlias")
                ?: error("Missing tracemind.keyAlias in local.properties")
            val keyPassword = signingProperties.getProperty("tracemind.keyPassword")
                ?: storePassword

            storeFile = rootProject.file(storeFilePath)
            this.storePassword = storePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }

    defaultConfig {
        applicationId = "com.jian.tracemind"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:coreUi"))
    implementation(project(":core:coreDomain"))
    implementation(project(":core:coreData"))
    implementation(project(":feature:featureHome"))
    implementation(project(":feature:featureInsights"))
    implementation(project(":feature:featureFolder"))
    implementation(project(":feature:featureEditor"))
    implementation(project(":feature:featureProfile"))
    implementation(project(":feature:featureSearch"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}