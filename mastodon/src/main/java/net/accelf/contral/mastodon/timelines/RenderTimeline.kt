package net.accelf.contral.mastodon.timelines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
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
internal fun RenderTimeline(
    dbAccount: DBAccount,
    apiAccount: ApiAccount?,
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(48.dp)
                .padding(2.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Home",
                maxLines = 1,
            )

            Text(
                text = apiAccount?.let { "@${apiAccount.username}@${dbAccount.domain}" }
                    ?: "${dbAccount.domain}:${dbAccount.id}",
                modifier = Modifier.align(End),
                maxLines = 1,
            )
        }

        Row {
            AsyncImage(
                model = apiAccount?.avatar,
                contentDescription = apiAccount?.acct,
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp),
            )
        }
    }
}

internal class PreviewRenderTimelineParameterProvider : PreviewParameterProvider<Pair<DBAccount, ApiAccount?>> {
    private val dbAccountProvider = PreviewDBAccountProvider()
    private val apiAccountProvider = PreviewApiAccountProvider()

    override val values: Sequence<Pair<DBAccount, ApiAccount?>>
        get() = dbAccountProvider.values.zip(apiAccountProvider.values)
            .plus(dbAccountProvider.values.first() to null)
}

@Composable
@Preview(widthDp = 360)
private fun PreviewRenderTimeline(
    @PreviewParameter(PreviewRenderTimelineParameterProvider::class) parameter: Pair<DBAccount, ApiAccount?>,
) {
    val (dbAccount, apiAccount) = parameter

    ContralTheme {
        RenderTimeline(
            dbAccount = dbAccount,
            apiAccount = apiAccount,
        )
    }
}
