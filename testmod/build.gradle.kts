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
}
