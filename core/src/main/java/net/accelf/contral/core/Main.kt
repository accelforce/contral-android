package net.accelf.contral.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.ui.theme.ContralAndroidTheme
import net.accelf.contral.core.pages.Greeting
import net.accelf.contral.core.pages.navigator.Navigator

@Composable
fun Main() {
    val navController = rememberNavController()

    ContralAndroidTheme {
        NavHost(navController = navController, startDestination = "navigator") {
            composable("navigator") { Navigator(navController = navController) }
            composable("greetings") { Greeting(name = "Contral") }
        }
    }
}
