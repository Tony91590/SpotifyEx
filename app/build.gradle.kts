import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Random

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
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
    for (i in 0 until len) {
        val next = if (prev == '.' || i == 0 || i == len - 1) ALPHA[random.nextInt(ALPHA.length)] else ALPHADOTS[random.nextInt(ALPHADOTS.length)]
        builder.append(next)
        prev = next
    }
    if (!builder.contains('.')) {
        builder[random.nextInt(len - 2) + 1] = '.'
    }
    return builder.toString()
}

// 🔹 Android
android {
android {
    namespace = "io.github.chsbuffer.revancedxposed"

    defaultConfig {
        applicationId = myPackageName
        versionCode = 33
        versionName = gitCommitDateProvider.get().trim()
        buildConfigField("String", "COMMIT_HASH", "\"${gitCommitHashProvider.get().trim()}\"")
    }

    flavorDimensions += "abi"
    productFlavors {
        create("universal") { dimension = "abi" }
    }

    packaging.resources {
        excludes.addAll(listOf("META-INF/**", "**.bin"))  // ✅ correction ici
    }

    buildFeatures.buildConfig = true

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

    lint { checkReleaseBuilds = false }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

// 🔹 Tests
tasks.withType<Test> {
    useJUnitPlatform()
}

// 🔹 Dépendances
dependencies {
    implementation("com.google.flatbuffers:flatbuffers-java:23.5.26")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("io.github.skylot:jadx-core:1.4.7")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")

    compileOnly("de.robv.android.xposed:api:82")
}

// 🔹 Android Components
androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.packaging.resources.excludes.add("kotlin/**")
    }
}
