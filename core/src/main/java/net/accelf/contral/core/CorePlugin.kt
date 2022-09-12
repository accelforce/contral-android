package net.accelf.contral.core

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder
import net.accelf.contral.api.ui.utils.staticCompositionLocalOf
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.core.pages.plugins.PluginsPage
import net.accelf.contral.core.pages.timelines.ListTimelinesPage
import net.accelf.contral.core.pages.timelines.ShowTimelinePage
import net.accelf.contral.core.pages.timelines.TimelineController

internal fun PluginResolver.corePlugin() {
    name = "Contral Core"
    version = 0 minor 7 patch 0

    addDatabase(LocalContralDatabase)

    addRoutes {
        composable("plugins") { PluginsPage() }
        composable("timelines") { ListTimelinesPage() }
        composable("timelines/{id}", listOf(navArgument("id") { type = NavType.LongType })) {
            val db = LocalContralDatabase.current
            val timelineController = LocalTimelineController.current
            var timeline by useState<Timeline?>(null)
            val id = it.arguments!!.getLong("id")

            LaunchedEffect(db.hashCode(), id) {
                val savedTimeline = db.savedTimelineDao().getSavedTimeline(id)!!
                timeline = timelineController.getTimeline(savedTimeline.params)
            }

            timeline?.let {
                ShowTimelinePage(
                    timeline = timeline!!,
                )
            }
        }
    }
}

internal val LocalPlugins by staticCompositionLocalOf<List<Plugin>>()
internal val LocalTimelineController by staticCompositionLocalOf<TimelineController>()
internal val LocalTimelineAdders by staticCompositionLocalOf<List<TimelineAdder>>()
internal val LocalContralDatabase by staticCompositionLocalOf<ContralDatabase>()
