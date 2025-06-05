import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    idea
    kotlin("jvm") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.freefair.lombok") version "8.13.1"
}

group = "io.github.pylonmc"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.xenondevs.xyz/releases")
}

val coreVersion = project.properties["pylon-core.version"] as String
val coreJarPath = project.findProperty("core-jar-path") as String?

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    if(coreJarPath is String) {
        compileOnly(files(coreJarPath))
    } else {
        compileOnly("io.github.pylonmc:pylon-core:$coreVersion")
    }
    implementation("xyz.xenondevs.invui:invui:1.45")
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
    name = "PylonBase"
    main = "io.github.pylonmc.pylon.base.PylonBase"
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("PylonCore")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

tasks.runServer {
    downloadPlugins {
        if(coreJarPath is String){
            copy {
                from(coreJarPath)
                into("run/plugins")
            }
        }
        else {
            github("pylonmc", "pylon-core", coreVersion, "pylon-core-$coreVersion.jar")
        }
    }
    maxHeapSize = "4G"
    minecraftVersion("1.21.4")
}