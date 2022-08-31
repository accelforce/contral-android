package net.accelf.contral.mastodon.timelines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import at.connyduck.calladapter.networkresult.onSuccess
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

internal class HomeTimeline(
    val dbAccount: DBAccount,
) : Timeline {

    private val mastodonApi = MastodonApi.create(dbAccount.domain, dbAccount.accessToken)

    override fun pager() = Pager(PagingConfig(20)) {
        StatusPagingSource { params ->
            mastodonApi.getHomeTimeline(
                limit = params.loadSize,
                minId = params.key?.minId,
                maxId = params.key?.maxId,
            )
        }
    }

    @Composable
    override fun Render() {
        var apiAccount by useState<ApiAccount?>(null)

        LaunchedEffect(Unit) {
            mastodonApi.getSelfAccount()
                .onSuccess { apiAccount = it }
        }

        RenderTimeline(
            dbAccount = dbAccount,
            apiAccount = apiAccount,
        )
    }
}
