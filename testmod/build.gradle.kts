// The example mod's shared code. Platforms compile these sources into their own dev-only `testmod`
// source set (rather than depending on this project) so the pages live in the test mod's own output,
// exactly like a real mod's pages would, which is where Icarus scans for them.
plugins {
    java
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(project(":core"))
    testRuntimeOnly("com.google.code.gson:gson:2.2.4")
}

// Renders a theme's menu headlessly for comparison with the reference footage:
// ./gradlew :testmod:snapshot -Pargs="skeet 1.25 4 build/snap.png"
tasks.register<JavaExec>("snapshot") {
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("studio.dreamys.test.snapshot.Snapshot")
    args = (project.findProperty("args") as String? ?: "skeet 1.25 0 build/snapshot.png").split(" ")
    workingDir = projectDir
    systemProperty("java.awt.headless", "true")
}
