// Minecraft 26.x (unobfuscated) is its own build on unimined 2.x: unimined 1.x, which the rest of Icarus uses,
// predates unobfuscated Minecraft. The root build includes this one, so `./gradlew build` still covers it.
pluginManagement {
    repositories {
        maven("https://maven.wagyourtail.xyz/releases/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "icarus-mc-26.3"

include("fabric", "neoforge")
