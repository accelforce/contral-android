package net.accelf.contral.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.core.plugin.resolvePlugins

@Composable
@OptIn(InternalComposeApi::class)
fun Main() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val plugins = remember { resolvePlugins(context) }

    CompositionLocalProvider(
        LocalPlugins provides plugins,
        LocalNavController provides navController,
    ) {
        val values = plugins.map(Plugin::injects).flatten().map { it.invoke() }.toTypedArray()
        currentComposer.startProviders(values)
        ContralTheme {
            NavHost(navController = navController, startDestination = "navigator") {
                plugins.forEach { it.renderRoutes.invoke(this) }
            }
        }
        currentComposer.endProviders()
    }
}

val LocalNavController = staticCompositionLocalOf<NavController> { error("LocalNavController is not set") }
