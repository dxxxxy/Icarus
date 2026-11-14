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
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "Icarus"

include("core", "testmod")
include("platforms:mc-1.21.1:fabric", "platforms:mc-1.21.1:neoforge")

// LWJGL2-era forge targets live in their own build (unimined 1.x), see legacy/settings.gradle.kts
includeBuild("legacy")
