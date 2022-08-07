package dev.schlaubi.sticker_grabber

import dev.schlaubi.mikbot.plugin.api.EnvironmentConfig
import io.ktor.http.*

object Config : EnvironmentConfig() {
    val LOTTIE_RENDERER_URL by getEnv(transform = ::Url)
}
