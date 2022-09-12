package net.accelf.contral.mastodon.pages.timelines.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.api.PreviewAccountProvider as PreviewApiAccountProvider
import net.accelf.contral.mastodon.models.Account as DBAccount
import net.accelf.contral.mastodon.models.PreviewAccountProvider as PreviewDBAccountProvider

@Composable
internal fun RenderAccount(
    dbAccount: DBAccount,
    apiAccount: ApiAccount?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        AsyncImage(
            model = apiAccount?.avatar,
            contentDescription = apiAccount?.acct,
            modifier = Modifier
                .size(48.dp)
                .padding(2.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = apiAccount?.displayName ?: "",
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = apiAccount?.let { "@${apiAccount.username}@${dbAccount.domain}" }
                    ?: "${dbAccount.domain}:${dbAccount.id}",
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

internal class PreviewRenderAccountParameterProvider :
    PreviewParameterProvider<Pair<DBAccount, ApiAccount?>> {
    private val dbAccountProvider = PreviewDBAccountProvider()
    private val apiAccountProvider = PreviewApiAccountProvider()

    override val values: Sequence<Pair<DBAccount, ApiAccount?>>
        get() = dbAccountProvider.values.zip(apiAccountProvider.values)
            .plus(dbAccountProvider.values.first() to null)
}

@Composable
@Preview(widthDp = 300)
private fun PreviewRenderAccount(
    @PreviewParameter(PreviewRenderAccountParameterProvider::class) parameter: Pair<DBAccount, ApiAccount?>,
) {
    val (dbAccount, apiAccount) = parameter

    ContralTheme {
        RenderAccount(
            dbAccount = dbAccount,
            apiAccount = apiAccount,
        )
    }
}
