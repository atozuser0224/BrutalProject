import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.3"
  id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0" // Generates plugin.yml based on the Gradle config
  kotlin("plugin.serialization") version "1.9.23"

  // Shades and relocates dependencies into our plugin jar. See https://imperceptiblethoughts.com/shadow/introduction/
  id("com.gradleup.shadow") version "8.3.2"
  kotlin("jvm")
}

group = "io.papermc.paperweight"
version = "1.0.0-SNAPSHOT"
description = "Test plugin for paperweight-userdev"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}


 paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// 2)
// For 1.20.4 or below, or when you care about supporting Spigot on >=1.20.5
// Configure reobfJar to run when invoking the build task
/*
tasks.assemble {
  dependsOn(tasks.reobfJar)
}
 */

dependencies {
  paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
  // paperweight.foliaDevBundle("1.21.1-R0.1-SNAPSHOT")
  // paperweight.devBundle("com.example.paperfork", "1.21.1-R0.1-SNAPSHOT")

  // Shadow will include the runtimeClasspath by default, which implementation adds to.
  // Dependencies you don't want to include go in the compileOnly configuration.
  // Make sure to relocate shaded dependencies!
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.19.0")
  implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.19.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
  implementation(platform("com.intellectualsites.bom:bom-newest:1.49"))
  compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
  compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") {
    isTransitive = false // 종속성을 직접 추가하지 않음
  }
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
// https://mvnrepository.com/artifact/net.kyori/adventure-nbt
  implementation("net.benwoodworth.knbt:knbt:0.11.5")

}

tasks {
  compileJava {
    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }

  // Only relevant when going with option 2 above
  /*
  reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar = layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar")
  }
   */

  shadowJar {
    // helper function to relocate a package into our package
    fun reloc(pkg: String) = relocate(pkg, "io.papermc.paperweight.testplugin.dependency.$pkg")
    // relocate cloud and it's transitive dependencies
    reloc("io.leangen.geantyref")
  }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
bukkitPluginYaml {
  main = "io.papermc.paperweight.testplugin.TestPlugin"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.add("Author")
  apiVersion = "1.21"
  libraries = listOf(
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.19.0",
    "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.19.0",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1",
    "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1",
    "net.benwoodworth.knbt:knbt:0.11.5"
  )
}
repositories {
  mavenCentral()
  maven(url = "https://jitpack.io")
  maven { url = uri("https://maven.enginehub.org/repo/") }

}
kotlin {
  jvmToolchain(21)
}
