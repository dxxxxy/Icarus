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
