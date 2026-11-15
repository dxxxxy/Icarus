plugins {
    `java-gradle-plugin`
}

repositories {
    maven("https://maven.wagyourtail.xyz/releases/")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // putting unimined on the buildSrc classpath makes it available to every project
    // (apply it with `id("xyz.wagyourtail.unimined")`, no version) and lets the
    // workarounds below shadow/patch it (see the classes in this source set)
    implementation("xyz.wagyourtail.unimined:xyz.wagyourtail.unimined.gradle.plugin:2.0.2")
}

gradlePlugin {
    plugins {
        create("argFileClasspath") {
            id = "icarus.argfile-classpath"
            implementationClass = "studio.dreamys.gradle.ArgFileClasspathPlugin"
        }
    }
}
