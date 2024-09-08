plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.geekofia.chokidar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.geekofia.chokidar"
        minSdk = 24
        targetSdk = 34
        versionCode = 154
        versionName = "1.5.4-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.15:5173\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://your-production-url.com\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    applicationVariants.all {
        outputs.all {
            val appName = project.name
            val versionCode = versionCode
            val variantName = name
            val fileExtension = if (outputFile.name.endsWith(".apk")) "apk" else "aab"

            val newFileName = "${appName}-${variantName}-${versionCode}.${fileExtension}"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                newFileName
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.preference)
    implementation(libs.security.crypto)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.lifecycle.service)
    implementation(libs.glide)
    implementation(libs.play.services.mlkit.barcode.scanning)
    annotationProcessor(libs.compiler)
    implementation(libs.squareup.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.barcode.scanning)
    implementation(libs.play.services.code.scanner)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
