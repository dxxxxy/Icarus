// unimined itself is on the classpath through buildSrc (see buildSrc/build.gradle.kts)

allprojects {
    group = "studio.dreamys"
    version = providers.gradleProperty("icarus_version").get()
}
