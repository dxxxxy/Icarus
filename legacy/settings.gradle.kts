// LaunchWrapper-era forge targets (1.7.10, 1.8.9, 1.12.2). This is a separate included build because
// unimined 2.0.2 does not handle FG2-era forge yet (1.7.10/1.8.9: forge classes missing from the dev jar,
// 1.12.2: MCP member names never applied); it stays on unimined 1.x.
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

include("forge-1.7.10", "forge-1.8.9", "forge-1.12.2")
