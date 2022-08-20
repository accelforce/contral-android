package net.accelf.contral.mastodon

import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.PluginResolver

@Suppress("unused")
fun PluginResolver.mastodonPlugin() {
    name = "Mastodon"
    version = 0 minor 0 patch 0

    require("core", 0 minor 0)
}
