package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import net.accelf.contral.api.ui.states.StateHolder
import kotlin.reflect.KClass

interface TimelineItemAction {
    fun supports(timelineItem: TimelineItem): Boolean

    @Composable
    fun Icon(timelineItem: TimelineItem)

    @Composable
    fun MenuItem(timelineItem: TimelineItem)

    fun createStateHolder(timelineItem: TimelineItem): StateHolder

    suspend fun action(timelineItem: TimelineItem, stateHolder: StateHolder)
}

@Suppress("INAPPLICABLE_JVM_NAME", "UNCHECKED_CAST")
abstract class TypedTimelineItemAction<T : TimelineItem, S : StateHolder>(private val klass: KClass<T>) :
    TimelineItemAction {
    @JvmName("supportsCustom")
    open fun supports(timelineItem: T): Boolean = true

    override fun supports(timelineItem: TimelineItem): Boolean =
        klass.isInstance(timelineItem) && supports(timelineItem as T)

    @Composable
    @JvmName("IconCustom")
    abstract fun Icon(timelineItem: T)

    @Composable
    override fun Icon(timelineItem: TimelineItem) = Icon(timelineItem as T)

    @Composable
    @JvmName("MenuItemCustom")
    abstract fun MenuItem(timelineItem: T)

    @Composable
    override fun MenuItem(timelineItem: TimelineItem) = MenuItem(timelineItem as T)

    @JvmName("createStateHolderCustom")
    abstract fun createStateHolder(timelineItem: T): S

    override fun createStateHolder(timelineItem: TimelineItem) = createStateHolder(timelineItem as T)

    @JvmName("actionCustom")
    abstract suspend fun action(timelineItem: T, stateHolder: S)

    override suspend fun action(timelineItem: TimelineItem, stateHolder: StateHolder) =
        action(timelineItem as T, stateHolder as S)
}
