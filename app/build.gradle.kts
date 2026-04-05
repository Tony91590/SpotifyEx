import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Random

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

// 🔹 Git info
val gitCommitHashProvider = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
    workingDir = rootProject.rootDir
}.standardOutput.asText!!

val gitCommitDateProvider = providers.exec {
    commandLine("git", "log", "-1", "--format=%cd", "--date=format:%y%m%d")
    workingDir = rootProject.rootDir
}.standardOutput.asText!!

// 🔹 Génération dynamique du package name
private val seed = (project.properties["PACKAGE_NAME_SEED"] as? String ?: "0").toLong()
private val myPackageName = genPackageName(seed)

private fun genPackageName(seed: Long): String {
    val ALPHA = "abcdefghijklmnopqrstuvwxyz"
    val ALPHADOTS = "$ALPHA....."

    val random = Random(seed)
    val len = 5 + random.nextInt(15)
    val builder = StringBuilder(len)
    var prev = 0.toChar()
    repeat(len) { i ->
        val next = if (prev == '.' || i == 0 || i == len - 1) {
            ALPHA[random.nextInt(ALPHA.length)]
        } else {
            ALPHADOTS[random.nextInt(ALPHADOTS.length)]
        }
        builder.append(next)
        prev = next
    }
    if (!builder.contains('.')) {
        val idx = random.nextInt(len - 2)
        builder[idx + 1] = '.'
    }
    return builder.toString()
}

// 🔹 Android configuration
android {
    namespace = "io.github.chsbuffer.revancedxposed"

    compileSdk = 35
    defaultConfig {
        applicationId = myPackageName
        minSdk = 27
        targetSdk = 34
        versionCode = 33
        versionName = gitCommitDateProvider.get().trim()
        buildConfigField("String", "COMMIT_HASH", "\"${gitCommitHashProvider.get().trim()}\"")
    }

    flavorDimensions += "abi"
    productFlavors {
        create("universal") { dimension = "abi" }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "RVX Spotify (Test)")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes.addAll(listOf("META-INF/**", "**.bin"))
    }

    lint {
        checkReleaseBuilds = false
    }
}

// 🔹 Kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xno-param-assertions",
            "-Xno-receiver-assertions",
            "-Xno-call-assertions"
        )
        jvmTarget = JvmTarget.JVM_17
    }
}

// 🔹 Dependencies
dependencies {
    implementation("com.google.flatbuffers:flatbuffers-java:23.5.26")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
    compileOnly("de.robv.android.xposed:api:82")
}
