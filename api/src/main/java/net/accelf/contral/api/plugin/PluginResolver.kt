package net.accelf.contral.api.plugin

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.room.Room
import androidx.room.RoomDatabase
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder
import net.accelf.contral.api.timelines.TimelineItemAction
import kotlin.reflect.KClass

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

    inline fun <reified T : RoomDatabase> addDatabase(compositionLocal: ProvidableCompositionLocal<T>) {
        inject {
            val context = LocalContext.current
            val db = remember {
                Room.databaseBuilder(context, T::class.java, T::class.qualifiedName!!).build()
            }
            compositionLocal provides db
        }
    }

    private val routeRenderers = mutableListOf<(NavGraphBuilder).() -> Unit>()
    fun addRoutes(routeRenderer: (NavGraphBuilder).() -> Unit) {
        routeRenderers.add(routeRenderer)
    }

    private val timelines = mutableListOf<KClass<*>>()
    fun <T : Timeline> addTimeline(timelineParams: KClass<T>) {
        timelines.add(timelineParams)
    }

    private val timelineAdders = mutableListOf<TimelineAdder>()
    fun addTimelineAdder(
        render: @Composable () -> Unit,
        onClick: ((String) -> Unit) -> Unit,
    ) {
        timelineAdders.add(TimelineAdder(render, onClick))
    }

    private val timelineItemActions = mutableListOf<TimelineItemAction>()
    fun addTimelineItemAction(action: TimelineItemAction) {
        timelineItemActions.add(action)
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
        timelines = timelines,
        timelineAdders = timelineAdders,
        timelineItemActions = timelineItemActions,
    )
}
