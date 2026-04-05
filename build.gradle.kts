buildscript {
    repositories {
        google()
        mavenCentral()                // essentiel pour toutes les dépendances AGP et Kotlin
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
