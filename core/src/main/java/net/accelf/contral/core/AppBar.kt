package net.accelf.contral.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AppBar(
    navController: NavController,
) {
    var menuExpanded by useState(false)

    CenterAlignedTopAppBar(
        title = { Text(text = "Contral") },
        actions = {
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Plugins") },
                    onClick = {
                        navController.navigate("plugins")
                        menuExpanded = false
                    },
                )

                DropdownMenuItem(
                    text = { Text(text = "Navigator") },
                    onClick = {
                        navController.navigate("navigator")
                        menuExpanded = false
                    },
                )
            }
        },
    )
}

@Composable
@Preview
private fun PreviewAppBar() {
    ContralTheme {
        AppBar(
            navController = rememberNavController(),
        )
    }
}
