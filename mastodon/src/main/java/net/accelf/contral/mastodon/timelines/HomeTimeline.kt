package net.accelf.contral.mastodon.timelines

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.accelf.contral.api.composers.Composer
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.MastodonComposer
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

@Serializable
@SerialName("mastodon/home")
internal class HomeTimeline(
    @SerialName("domain") val domain: String,
    @SerialName("id") val id: String,
) : Timeline {

    @Composable
    @SuppressLint("ComposableNaming")
    override fun getPager(setPager: (Pager<*, out TimelineItem>) -> Unit) {
        val db = LocalMastodonDatabase.current

        LaunchedEffect(Unit) {
            val account = db.accountDao().getAccount(domain, id)!!
            val mastodonApi = MastodonApi.create(account.domain, account.accessToken)

            setPager(
                Pager(PagingConfig(20)) {
                    StatusPagingSource { params ->
                        mastodonApi.getHomeTimeline(
                            limit = params.loadSize,
                            minId = params.key?.minId,
                            maxId = params.key?.maxId,
                        )
                    }
                },
            )
        }
    }

    @Composable
    @SuppressLint("ComposableNaming")
    override fun getComposer(setComposer: (Composer) -> Unit) {
        val db = LocalMastodonDatabase.current

        LaunchedEffect(Unit) {
            val account = db.accountDao().getAccount(domain, id)!!
            val mastodonApi = MastodonApi.create(account.domain, account.accessToken)
            setComposer(MastodonComposer(mastodonApi))
        }
    }

    @Composable
    override fun Render() {
        val db = LocalMastodonDatabase.current
        var dbAccount by useState<DBAccount?>(null)
        var apiAccount by useState<ApiAccount?>(null)

        LaunchedEffect(Unit) {
            dbAccount = db.accountDao().getAccount(domain, id)
            val mastodonApi = MastodonApi.create(dbAccount!!.domain, dbAccount!!.accessToken)
            apiAccount = mastodonApi.getSelfAccount()
        }

        dbAccount?.let {
            RenderTimeline(
                dbAccount = dbAccount!!,
                apiAccount = apiAccount,
            )
        }
    }
}
