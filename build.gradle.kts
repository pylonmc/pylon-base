plugins {
    java
    idea
    id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "io.github.pylonmc"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    // TODO: add pylon-core once BS done
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.shadowJar {
    mergeServiceFiles()

    fun doRelocate(lib: String) {
        relocate(lib, "io.github.pylonmc.pylon.base.shadowlibs.$lib")
    }

    archiveBaseName = project.name
    archiveClassifier = null
}

bukkit {
    name = project.name
    main = "io.github.pylonmc.pylon.base.PylonBase"
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("pylon-core")
}

tasks.runServer {
    maxHeapSize = "4G"
    minecraftVersion("1.21.4")
}