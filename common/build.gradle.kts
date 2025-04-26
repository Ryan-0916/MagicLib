tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("com.google.code.gson:gson:2.10.1")

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