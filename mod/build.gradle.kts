plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.4:fat")
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.4-R0.1-SNAPSHOT")
    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")
    compileOnly("net.fabricmc:sponge-mixin:0.15.2+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

artifacts {
    archives(tasks.shadowJar)
}

tasks {
    shadowJar {
        archiveClassifier = ""
        archiveFileName = "${rootProject.name}-bukkit-mod-${rootProject.properties["projectVersion"]}.jar"
        destinationDirectory.set(file("$rootDir/target"))
    }
}
