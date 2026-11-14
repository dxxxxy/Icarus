// unimined itself is on the classpath through buildSrc (see buildSrc/build.gradle.kts)

subprojects {
    group = "studio.dreamys"
    version = providers.gradleProperty("icarus_version").get()
}
