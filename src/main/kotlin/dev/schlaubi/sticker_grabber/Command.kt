package dev.schlaubi.sticker_grabber

import com.kotlindiscord.kord.extensions.commands.application.message.EphemeralMessageCommand
import com.kotlindiscord.kord.extensions.commands.application.message.EphemeralMessageCommandContext
import com.kotlindiscord.kord.extensions.components.ComponentContainer
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.ephemeralButton
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralMessageCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondPublic
import dev.kord.common.entity.MessageStickerType
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.actionRow
import dev.schlaubi.mikbot.plugin.api.util.discordError
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

private val rawFormatTypes = listOf(MessageStickerType.PNG, MessageStickerType.APNG)

suspend fun Extension.contextCommand() = ephemeralMessageCommand {
    name = "commands.context.copy.description"

    action {
        val stickers = targetMessages.flatMap(Message::stickers)
        if (stickers.isEmpty()) {
            respond {
                content = translate("commands.context.copy.not_found")
            }
            return@action
        }

        respond {
            coroutineScope {
                stickers.forEach {
                    launch {
                        val lottieUrl = "https://cdn.discordapp.com/stickers/${it.id}.json"

                        embed {
                            title = translate("commands.context.copy.title", arrayOf(it.id))
                            if (it.formatType in rawFormatTypes) {
                                description = "https://cdn.discordapp.com/stickers/${it.id}.png"
                            } else {
                                description = translate("commands.context.copy.lottie.description")

                                field {
                                    name = translate("commands.context.copy.lottie.url")
                                    value = lottieUrl
                                }
                            }
                        }

                        if (it.formatType == MessageStickerType.LOTTIE) {
                            components {
                                addConversionButton(
                                    "commands.context.copy.lottie.as_webp",
                                    ContentType("image", "webp"),
                                    lottieUrl
                                )

                                addConversionButton(
                                    "commands.context.copy.lottie.as_gif",
                                    ContentType.Image.GIF,
                                    lottieUrl
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

context(EphemeralMessageCommandContext, EphemeralMessageCommand)
        private suspend fun ComponentContainer.addConversionButton(name: String, format: ContentType, url: String) {
    ephemeralButton {
        bundle = this@EphemeralMessageCommand.resolvedBundle
        label = translate(name)

        action {
            val status = respondPublic { content = translate("commands.context.copy.lottie.converting") }
            val cdnResponse = kord.resources.httpClient.get(url)
            if (!cdnResponse.status.isSuccess()) discordError(translate("commands.context.copy.lottie_error"))
            val json = cdnResponse.body<ByteArray>()

            val image = convertImage(json, format)

            val followUp = status.edit {
                content = translate("commands.content.copy.lottie.done")
                addFile("sticker.${format.contentSubtype}", ByteArrayInputStream(image))
            }

            val message = followUp.channel.getMessage(followUp.id)

            followUp.edit {
                actionRow {
                    linkButton(message.attachments.first().url) {
                        label = translate("commands.context.copy.lottie.open_in_browser")
                    }
                }
            }
        }
    }
}
