tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.apache.commons:commons-lang3:3.8.1")
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}