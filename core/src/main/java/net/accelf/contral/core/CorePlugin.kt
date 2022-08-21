package net.accelf.contral.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.compose.composable
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.core.pages.Greeting
import net.accelf.contral.core.pages.navigator.NavigatorPage
import net.accelf.contral.core.pages.plugins.PluginsPage

internal fun PluginResolver.corePlugin() {
    name = "Contral Core"
    version = 0 minor 2 patch 0

    addRoutes {
        composable("navigator") { NavigatorPage() }
        composable("greetings") { Greeting(name = "Contral") }
        composable("plugins") { PluginsPage() }
    }
}

internal val LocalPlugins = staticCompositionLocalOf<List<Plugin>> { error("LocalPlugins is not set") }
