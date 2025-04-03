rootProject.name = "MagicLib"
include(":paper")
include(":common")
include(":mod")
include(":mc_1_21_R3")
include(":mc_1_20_R3")
include(":mc_1_20_R1")
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

