// Fichier build.gradle.kts du projet (niveau top)
buildscript {
    repositories {
        google()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        // jcenter() est obsolète et retiré
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

allprojects {
    repositories {
        google()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        // jcenter() retiré pour éviter les warnings et futures erreurs
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
