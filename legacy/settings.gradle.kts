// LWJGL2-era forge targets (1.7.10, 1.8.9). This is a separate included build because
// unimined 2.x does not support FG2-era userdev packages yet; it stays on unimined 1.x.
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

rootProject.name = "icarus-legacy"

include("forge-1.8.9")
