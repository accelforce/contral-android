package net.accelf.contral.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder
import net.accelf.contral.core.pages.Greeting
import net.accelf.contral.core.pages.navigator.NavigatorPage
import net.accelf.contral.core.pages.plugins.PluginsPage
import net.accelf.contral.core.pages.timelines.ListTimelinesPage
import net.accelf.contral.core.pages.timelines.ShowTimeline

internal fun PluginResolver.corePlugin() {
    name = "Contral Core"
    version = 0 minor 5 patch 0

    addRoutes {
        composable("navigator") { NavigatorPage() }
        composable("greetings") { Greeting(name = "Contral") }
        composable("plugins") { PluginsPage() }
        composable("timelines") { ListTimelinesPage() }
        composable("timelines/{id}", listOf(navArgument("id") { type = NavType.IntType })) {
            val timelines = LocalTimelines.current
            val id = it.arguments!!.getInt("id")
            ShowTimeline(timelines[id])
        }
    }
}

internal val LocalPlugins = staticCompositionLocalOf<List<Plugin>> { error("LocalPlugins is not set") }
internal val LocalTimelines = staticCompositionLocalOf<List<Timeline>> { error("LocalTimelines is not set") }
internal val LocalTimelineAdders = staticCompositionLocalOf<List<TimelineAdder>> {
    error("LocalTimelineAdders is not set")
}
