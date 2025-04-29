val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
    implementation(project(mapOf("path" to ":mc_1_21_R3", "configuration" to "reobf")))
    implementation(project(mapOf("path" to ":mc_1_20_R3", "configuration" to "reobf")))
    implementation(project(mapOf("path" to ":mc_1_20_R1", "configuration" to "reobf")))
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
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