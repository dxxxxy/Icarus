plugins {
    // unimined 1.x for every target; 2.x can't build FG2-era forge yet and has several windows bugs.
    // Revisit once 2.x matures.
    id("xyz.wagyourtail.unimined") version "1.4.1" apply false
}

allprojects {
    group = "studio.dreamys"
    version = providers.gradleProperty("icarus_version").get()
}

subprojects {
    // declared per project rather than in settings: unimined adds its own project repositories,
    // which would make gradle ignore the settings ones
    repositories {
        mavenCentral()
    }
}

// IntelliJ sync workaround. During an IDE sync (idea.sync.active), unimined 1.x adds a mappings jar to Fabric runtime
// classpaths as a java.nio.file.Path. IntelliJ walks classpath structure and treats any Iterable as a container; a
// one-segment Path iterates to itself, so the sync dies with StackOverflowErrors ("Multiple build operations failed.
// null null null"). Swap those Paths for Files. Remove once unimined or IntelliJ fixes it.
fun replacePaths(collection: Any?, seen: MutableSet<Any> = HashSet()) {
    if (collection == null || !seen.add(collection)) return
    when (collection) {
        is ConfigurableFileCollection -> {
            val from = collection.from.toList()
            if (from.any { it is java.nio.file.Path }) collection.setFrom(from.map { if (it is java.nio.file.Path) it.toFile() else it })
            from.forEach { replacePaths(it, seen) }
        }
        is org.gradle.api.internal.file.UnionFileCollection -> collection.sources.forEach { replacePaths(it, seen) }
    }
}

gradle.projectsEvaluated {
    rootProject.allprojects {
        extensions.findByType<SourceSetContainer>()?.forEach {
            replacePaths(it.compileClasspath)
            replacePaths(it.runtimeClasspath)
        }
    }
}

// `./gradlew build` also builds the separate Minecraft 26.x build (see settings.gradle.kts)
tasks.register("build") {
    group = "build"
    description = "Builds every target, including the included Minecraft 26.x build."
    dependsOn(gradle.includedBuild("mc-26.3").task(":fabric:build"))
    dependsOn(gradle.includedBuild("mc-26.3").task(":neoforge:build"))
}
