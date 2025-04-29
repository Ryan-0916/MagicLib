plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        compileOnly("org.apache.commons:commons-lang3:3.8.1")
        compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")

        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
        testCompileOnly("org.projectlombok:lombok:1.18.24")
    }

    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
        mavenLocal()
        mavenCentral()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* PacketEvents */
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
    }

    tasks.processResources {
        filteringCharset = "UTF-8"
        filesMatching(arrayListOf("plugin.yml")) {
            expand(
                Pair("projectVersion", rootProject.properties["projectVersion"]),
            )
        }
    }

}