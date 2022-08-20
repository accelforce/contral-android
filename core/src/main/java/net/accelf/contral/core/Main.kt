package net.accelf.contral.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.plugin.LocalPlugins
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.core.pages.Greeting
import net.accelf.contral.core.pages.navigator.Navigator
import net.accelf.contral.core.pages.plugins.PluginsPage
import net.accelf.contral.core.plugin.resolvePlugins

@Composable
fun Main() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val plugins = remember { resolvePlugins(context) }

    CompositionLocalProvider(
        LocalPlugins provides plugins,
    ) {
        ContralTheme {
            NavHost(navController = navController, startDestination = "navigator") {
                composable("navigator") { Navigator(navController = navController) }
                composable("greetings") { Greeting(name = "Contral") }
                composable("plugins") { PluginsPage() }
            }
        }
    }
}
