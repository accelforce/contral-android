package net.accelf.contral.core.pages.plugins

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.accelf.contral.api.plugin.LocalPlugins
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.ui.theme.ContralTheme

@Composable
internal fun PluginsPage() {
    val plugins = LocalPlugins.current
    Plugins(plugins = plugins)
}

@Composable
private fun Plugins(
    plugins: List<Plugin>,
) {
    Column {
        plugins.forEach { it.Render() }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPlugins() {
    ContralTheme {
        Plugins(plugins = PreviewPluginProvider().values.toList())
    }
}
