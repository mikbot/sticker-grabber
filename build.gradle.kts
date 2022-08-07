import dev.schlaubi.mikbot.gradle.GenerateDefaultTranslationBundleTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Locale

plugins {
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    kotlin("jvm") version "1.7.10"
    id("dev.schlaubi.mikbot.gradle-plugin") version "2.4.1"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/service/local/repositories/snapshots/content/")
}

dependencies {
    @Suppress("DependencyOnStdlib")
    compileOnly(kotlin("stdlib-jdk8"))
    mikbot("dev.schlaubi", "mikbot-api", "3.5.1-SNAPSHOT")
    ksp("dev.schlaubi", "mikbot-plugin-processor", "2.2.0")
}

mikbotPlugin {
    description.set("Grab Discord Stickers as images")
    pluginId.set("sticker-grabber")
    bundle.set("sticker_grabber")
    provider.set("Schlaubi")
    license.set("MIT")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "18"
            freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
        }
    }

    val generateDefaultResourceBundle = task<GenerateDefaultTranslationBundleTask>("generateDefaultResourceBundle") {
        defaultLocale.set(Locale("en", "GB"))
    }

    assemblePlugin {
        dependsOn(generateDefaultResourceBundle)
    }
}
