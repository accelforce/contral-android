package net.accelf.contral.core

import androidx.compose.runtime.staticCompositionLocalOf
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.plugin.PluginResolver

internal fun PluginResolver.corePlugin() {
    name = "Contral Core"
    version = 0 minor 1 patch 0
}

internal val LocalPlugins = staticCompositionLocalOf<List<Plugin>> { error("LocalPlugins is not set") }
