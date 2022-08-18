package net.accelf.contral.core.pages.navigator

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun <T> TextPicker(
    items: Iterable<T>,
    getLabel: (T) -> String,
    currentItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by useState(false)

    Column(
        modifier = modifier,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
                when (it) {
                    is PressInteraction.Release -> expanded = !expanded
                }
            }
        }
        OutlinedTextField(
            value = getLabel(currentItem),
            onValueChange = {},
            readOnly = true,
            interactionSource = interactionSource,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = getLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewTextPicker() {
    ContralTheme {
        val list = remember { listOf(1, 2, 3, 4, 5) }
        var current by useState(list.first())

        TextPicker(
            items = list,
            getLabel = Int::toString,
            currentItem = current,
            onItemSelected = { current = it },
        )
    }
}
