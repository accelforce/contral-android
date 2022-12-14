package net.accelf.contral.mastodon.api

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.pages.ApiSource
import net.accelf.contral.mastodon.pages.LocalApiSource
import net.accelf.contral.mastodon.ui.Html
import net.accelf.contral.mastodon.ui.HtmlAnnotations
import net.accelf.contral.mastodon.ui.HtmlText

@Serializable
data class Status(
    @SerialName("id") val id: String,
    @SerialName("account") val account: Account,
    @SerialName("content") val content: Html,
    @SerialName("reblog") val boostedStatus: Status?,
    @SerialName("spoiler_text") val contentsWarning: String,
    @SerialName("favourited") val apiFavorited: Boolean = false,
    @SerialName("reblogged") val apiBoosted: Boolean = false,
) : TimelineItem {

    internal val isActionable: Boolean
        get() = boostedStatus == null
    internal val actionableStatus: Status
        get() = boostedStatus ?: this
    internal val favorited: Boolean
        get() = actionableStatus.apiFavorited
    internal val boosted: Boolean
        get() = actionableStatus.apiBoosted

    @Composable
    override fun Render() {
        val navController = LocalNavController.current
        val source = LocalApiSource.current

        Render(
            openAccount = {
                navController.navigate(it.path(source.domain, source.id))
            },
        )
    }

    @Composable
    internal fun Render(
        openAccount: (Account) -> Unit,
    ) {
        val uriHandler = LocalUriHandler.current

        if (boostedStatus != null) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Boost",
                        modifier = Modifier.graphicsLayer(rotationY = 180f),
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    AsyncImage(
                        model = account.avatar,
                        contentDescription = account.acct,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp)
                            .clickable { openAccount(account) },
                    )

                    Text(
                        text = account.displayName,
                        modifier = Modifier.clickable { openAccount(account) },
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                    )

                    Text(
                        text = "@${account.acct}",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable { openAccount(account) },
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                boostedStatus.Render(
                    openAccount = openAccount,
                )
            }
        } else {
            Row {
                AsyncImage(
                    model = account.avatar,
                    contentDescription = account.acct,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(2.dp)
                        .clickable { openAccount(account) },
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { openAccount(account) },
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

                    var hideContents by useState(contentsWarning.isNotBlank())

                    if (contentsWarning.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hideContents = !hideContents },
                        ) {
                            val iconRotation by animateFloatAsState(if (hideContents) -90f else 0f)
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                modifier = Modifier.rotate(iconRotation),
                            )

                            Text(
                                text = contentsWarning,
                                maxLines = 1,
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = !hideContents,
                    ) {
                        HtmlText(
                            html = content,
                        ) { annotation ->
                            when (annotation.tag) {
                                HtmlAnnotations.URL.tag -> {
                                    { uriHandler.openUri(annotation.item) }
                                }
                                else -> null
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Suppress("ComposableNaming")
    override fun getOnSelected(setOnSelected: (() -> Unit) -> Unit) {
        val scope = rememberCoroutineScope()
        val navController = LocalNavController.current
        val source = LocalApiSource.current

        setOnSelected {
            scope.launch {
                navController.navigate(path(source))
            }
        }
    }

    private fun path(source: ApiSource) = "mastodon/statuses/${source.domain}/$id?sourceId=${source.id}"
}

internal class PreviewStatusProvider : PreviewParameterProvider<Status> {
    private val accountProvider = PreviewAccountProvider()

    override val values: Sequence<Status>
        get() = sequenceOf(
            Status(
                id = "123456789123456789",
                account = accountProvider.values.elementAt(0),
                content = "Hi!<br>This is a sample post.",
                boostedStatus = null,
                contentsWarning = "CW",
                apiFavorited = false,
                apiBoosted = false,
            ),
            Status(
                id = "234567890234567890",
                account = accountProvider.values.elementAt(1),
                content = "",
                boostedStatus = Status(
                    id = "123456789123456789",
                    account = accountProvider.values.elementAt(0),
                    content = "Hi!<br>This is a sample post.",
                    boostedStatus = null,
                    contentsWarning = "",
                    apiFavorited = true,
                    apiBoosted = true,
                ),
                contentsWarning = "",
                apiFavorited = false,
                apiBoosted = false,
            ),
        )
}

@Composable
@Preview
private fun PreviewStatus(
    @PreviewParameter(PreviewStatusProvider::class) status: Status,
) {
    ContralTheme {
        status.Render(
            openAccount = {},
        )
    }
}
