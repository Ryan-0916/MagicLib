rootProject.name = "MagicLib"
include(":common")
include(":velocity")
include(":bukkit")
include(":mod")
include(":core")
include(":mc_1_21_R3")
include(":mc_1_20_R3")
include(":mc_1_20_R1")
pluginManagement {
    repositories {
        gradlePluginPortal()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* PacketEvents */
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        /* PlaceholderApi */
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        /* Rtag */
        maven("https://jitpack.io")
    }
}
