val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("com.saicone.rtag:rtag:1.5.10")
    implementation("com.saicone.rtag:rtag-item:1.5.10")
    implementation(project(mapOf("path" to ":mc_1_21_R3", "configuration" to "reobf")))
    implementation(project(mapOf("path" to ":mc_1_20_R3", "configuration" to "reobf")))
    implementation(project(mapOf("path" to ":mc_1_20_R1", "configuration" to "reobf")))
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.bukkit" && requested.name == "bukkit") {
                useVersion("1.21.4-R0.1-SNAPSHOT")
                useTarget("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
            }
        }
    }
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
//    relocate("redis.clients.jedis", "com.magicrealms.magiclib.libraries.jedis")
//    relocate("com.mysql", "com.magicrealms.magiclib.libraries.mysql")
//    relocate("com.mongodb", "com.magicrealms.magiclib.libraries.mongodb")
//    relocate("com.google", "com.magicrealms.magiclib.libraries.google")
//    relocate("com.saicone.rtag", "com.magicrealms.magiclib.libraries.saicone.rtag")
//    relocate("org.apache.commons.pool2", "com.magicrealms.magiclib.libraries.commons.pool2")
//    relocate("org.bson", "com.magicrealms.magiclib.libraries.libraries.bson")
//    relocate("org.jspecify.annotations", "com.magicrealms.magiclib.libraries.libraries.jspecify.annotations")
//    relocate("org.slf4j", "com.magicrealms.magiclib.libraries.libraries.jspecify.slf4j")
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