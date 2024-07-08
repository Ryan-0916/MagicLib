tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

dependencies {
    implementation(project(":common"))
    implementation(project(mapOf("path" to ":mc_1_20_R3")))
    implementation(project(mapOf("path" to ":mc_1_20_R1")))
    compileOnly("org.projectlombok:lombok:1.18.24")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}