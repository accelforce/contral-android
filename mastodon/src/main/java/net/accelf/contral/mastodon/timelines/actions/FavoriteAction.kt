package net.accelf.contral.mastodon.timelines.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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

    private fun title(timelineItem: Status) =
        when (timelineItem.favorited) {
            true -> "Un-favourite"
            false -> "Favourite"
        }

    @Composable
    private fun color(timelineItem: Status) =
        when (timelineItem.favorited) {
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.onBackground
        }

    @Composable
    override fun Icon(timelineItem: Status) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = title(timelineItem),
            tint = color(timelineItem),
        )
    }

    @Composable
    override fun MenuItem(timelineItem: Status) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color(timelineItem),
            )

            Text(
                text = title(timelineItem),
            )
        }
    }

    override suspend fun action(timelineItem: Status, stateHolder: StatusStateHolder) {
        val timeline = stateHolder.timeline
        val mastodonApi = stateHolder.mastodonApi

        when {
            timelineItem.favorited -> mastodonApi?.unFavouriteStatus(timelineItem.actionableStatus.id)
            else -> mastodonApi?.favouriteStatus(timelineItem.actionableStatus.id)
        }
            ?.let { updated ->
                timeline?.pagingSource?.replace(updated)
                if (!timelineItem.isActionable) {
                    timeline?.pagingSource?.replace(timelineItem.copy(boostedStatus = updated))
                }
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
