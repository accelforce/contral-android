package net.accelf.contral.core.pages.timelines

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import net.accelf.contral.api.timelines.LocalTimelineItem
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.timelines.TimelineItemAction
import net.accelf.contral.api.ui.states.StateHolder
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.core.LocalTimelineItemActions

@Composable
internal fun TimelineItemActionBar(
    modifier: Modifier = Modifier,
) {
    val timelineItem = LocalTimelineItem.current
    val actions = LocalTimelineItemActions.current

    RenderTimelineItemActionBar(
        timelineItem = timelineItem,
        actions = actions,
        modifier = modifier,
    )
}

@Composable
private fun RenderTimelineItemActionBar(
    timelineItem: TimelineItem,
    actions: List<TimelineItemAction>,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        actions.forEach { action ->
            if (action.supports(timelineItem)) {
                val holder = remember(action, timelineItem) {
                    action.createStateHolder(timelineItem)
                }
                holder.prepare()

                Box(
                    modifier = Modifier.clickable {
                        scope.launch {
                            action.action(timelineItem, holder)
                        }
                    },
                ) {
                    action.Icon(
                        timelineItem = timelineItem,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(widthDp = 300)
private fun PreviewRenderTimelineItemActionBar() {
    ContralTheme {
        RenderTimelineItemActionBar(
            timelineItem = object : TimelineItem {
                @Composable
                override fun Render() {
                    TODO()
                }

                @Composable
                @SuppressLint("ComposableNaming")
                override fun getOnSelected(setOnSelected: (() -> Unit) -> Unit) {
                    TODO()
                }
            },
            actions = List(5) {
                object : TimelineItemAction {
                    override fun supports(timelineItem: TimelineItem): Boolean = true

                    @Composable
                    override fun Icon(timelineItem: TimelineItem) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                        )
                    }

                    @Composable
                    override fun MenuItem(timelineItem: TimelineItem) {
                        TODO()
                    }

                    override fun createStateHolder(timelineItem: TimelineItem): StateHolder = TODO()

                    override suspend fun action(
                        timelineItem: TimelineItem,
                        stateHolder: StateHolder,
                    ) {
                        TODO()
                    }
                }
            },
        )
    }
}
