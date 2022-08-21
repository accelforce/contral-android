package net.accelf.contral.core.pages.plugins

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.ui.theme.ContralTheme

@Composable
internal fun Plugin.Render() {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(text = version.toString(), style = MaterialTheme.typography.bodySmall)
    }
}

internal class PreviewPluginProvider : PreviewParameterProvider<Plugin> {
    override val values: Sequence<Plugin>
        get() = sequenceOf(
            Plugin(
                id = "net.accelf.contral.core.example.ExamplePlugin.examplePlugin",
                name = "Example Plugin",
                version = 1 minor 2 patch 3,
                dependencies = emptyMap(),
                injects = emptyList(),
            ),
            Plugin(
                id = "net.accelf.contral.mastodon.MastodonPlugin.mastodonPlugin",
                name = "Mastodon",
                version = 0 minor 0 patch 0,
                dependencies = emptyMap(),
                injects = emptyList(),
            ),
        )
}

@Composable
@Preview
private fun PreviewPlugin(
    @PreviewParameter(PreviewPluginProvider::class) plugin: Plugin,
) {
    ContralTheme {
        plugin.Render()
    }
}
