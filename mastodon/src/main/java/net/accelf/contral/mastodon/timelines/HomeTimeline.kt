package net.accelf.contral.mastodon.timelines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import at.connyduck.calladapter.networkresult.onSuccess
import kotlinx.coroutines.launch
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

internal class HomeTimeline(
    private val dbAccount: DBAccount,
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
        val scope = rememberCoroutineScope()
        var apiAccount by useState<ApiAccount?>(null)

        SideEffect {
            scope.launch {
                mastodonApi.getSelfAccount()
                    .onSuccess { apiAccount = it }
            }
        }

        RenderTimeline(
            dbAccount = dbAccount,
            apiAccount = apiAccount,
        )
    }
}
