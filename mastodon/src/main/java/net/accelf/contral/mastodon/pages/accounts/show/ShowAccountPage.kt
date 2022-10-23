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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.api.Account
import net.accelf.contral.mastodon.api.PreviewAccountProvider
import net.accelf.contral.mastodon.pages.LocalApiSource

@Composable
internal fun ShowAccountPage(
    id: String,
) {
    val mastodonApi = LocalApiSource.current.mastodonApi

    ShowAccount(
        getAccount = remember(mastodonApi.hashCode(), id) {
            suspend { mastodonApi.getAccount(id) }
        },
    )
}

private const val HeaderRatio = 8f / 3
private const val AvatarSize = 64

@Composable
private fun ShowAccount(
    getAccount: suspend () -> Account,
) {
    val scope = rememberCoroutineScope()
    var account by useState<Account?>(null)
    var loading by useState(false)
    val loadAccount = remember(getAccount.hashCode()) {
        suspend {
            loading = true
            account = getAccount()
            loading = false
        }
    }

    LaunchedEffect(loadAccount.hashCode()) {
        loadAccount()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = loading),
        onRefresh = { scope.launch { loadAccount() } },
    ) {
        account?.let {
            BoxWithConstraints {
                AsyncImage(
                    model = it.header,
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
                            model = it.avatar,
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
                                text = it.displayName,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall,
                            )

                            Text(
                                text = "@${it.acct}",
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

@Composable
@Preview(widthDp = 300, heightDp = 300)
private fun PreviewShowAccount(
    @PreviewParameter(PreviewAccountProvider::class) account: Account,
) {
    ContralTheme {
        ShowAccount(
            getAccount = { account },
        )
    }
}
