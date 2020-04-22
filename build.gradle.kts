import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    maven {
        url = uri("http://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
}
buildscript {
    repositories {
        dependencies {
            classpath(kotlin("gradle-plugin", version = "1.3.21"))
        }
    }
}
plugins {
    idea apply true
    kotlin("jvm") version "1.3.61"
    id("org.jetbrains.intellij") version "0.4.15"
    id("com.github.ben-manes.versions") version "0.21.0"
}
apply {
    plugin("java")
    plugin("kotlin")
    plugin("org.jetbrains.intellij")
}
tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.freeCompilerArgs += "-progressive"
    }
    patchPluginXml {
        changeNotes(project.property("changeNotes").toString().replace("\n", "<br>\n"))
    }
}
val ideaVersion : String by project;
intellij {
    version = ideaVersion
    sandboxDirectory = project.rootDir.canonicalPath + "/build/idea-sandbox"
    downloadSources = false
    updateSinceUntilBuild = false
    pluginName = name
    setPlugins(
            "com.jetbrains.php:193.5233.102",
            "CSS",
            "java",
            "java-i18n",
            "properties"
    )
}
