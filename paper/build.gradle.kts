tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

dependencies {
    implementation(project(":common"))
    implementation(project(mapOf("path" to ":mc_1_20_R3")))
    compileOnly("org.projectlombok:lombok:1.18.24")
    compileOnly(files("libs/purpur-1.20.4.jar"))
}