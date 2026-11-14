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

// IntelliJ run configurations. unimined's genIntellijRuns (run on sync) names the module after the Gradle path, e.g.
// Icarus.platforms.mc-1.21.1.fabric.main, but IntelliJ turns dots inside a project name into underscores
// (Icarus.platforms.mc-1_21_1.fabric.main), so every generated config says "Module not specified". Fix the names, and
// add a config for Fabric 26.3, whose unimined 2.x build generates none. That one runs the 26.3 build on its own
// (-p): run through the root build, Gradle would first configure every other target, about a minute.
fun fixIdeaRunConfigs() {
    val dir = rootDir.resolve(".idea/runConfigurations")
    if (!dir.isDirectory) return
    val renames = rootProject.allprojects.filter { "." in it.path }.map {
        val gradleName = rootProject.name + it.path.replace(":", ".")
        val ideaName = rootProject.name + it.path.split(":").joinToString(".") { part -> part.replace(".", "_") }
        "name=\"$gradleName." to "name=\"$ideaName."
    }
    dir.listFiles { f -> f.name.endsWith(".xml") }?.forEach { file ->
        val text = file.readText()
        val fixed = renames.fold(text) { acc, (from, to) -> acc.replace(from, to) }
        if (fixed != text) file.writeText(fixed)
    }
    dir.resolve("Icarus_26_3_Fabric_Client.xml").writeText("""
        <component name="ProjectRunConfigurationManager">
          <configuration default="false" name=":platforms:mc-26.3:fabric Minecraft Client (testmod)" type="GradleRunConfiguration" factoryName="Gradle">
            <ExternalSystemSettings>
              <option name="externalProjectPath" value="${'$'}PROJECT_DIR${'$'}" />
              <option name="externalSystemIdString" value="GRADLE" />
              <option name="scriptParameters" value="-p platforms/mc-26.3" />
              <option name="taskNames">
                <list>
                  <option value=":fabric:runTestmodClient" />
                </list>
              </option>
            </ExternalSystemSettings>
            <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
            <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
            <DebugAllEnabled>false</DebugAllEnabled>
            <RunAsTest>false</RunAsTest>
            <method v="2" />
          </configuration>
        </component>
    """.trimIndent() + "\n")
}

// a finalizer, not doLast: unimined adds its own actions after ours, and there is one generator per source set
val fixIdeaRunConfigsTask = tasks.register("fixIdeaRunConfigs") {
    doLast { fixIdeaRunConfigs() }
}

subprojects {
    tasks.matching { it.name == "genIntellijRuns" || it.name.endsWith("GenIntellijRuns") }.configureEach {
        finalizedBy(fixIdeaRunConfigsTask)
    }
}

// `./gradlew build` also builds the separate Minecraft 26.x build (see settings.gradle.kts)
tasks.register("build") {
    group = "build"
    description = "Builds every target, including the included Minecraft 26.x build."
    dependsOn(gradle.includedBuild("mc-26.3").task(":fabric:build"))
    dependsOn(gradle.includedBuild("mc-26.3").task(":neoforge:build"))
}
