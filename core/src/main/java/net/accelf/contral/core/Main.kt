package net.accelf.contral.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.LocalRegisterTimeline
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.core.models.SavedTimeline
import net.accelf.contral.core.pages.timelines.TimelineController
import net.accelf.contral.core.plugin.resolvePlugins

@Composable
@OptIn(InternalComposeApi::class, ExperimentalMaterial3Api::class)
fun Main() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val plugins = remember { resolvePlugins(context) }

    CompositionLocalProvider(
        LocalPlugins provides plugins,
        LocalNavController provides navController,
    ) {
        val values = plugins.map(Plugin::injects).flatten().map { it.invoke() }.toTypedArray()
        currentComposer.startProviders(values)

        val timelineController = remember { TimelineController(plugins.map(Plugin::timelines).flatten()) }
        val timelineAdders = remember { plugins.map(Plugin::timelineAdders).flatten() }
        val contralDatabase = LocalContralDatabase.current
        suspend fun registerTimeline(timeline: Timeline) =
            contralDatabase.savedTimelineDao()
                .insert(SavedTimeline(params = timelineController.paramsToString(timeline)))
        CompositionLocalProvider(
            LocalTimelineController provides timelineController,
            LocalTimelineAdders provides timelineAdders,
            LocalRegisterTimeline provides ::registerTimeline,
        ) {
            ContralTheme {
                Scaffold(
                    topBar = {
                        AppBar(
                            navController = navController,
                        )
                    },
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "timelines",
                        modifier = Modifier.padding(paddingValues),
                    ) {
                        plugins.forEach { it.renderRoutes.invoke(this) }
                    }
                }
            }
        }

        currentComposer.endProviders()
    }
}
