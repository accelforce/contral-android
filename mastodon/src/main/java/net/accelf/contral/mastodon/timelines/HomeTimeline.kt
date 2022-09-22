package net.accelf.contral.mastodon.timelines

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.accelf.contral.api.composers.Composer
import net.accelf.contral.api.timelines.AbstractTimeline
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.MastodonComposer
import net.accelf.contral.mastodon.api.Status
import net.accelf.contral.mastodon.util.Reference
import java.util.*
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

@Serializable
@SerialName("mastodon/home")
internal class HomeTimeline(
    @SerialName("domain") val domain: String,
    @SerialName("id") val id: String,
) : AbstractTimeline() {

    @Transient
    private val statuses = TreeMap<String, Status> { o1, o2 ->
        -compareValues(o1, o2)
    }

    @Transient
    private val pagingSourceRef = Reference<StatusPagingSource>()
    var pagingSource by pagingSourceRef
        private set

    @Composable
    @SuppressLint("ComposableNaming")
    @OptIn(ExperimentalPagingApi::class)
    override fun getPager(setPager: (Pager<*, out TimelineItem>) -> Unit) {
        val db = LocalMastodonDatabase.current

        LaunchedEffect(Unit) {
            val account = db.accountDao().getAccount(domain, id)!!
            val mastodonApi = account.mastodonApi

            setPager(
                Pager(
                    config = PagingConfig(20),
                    remoteMediator = StatusRemoteMediator(pagingSourceRef) { key, loadSize ->
                        mastodonApi.getHomeTimeline(
                            limit = loadSize,
                            minId = key.minId,
                            maxId = key.maxId,
                        )
                    },
                ) {
                    StatusPagingSource(statuses).also { pagingSource = it }
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
            setComposer(MastodonComposer(account.mastodonApi))
        }
    }

    @Composable
    override fun Render() {
        val db = LocalMastodonDatabase.current
        var dbAccount by useState<DBAccount?>(null)
        var apiAccount by useState<ApiAccount?>(null)

        LaunchedEffect(Unit) {
            dbAccount = db.accountDao().getAccount(domain, id)
            apiAccount = runCatching { dbAccount!!.mastodonApi.getSelfAccount() }.getOrNull()
        }

        dbAccount?.let {
            RenderTimeline(
                dbAccount = dbAccount!!,
                apiAccount = apiAccount,
            )
        }
    }
}
