plugins {
    id("java")
}

group = "com.magicrealms.magiclib"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/InvSync-1.2.6.jar"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}