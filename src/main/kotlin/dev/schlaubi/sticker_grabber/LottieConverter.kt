package dev.schlaubi.sticker_grabber

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.Duration.Companion.minutes

private val client = HttpClient {
    expectSuccess = true
    install(HttpTimeout) {
        requestTimeoutMillis = 2.minutes.inWholeMilliseconds
    }
}

suspend fun convertImage(source: ByteArray, format: ContentType): ByteArray = client.post(Config.LOTTIE_RENDERER_URL) {
    url {
        path("convert")
    }

    header(HttpHeaders.Accept, format)

    setBody(source)
}.body()
