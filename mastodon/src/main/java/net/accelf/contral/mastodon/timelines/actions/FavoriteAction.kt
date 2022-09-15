package net.accelf.contral.mastodon.timelines.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.mastodon.api.PreviewStatusProvider
import net.accelf.contral.mastodon.api.Status

internal object FavoriteAction : StatusAction() {

    @Composable
    override fun Icon(timelineItem: Status) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.Star,
            contentDescription = when {
                timelineItem.favourited -> "Un-favourite"
                else -> "Favourite"
            },
            tint = when {
                timelineItem.favourited -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onBackground
            },
        )
    }

    @Composable
    override fun MenuItem(timelineItem: Status) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = when {
                    timelineItem.favourited -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onBackground
                },
            )

            Text(
                text = when {
                    timelineItem.favourited -> "Un-favourite"
                    else -> "Favourite"
                },
            )
        }
    }

    override suspend fun action(timelineItem: Status, stateHolder: StatusStateHolder) {
        val timeline = stateHolder.timeline
        val mastodonApi = stateHolder.mastodonApi

        when {
            timelineItem.favourited -> mastodonApi?.unFavouriteStatus(timelineItem.id)
            else -> mastodonApi?.favouriteStatus(timelineItem.id)
        }
            ?.let { updated ->
                timeline?.pagingSource?.replace(updated)
            }
    }
}

@Composable
@Preview
private fun PreviewStatusAction(
    @PreviewParameter(PreviewStatusProvider::class) status: Status,
) {
    ContralTheme {
        Column {
            FavoriteAction.Icon(timelineItem = status)
            FavoriteAction.MenuItem(timelineItem = status)
        }
    }
}
