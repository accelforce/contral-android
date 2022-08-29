package net.accelf.contral.api.plugin

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.navigation.NavGraphBuilder
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder

class PluginResolver(
    private val id: String,
) {
    var name: String? = null
    var version: Version? = null

    private val dependencies = mutableMapOf<String, MinorVersion>()
    fun require(name: String, version: MinorVersion) {
        dependencies[name]?.let { old ->
            if (old.major != version.major) {
                error("Two duplicated dependencies for $id requires different major version: $old and $version")
            }

            if (old.minor >= version.minor) {
                return
            }
        }
        dependencies[name] = version
    }

    private val injects = mutableListOf<@Composable () -> ProvidedValue<*>>()
    fun inject(getValue: @Composable () -> ProvidedValue<*>) {
        injects.add(getValue)
    }

    private val routeRenderers = mutableListOf<(NavGraphBuilder).() -> Unit>()
    fun addRoutes(routeRenderer: (NavGraphBuilder).() -> Unit) {
        routeRenderers.add(routeRenderer)
    }

    fun interface AddTimelineScope {
        fun addTimeline(timeline: Timeline)
    }

    private val timelineRenderers = mutableListOf<@Composable TimelineRenderer>()
    fun addTimelines(timelineRenderer: @Composable TimelineRenderer) {
        timelineRenderers.add(timelineRenderer)
    }

    private val timelineAdders = mutableListOf<TimelineAdder>()
    fun addTimelineAdder(
        render: @Composable () -> Unit,
        onClick: ((String) -> Unit) -> Unit,
    ) {
        timelineAdders.add(TimelineAdder(render, onClick))
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun build() = Plugin(
        id = id,
        name = name ?: id,
        version = version ?: error("Plugin version for $id not defined"),
        dependencies = dependencies,
        injects = injects,
        renderRoutes = {
            routeRenderers.forEach { it.invoke(this) }
        },
        renderTimelines = {
            timelineRenderers.forEach { it.invoke(this) }
        },
        timelineAdders = timelineAdders,
    )
}

typealias TimelineRenderer = (PluginResolver.AddTimelineScope).() -> Unit
