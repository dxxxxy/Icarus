plugins {
    java
    id("xyz.wagyourtail.unimined")
    id("icarus.argfile-classpath")
}

base {
    archivesName.set("icarus-fabric-1.21.1")
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
        loader("fabric", "0.17.3")
        mappings {
            mojmap()
        }

        // dev-only example mod, launched next to the library by runTestmodClient
        sourceSet("testmod") {
            version = "1.21.1"
            loader("fabric", "0.17.3")
            mappings {
                mojmap()
            }
            combineWith("main")
        }
    }
}

sourceSets {
    main {
        // Minecraft-free core + the code shared by every 1.21.1 loader
        java.srcDir(rootProject.file("core/src/main/java"))
        resources.srcDir(rootProject.file("core/src/main/resources"))
        java.srcDir(file("../common/java"))
    }
    named("testmod") {
        java.srcDir(rootProject.file("testmod/src/main/java"))
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

dependencies {
    val lombok = "org.projectlombok:lombok:${providers.gradleProperty("lombok_version").get()}"
    compileOnly(lombok)
    annotationProcessor(lombok)
    "testmodCompileOnly"(lombok)
    "testmodAnnotationProcessor"(lombok)
}

// the example mod only ever runs from its dev classes; never package it
tasks.matching { it.name.contains("testmod", ignoreCase = true) && it.name.endsWith("Jar") }.configureEach {
    enabled = false
}

// Fabric treats the classpath directory holding fabric.mod.json as the mod's root. Keep each source set's
// classes and resources in one directory so the mod root contains its classes (Icarus scans it for @IPage),
// like it would inside a jar. (Loom does the same job with fabric.classPathGroups; unimined does not.)
for (name in listOf("main", "testmod")) {
    sourceSets.named(name) {
        output.setResourcesDir(java.destinationDirectory)
    }
}
tasks.named("compileTestmodJava") {
    dependsOn("processResources") //main's resources now share main's classes directory
}

// unimined's neoforge descriptor floors every neoforge env at java 25; launch with this project's toolchain
val runLauncher = javaToolchains.launcherFor { languageVersion.set(java.toolchain.languageVersion) }
gradle.taskGraph.whenReady {
    allTasks.filterIsInstance<xyz.wagyourtail.unimined.runs.MinecraftRunTask>().filter { it.project == project }.forEach {
        it.javaExecutable.set(runLauncher.map { l -> l.executablePath.asFile.absolutePath })
    }
}
