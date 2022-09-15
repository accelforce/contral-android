@file:Suppress("ForbiddenImport")

package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import net.accelf.contral.api.composers.Composer
import net.accelf.contral.api.timelines.LocalTimeline
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.utils.useState

@Composable
internal fun ShowTimelinePage(
    timeline: Timeline,
) {
    var pager by useState<Pager<*, TimelineItem>?>(null)

    timeline.getPager {
        @Suppress("UNCHECKED_CAST")
        pager = it as Pager<*, TimelineItem>
    }

    CompositionLocalProvider(
        LocalTimeline provides timeline,
    ) {
        pager?.let {
            ShowTimeline(
                timeline = timeline,
                pager = pager!!,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun ShowTimeline(
    timeline: Timeline,
    pager: Pager<*, TimelineItem>,
) {
    val pagingItems = pager.flow.collectAsLazyPagingItems()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = {
            pagingItems.refresh()
        },
        modifier = Modifier.fillMaxSize(),
        indicator = { state, distance ->
            SwipeRefreshIndicator(
                state = if (state.isSwipeInProgress) {
                    state
                } else {
                    SwipeRefreshState(pagingItems.loadState.refresh == LoadState.Loading)
                },
                refreshTriggerDistance = distance,
            )
        },
    ) {
        BoxWithConstraints {
            val state = rememberBottomSheetScaffoldState()
            var min by useState(0.dp)
            val max = maxHeight
            val height = run {
                val ratio = state.bottomSheetState.progress.fraction
                when (state.bottomSheetState.progress.from) {
                    BottomSheetValue.Collapsed -> when (state.bottomSheetState.progress.to) {
                        BottomSheetValue.Collapsed -> min
                        BottomSheetValue.Expanded -> {
                            min * (1 - ratio) + max * ratio
                        }
                    }
                    BottomSheetValue.Expanded -> when (state.bottomSheetState.progress.to) {
                        BottomSheetValue.Collapsed -> {
                            min * ratio + max * (1 - ratio)
                        }
                        BottomSheetValue.Expanded -> max
                    }
                }
            }

            var composer by useState<Composer>(DummyComposer)
            timeline.getComposer {
                composer = it
            }
            BottomSheetScaffold(
                sheetContent = {
                    Box(modifier = Modifier.height(height)) {
                        Composer(
                            setMinHeight = { min = it },
                            composer = composer,
                        )
                    }
                    Box(modifier = Modifier.height(max - height))
                },
                scaffoldState = state,
                sheetShape = MaterialTheme.shapes.small,
                sheetPeekHeight = min,
                sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
}
