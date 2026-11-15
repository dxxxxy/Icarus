plugins {
    java
    id("xyz.wagyourtail.unimined")
}

base {
    archivesName.set("icarus-forge-1.8.9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

sourceSets.main {
    // the Minecraft-free core is compiled straight into the adapter jar
    java.srcDir(rootProject.file("core/src/main/java"))
    resources.srcDir(rootProject.file("core/src/main/resources"))
    // the example mod is compiled in for dev runs (FG2-era FML fails to construct mods from a second
    // dev source set) and stripped from the published jar below
    java.srcDir(rootProject.file("testmod/src/main/java"))
}

tasks.jar {
    exclude("studio/dreamys/test/**")
}

unimined.minecraft {
    version("1.8.9")

    mappings {
        searge()
        mcp("stable", "22-1.8.9")
    }

    minecraftForge {
        loader("11.15.1.2318-1.8.9")
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:${providers.gradleProperty("lombok_version").get()}"
    compileOnly(lombok)
    annotationProcessor(lombok)
}
