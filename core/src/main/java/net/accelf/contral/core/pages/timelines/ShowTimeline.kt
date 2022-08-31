package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import net.accelf.contral.api.timelines.LocalTimeline
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineItem

@Suppress("UNCHECKED_CAST")
@Composable
internal fun ShowTimeline(
    timeline: Timeline,
) {
    val pagingItems = (timeline.pager() as Pager<*, TimelineItem>).flow.collectAsLazyPagingItems()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = {
            pagingItems.refresh()
        },
        modifier = Modifier.fillMaxSize(),
        indicator = { _, distance ->
            SwipeRefreshIndicator(
                state = SwipeRefreshState(pagingItems.loadState.refresh == LoadState.Loading),
                refreshTriggerDistance = distance,
            )
        },
    ) {
        CompositionLocalProvider(
            LocalTimeline provides timeline,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = pagingItems,
                ) {
                    it?.Render()
                    Divider()
                }
            }
        }
    }
}
