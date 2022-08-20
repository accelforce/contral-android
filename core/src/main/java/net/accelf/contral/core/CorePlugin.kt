package net.accelf.contral.core

import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.PluginResolver

internal fun PluginResolver.corePlugin() {
    name = "Contral Core"
    version = 0 minor 0 patch 0
}
