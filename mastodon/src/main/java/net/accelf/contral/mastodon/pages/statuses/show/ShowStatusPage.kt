package net.accelf.contral.mastodon.pages.statuses.show

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.api.Status
import net.accelf.contral.mastodon.pages.LocalApiSource

@Composable
internal fun ShowStatusPage(
    id: String,
) {
    val mastodonApi = LocalApiSource.current.mastodonApi

    ShowStatus(
        getStatus = remember(mastodonApi.hashCode(), id) {
            suspend { mastodonApi.getStatus(id) }
        },
    )
}

@Composable
private fun ShowStatus(
    getStatus: suspend () -> Status,
) {
    val scope = rememberCoroutineScope()
    var status by useState<Status?>(null)
    var loading by useState(false)
    val loadStatus = remember(getStatus.hashCode()) {
        suspend {
            loading = true
            status = getStatus()
            loading = false
        }
    }

    LaunchedEffect(loadStatus.hashCode()) {
        loadStatus()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = loading),
        onRefresh = { scope.launch { loadStatus() } },
    ) {
        status?.Render()
    }
}
