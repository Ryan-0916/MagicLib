plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val projectVersion : String by project
val projectGroup : String by project

allprojects {

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
        testCompileOnly("org.projectlombok:lombok:1.18.24")

        implementation("redis.clients:jedis:3.7.0")
        implementation("org.mongodb:bson:3.12.11")
        implementation("org.mongodb:mongodb-driver:3.12.11")
        implementation("org.mongodb:mongodb-driver-core:3.12.11")
        implementation("mysql:mysql-connector-java:8.0.32")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    if ("paper" == project.name) {

        tasks.shadowJar {
            destinationDirectory.set(file("$rootDir/target"))
            archiveClassifier.set("")
            archiveFileName.set("MagicLib-${projectVersion}.jar")
        }

        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    groupId = "com.magicrealms.magiclib"
                    artifactId = "MagicLib"
                    version = projectVersion
                    artifact(tasks.shadowJar)
                }
            }
            repositories {
                mavenLocal()
            }
        }
    }
}