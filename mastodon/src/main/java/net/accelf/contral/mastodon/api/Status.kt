package net.accelf.contral.mastodon.api

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.mastodon.ui.Html
import net.accelf.contral.mastodon.ui.HtmlAnnotations
import net.accelf.contral.mastodon.ui.HtmlText

@Serializable
data class Status(
    @SerialName("id") val id: String,
    @SerialName("account") val account: Account,
    @SerialName("content") val content: Html,
) : TimelineItem {

    @Composable
    override fun Render() {
        val uriHandler = LocalUriHandler.current

        Row {
            AsyncImage(
                model = account.avatar,
                contentDescription = account.acct,
                modifier = Modifier.size(48.dp),
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = account.displayName,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    Text(
                        text = "@${account.acct}",
                        modifier = Modifier.padding(start = 4.dp),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                HtmlText(
                    html = content,
                ) { annotations ->
                    annotations.forEach { annotation ->
                        when (annotation.tag) {
                            HtmlAnnotations.URL.tag -> {
                                uriHandler.openUri(annotation.item)
                                return@HtmlText
                            }
                        }
                    }
                }
            }
        }
    }
}

private class PreviewStatusProvider : PreviewParameterProvider<Status> {
    override val values: Sequence<Status>
        get() = sequenceOf(
            Status(
                id = "123456789123456789",
                account = Account(
                    id = "123456789123456789",
                    acct = "test",
                    username = "test",
                    avatar = "https://robohash.org/sample.png?set=set4",
                    nullableDisplayName = "Test",
                ),
                content = "<p>Hi!<br>This is a sample post.</p>",
            ),
        )
}

@Composable
@Preview
private fun PreviewStatus(
    @PreviewParameter(PreviewStatusProvider::class) status: Status,
) {
    ContralTheme {
        status.Render()
    }
}
