package net.accelf.contral.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.ui.theme.ContralAndroidTheme
import net.accelf.contral.core.pages.Greeting

@Composable
fun Main() {
    val navController = rememberNavController()

    ContralAndroidTheme {
        NavHost(navController = navController, startDestination = "greetings") {
            composable("greetings") { Greeting(name = "Contral") }
        }
    }
}
