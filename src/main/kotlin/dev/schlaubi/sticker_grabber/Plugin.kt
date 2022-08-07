package dev.schlaubi.sticker_grabber

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.PluginWrapper

@PluginMain
class Plugin(wrapper: PluginWrapper) : Plugin(wrapper) {
    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::Module)
    }
}

class Module : Extension() {
    override val name: String = "sticker-grabber"
    override val bundle: String = "sticker_grabber"

    override suspend fun setup() {
        contextCommand()
    }
}
