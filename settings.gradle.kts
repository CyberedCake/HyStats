pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }
    plugins {
        val egtVersion = "0.1.0" // should be whatever is displayed in above badge
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
    }
}

rootProject.name = "hystats"
