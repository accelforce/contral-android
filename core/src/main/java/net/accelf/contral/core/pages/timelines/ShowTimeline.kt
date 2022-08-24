package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineItem

@Suppress("UNCHECKED_CAST")
@Composable
internal fun ShowTimeline(
    timeline: Timeline,
) {
    val pagingItems = (timeline.pager() as Pager<*, TimelineItem>).flow.collectAsLazyPagingItems()

    LazyColumn {
        items(
            items = pagingItems,
        ) {
            it?.Render()
            Divider()
        }
    }
}
