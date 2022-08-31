package net.accelf.contral.mastodon.pages.accounts.show

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import at.connyduck.calladapter.networkresult.onSuccess
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.PreviewAccountProvider
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

@Composable
internal fun ShowAccountPage(
    id: String,
    domain: String,
    sourceDBAccount: DBAccount?,
) {
    val scope = rememberCoroutineScope()
    val mastodonApi = remember(domain, sourceDBAccount) { MastodonApi.create(domain, sourceDBAccount?.accessToken) }
    var account by useState<ApiAccount?>(null)
    var sourceApiAccount by useState<ApiAccount?>(null)
    var loading by useState(false)

    val loadAccount = suspend {
        loading = true
        mastodonApi.getAccount(id)
            .onSuccess {
                account = it
            }
        loading = false
    }

    LaunchedEffect(mastodonApi.hashCode(), id) {
        loadAccount()
    }

    LaunchedEffect(mastodonApi.hashCode()) {
        sourceApiAccount = sourceDBAccount?.let {
            mastodonApi.getSelfAccount().getOrNull()
        }
    }

    ShowAccount(
        account = account,
        domain = domain,
        sourceAccount = sourceApiAccount,
        loading = loading,
        onRefresh = {
            scope.launch {
                loadAccount()
            }
        },
    )
}

private const val HeaderRatio = 8f / 3
private const val AvatarSize = 64

@Composable
private fun ShowAccount(
    account: ApiAccount?,
    domain: String,
    sourceAccount: ApiAccount?,
    loading: Boolean,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = loading),
        onRefresh = onRefresh,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "Viewing as",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 4.dp),
                )

                sourceAccount?.let {
                    AsyncImage(
                        model = sourceAccount.avatar,
                        contentDescription = "@${sourceAccount.acct}",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 2.dp),
                    )
                }

                Text(
                    text = sourceAccount?.let { "@${sourceAccount.acct}" } ?: "$domain (Public)",
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            account?.let {
                BoxWithConstraints {
                    AsyncImage(
                        model = account.header,
                        contentDescription = "Header",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(HeaderRatio),
                        contentScale = ContentScale.FillWidth,
                    )

                    Column(
                        modifier = Modifier.padding(
                            top = this@BoxWithConstraints.maxWidth / HeaderRatio - (AvatarSize / 3).dp,
                        ),
                    ) {
                        Row {
                            AsyncImage(
                                model = account.avatar,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(AvatarSize.dp)
                                    .padding(4.dp),
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(start = 4.dp, bottom = 4.dp),
                            ) {
                                Text(
                                    text = account.displayName,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleSmall,
                                )

                                Text(
                                    text = "@${account.acct}",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal data class PreviewShowAccountParameter(
    val account: ApiAccount?,
    val domain: String,
    val sourceAccount: ApiAccount?,
    val loading: Boolean,
)

internal class PreviewShowAccountParameterProvider : PreviewParameterProvider<PreviewShowAccountParameter> {
    private val accountProvider = PreviewAccountProvider()

    override val values: Sequence<PreviewShowAccountParameter>
        get() = sequenceOf(
            PreviewShowAccountParameter(
                account = null,
                domain = "example.domain",
                sourceAccount = null,
                loading = true,
            ),
            PreviewShowAccountParameter(
                account = accountProvider.values.first(),
                domain = "example.domain",
                sourceAccount = null,
                loading = false,
            ),
            PreviewShowAccountParameter(
                account = accountProvider.values.last(),
                domain = "example.domain",
                sourceAccount = accountProvider.values.first(),
                loading = true,
            ),
            PreviewShowAccountParameter(
                account = accountProvider.values.last(),
                domain = "example.domain",
                sourceAccount = accountProvider.values.last(),
                loading = false,
            ),
        )
}

@Composable
@Preview(widthDp = 300, heightDp = 300)
private fun PreviewShowAccount(
    @PreviewParameter(PreviewShowAccountParameterProvider::class) parameter: PreviewShowAccountParameter,
) {
    val (account, domain, sourceAccount, loading) = parameter

    ContralTheme {
        ShowAccount(
            account = account,
            domain = domain,
            sourceAccount = sourceAccount,
            loading = loading,
            onRefresh = {},
        )
    }
}
