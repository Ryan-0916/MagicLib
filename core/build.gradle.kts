val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

// val target = file("$rootDir/target")
val target = file("D:\\Minecraft\\Servers\\1.21.4\\Lobby\\plugins")
tasks.shadowJar {
    destinationDirectory.set(target)
    archiveClassifier.set("")
    archiveFileName.set("${rootProject.name}-${projectVersion}.jar")
}

publishing {
    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = projectGroup
            artifactId = projectArtifactId
            version = projectVersion
            from(components["shadow"])
        }
    }
}