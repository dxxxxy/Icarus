plugins {
    `java-library`
}

base {
    archivesName.set("icarus-core")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    // every supported Minecraft version ships gson; 2.2.4 is the oldest (1.7.10/1.8.9)
    compileOnly("com.google.code.gson:gson:2.2.4")

    val lombok = "org.projectlombok:lombok:${providers.gradleProperty("lombok_version").get()}"
    compileOnly(lombok)
    annotationProcessor(lombok)

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.google.code.gson:gson:2.2.4")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("java.awt.headless", "true")
}
