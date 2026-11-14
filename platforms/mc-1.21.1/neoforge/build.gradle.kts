plugins {
    java
    id("xyz.wagyourtail.unimined")
    id("icarus.argfile-classpath")
}

base {
    archivesName.set("icarus-neoforge-1.21.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21) //unimined may compile some envs with the daemon's JDK; pin the bytecode level
}

unimined {
    // compile and launch with the java toolchain above instead of the JDK running gradle
    useToolchains = true

    minecraft {
        version = "1.21.1"
        loader("neoforge", "21.1.266")
        mappings {
            mojmap()
        }
    }
}

sourceSets {
    main {
        // Minecraft-free core + the code shared by every 1.21.1 loader
        java.srcDir(rootProject.file("core/src/main/java"))
        resources.srcDir(rootProject.file("core/src/main/resources"))
        java.srcDir(file("../common/java"))
        // the example mod is compiled in for dev runs and stripped from the published jar below: neoforge's
        // userdev launch setup only applies to runs named exactly "client"/"server", so a separate testmod
        // env/project can't be launched properly
        java.srcDir(rootProject.file("testmod/src/main/java"))
    }
}

tasks.jar {
    exclude("studio/dreamys/test/**")
    filesMatching("META-INF/neoforge.mods.toml") {
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
    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:${providers.gradleProperty("lombok_version").get()}"
    compileOnly(lombok)
    annotationProcessor(lombok)
}

// unimined's neoforge descriptor floors every neoforge env at java 25; launch with this project's toolchain
val runLauncher = javaToolchains.launcherFor { languageVersion.set(java.toolchain.languageVersion) }
gradle.taskGraph.whenReady {
    allTasks.filterIsInstance<xyz.wagyourtail.unimined.runs.MinecraftRunTask>().filter { it.project == project }.forEach {
        it.javaExecutable.set(runLauncher.map { l -> l.executablePath.asFile.absolutePath })
    }
}
