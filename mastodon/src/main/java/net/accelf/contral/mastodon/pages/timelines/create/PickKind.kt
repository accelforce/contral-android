package net.accelf.contral.mastodon.pages.timelines.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState

@Composable
internal fun PickKind(
    enabled: Boolean,
    selectedKind: Kind,
    setSelectedKind: (Kind) -> Unit,
) {
    var expanded by useState(false)

    Box {
        OutlinedButton(
            onClick = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
        ) {
            selectedKind.Render()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Kind.values().forEach { kind ->
                DropdownMenuItem(
                    text = {
                        kind.Render()
                    },
                    onClick = {
                        setSelectedKind(kind)
                        expanded = false
                    },
                )
            }
        }
    }
}

internal enum class Kind(
    private val icon: ImageVector,
    private val title: String,
) {
    HOME(icon = Icons.Default.Home, title = "Home"),
    ;

    @Composable
    internal fun Render() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp),
            )

            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview(widthDp = 300, heightDp = 300)
private fun PreviewPickKind() {
    ContralTheme {
        PickKind(
            enabled = true,
            selectedKind = Kind.HOME,
            setSelectedKind = {},
        )
    }
}
