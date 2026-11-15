plugins {
    java
    id("xyz.wagyourtail.unimined")
}

base {
    archivesName.set("icarus-fabric-1.16.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// dev-only example mod, launched next to the library by runTestmodClient
val testmod: SourceSet by sourceSets.creating

sourceSets {
    main {
        // Minecraft-free core + the code shared by every 1.16.5 loader
        java.srcDir(rootProject.file("core/src/main/java"))
        resources.srcDir(rootProject.file("core/src/main/resources"))
        java.srcDir(file("../common/java"))
    }
    testmod.java.srcDir(rootProject.file("testmod/src/main/java"))
}

unimined.minecraft {
    version("1.16.5")

    mappings {
        mojmap()
    }

    fabric {
        loader("0.17.3")
    }
}

// not combineWith(main): that would put both in one fabric classpath group, i.e. one mod, and hide testmod
unimined.minecraft(testmod) {
    version("1.16.5")

    mappings {
        mojmap()
    }

    fabric {
        loader("0.17.3")
    }

    defaultRemapJar = false
}

testmod.compileClasspath += sourceSets.main.get().output
testmod.runtimeClasspath += sourceSets.main.get().output

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

// unimined 1.x adds the source set output to the jar a second time
tasks.jar { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
