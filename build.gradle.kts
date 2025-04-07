plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

val projectVersion : String by project
val projectGroup : String by project

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        compileOnly("org.apache.commons:commons-lang3:3.8.1")
        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
        testCompileOnly("org.projectlombok:lombok:1.18.24")
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    if ("paper" == project.name) {
        tasks.shadowJar {
            destinationDirectory.set(file("$rootDir/target"))
            archiveClassifier.set("")
            archiveFileName.set("${rootProject.name}-${projectVersion}.jar")
        }
    }
}