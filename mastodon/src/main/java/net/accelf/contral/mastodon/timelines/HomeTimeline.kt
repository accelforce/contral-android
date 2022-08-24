package net.accelf.contral.mastodon.timelines

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.models.Account

internal class HomeTimeline(
    private val account: Account,
) : Timeline {
    override fun pager() = Pager(PagingConfig(20)) {
        StatusPagingSource(MastodonApi.create(account.domain, account.accessToken))
    }

    @Composable
    override fun Render() {
        Text(text = "${account.domain}/${account.id}")
    }
}
