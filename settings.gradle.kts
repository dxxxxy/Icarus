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

rootProject.name = "Icarus"

include("core", "testmod")

// LaunchWrapper-era forge (LWJGL2)
include("platforms:forge-1.7.10", "platforms:forge-1.8.9", "platforms:forge-1.12.2")

// one project per loader; platforms/mc-<version>/common holds the code they share
include("platforms:mc-1.16.5:fabric", "platforms:mc-1.16.5:forge")
include("platforms:mc-1.20.1:fabric", "platforms:mc-1.20.1:forge")
include("platforms:mc-1.21.1:fabric", "platforms:mc-1.21.1:neoforge")
