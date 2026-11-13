plugins {
    java
}

repositories {
    maven("https://maven.wagyourtail.xyz/releases/")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // putting unimined on the buildSrc classpath makes it available to every project
    // (apply it with `id("xyz.wagyourtail.unimined")`, no version) and lets the
    // processor override below shadow the builtin one via ServiceLoader ordering
    implementation("xyz.wagyourtail.unimined:xyz.wagyourtail.unimined.gradle.plugin:2.0.2")
}
