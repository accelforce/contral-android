package net.accelf.contral.core.pages.navigator

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState

@Composable
fun Navigator(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var destination by useState(value = navController.currentDestination!!)

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
    ) {
        TextPicker(
            items = navController.graph,
            currentItem = destination,
            getLabel = { it.route!! },
            onItemSelected = { destination = it },
        )

        Button(
            onClick = { navController.navigate(destination.route!!) },
            modifier = Modifier.padding(start = 4.dp),
            enabled = destination != navController.currentDestination,
        ) {
            Text(text = "Navigate")
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewNavigator() {
    val navController = rememberNavController()

    ContralTheme {
        NavHost(
            navController = navController,
            startDestination = "sample",
        ) {
            composable("sample") {
                Navigator(navController = navController)
            }
        }
    }
}
