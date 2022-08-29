package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.contral.api.ui.theme.ContralTheme

@Composable
internal fun AddTimelineButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        text = {
            Text(
                text = "Add a timeline",
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
            )
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
@Preview(widthDp = 400, heightDp = 400)
private fun PreviewAddTimelineButton() {
    ContralTheme {
        Box {
            AddTimelineButton(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {}
        }
    }
}
