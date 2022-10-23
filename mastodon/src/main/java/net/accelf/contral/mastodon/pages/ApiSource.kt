package net.accelf.contral.mastodon.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import net.accelf.contral.api.ui.utils.compositionLocalOf
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

internal sealed interface ApiSource {
    val domain: String
    val id: String?
        get() = null
    val mastodonApi: MastodonApi

    @Composable
    fun Render(
        modifier: Modifier,
    )
}

internal data class PublicApiSource(
    override val domain: String,
) : ApiSource {
    override val mastodonApi by lazy { MastodonApi.create(domain, null) }

    @Composable
    override fun Render(
        modifier: Modifier,
    ) {
        Text(
            text = "Viewing as $domain (Public)",
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier,
        )
    }
}

internal data class AccountApiSource(
    override val domain: String,
    val dbAccount: DBAccount,
    val apiAccount: ApiAccount,
) : ApiSource {
    override val mastodonApi by lazy { MastodonApi.create(domain, dbAccount.accessToken) }
    override val id
        get() = dbAccount.id

    @Composable
    override fun Render(
        modifier: Modifier,
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = modifier,
        ) {
            Text(
                text = "Viewing as",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp),
            )

            AsyncImage(
                model = apiAccount.avatar,
                contentDescription = "@${apiAccount.acct}",
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 2.dp),
            )

            Text(
                text = "@${apiAccount.acct}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

internal val LocalApiSource by compositionLocalOf<ApiSource>()

@Composable
internal fun NavBackStackEntry.WithSourceAccount(
    content: @Composable () -> Unit,
) {
    val domain = arguments!!.getString("domain")!!
    val sourceId = arguments!!.getString("sourceId")

    val db = LocalMastodonDatabase.current

    val source by produceState<ApiSource>(PublicApiSource(domain), domain, sourceId) {
        value = if (sourceId == null) {
            PublicApiSource(domain)
        } else {
            val dbAccount = db.accountDao().getAccount(domain, sourceId)!!
            val apiAccount = MastodonApi.create(domain, dbAccount.accessToken).getSelfAccount()
            AccountApiSource(domain, dbAccount, apiAccount)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        source.Render(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        CompositionLocalProvider(LocalApiSource provides source) {
            content()
        }
    }
}
