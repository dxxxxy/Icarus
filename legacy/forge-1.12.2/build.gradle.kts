plugins {
    java
    id("xyz.wagyourtail.unimined")
}

base {
    archivesName.set("icarus-forge-1.12.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val icarusRoot = rootDir.parentFile

sourceSets.main {
    // the Minecraft-free core is compiled straight into the adapter jar
    java.srcDir(icarusRoot.resolve("core/src/main/java"))
    resources.srcDir(icarusRoot.resolve("core/src/main/resources"))
    // the example mod is compiled in for dev runs (FG2-era FML fails to construct mods from a second
    // dev source set) and stripped from the published jar below
    java.srcDir(icarusRoot.resolve("testmod/src/main/java"))
}

tasks.jar {
    exclude("studio/dreamys/test/**")
}

unimined.minecraft {
    version("1.12.2")

    mappings {
        searge()
        mcp("stable", "39-1.12")
    }

    minecraftForge {
        loader("14.23.5.2860")
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:1.18.42"
    compileOnly(lombok)
    annotationProcessor(lombok)
}
