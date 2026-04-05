// Root build.gradle.kts

plugins {
    // Kotlin DSL plugins, version définie ici pour tous les modules
    kotlin("android") version "1.9.20" apply false
    kotlin("plugin.serialization") version "1.9.20" apply false
}

allprojects {
    repositories {
        google()       // Nécessaire pour les plugins Android
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
