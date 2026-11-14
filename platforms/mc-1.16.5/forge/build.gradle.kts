plugins {
    java
    id("xyz.wagyourtail.unimined")
}

base {
    archivesName.set("icarus-forge-1.16.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    maven("https://maven.minecraftforge.net/")
}

sourceSets {
    main {
        // Minecraft-free core + the code shared by every 1.16.5 loader
        java.srcDir(rootProject.file("core/src/main/java"))
        resources.srcDir(rootProject.file("core/src/main/resources"))
        java.srcDir(file("../common/java"))
        // the example mod is compiled in for dev runs and stripped from the published jar below
        java.srcDir(rootProject.file("testmod/src/main/java"))
    }
}

unimined.minecraft {
    version("1.16.5")

    mappings {
        mojmap()
    }

    minecraftForge {
        loader("36.2.42")
    }
}

tasks.jar {
    exclude("studio/dreamys/test/**")
    filesMatching("META-INF/mods.toml") {
        var inTestmod = false
        filter { line ->
            when {
                line.startsWith("# testmod-begin") -> { inTestmod = true; null }
                line.startsWith("# testmod-end") -> { inTestmod = false; null }
                inTestmod -> null
                else -> line
            }
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:${providers.gradleProperty("lombok_version").get()}"
    compileOnly(lombok)
    annotationProcessor(lombok)
}
