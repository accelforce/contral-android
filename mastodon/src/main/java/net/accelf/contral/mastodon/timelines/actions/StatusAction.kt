package net.accelf.contral.mastodon.timelines.actions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.accelf.contral.api.timelines.LocalTimeline
import net.accelf.contral.api.timelines.TypedTimelineItemAction
import net.accelf.contral.api.ui.states.StateHolder
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Status
import net.accelf.contral.mastodon.timelines.HomeTimeline

internal class StatusStateHolder : StateHolder {

    internal var timeline: HomeTimeline? by mutableStateOf(null)
    internal var mastodonApi: MastodonApi? by mutableStateOf(null)

    @Composable
    @SuppressLint("ComposableNaming")
    override fun prepare() {
        val db = LocalMastodonDatabase.current
        timeline = LocalTimeline.current as HomeTimeline

        LaunchedEffect(this) {
            mastodonApi = db.accountDao().getAccount(timeline!!.domain, timeline!!.id)?.mastodonApi
        }
    }
}

internal sealed class StatusAction : TypedTimelineItemAction<Status, StatusStateHolder>(Status::class) {
    override fun createStateHolder(timelineItem: Status): StatusStateHolder = StatusStateHolder()
}
