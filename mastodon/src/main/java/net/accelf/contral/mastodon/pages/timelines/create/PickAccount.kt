package net.accelf.contral.mastodon.pages.timelines.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.models.PreviewAccountProvider
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.models.Account as DBAccount

@Composable
internal fun PickAccount(
    selectedAccount: DBAccount?,
    setSelectedAccount: (DBAccount) -> Unit,
    dbAccounts: List<DBAccount>,
    accountsMap: Map<DBAccount, ApiAccount?>,
    navController: NavController,
) {
    var expanded by useState(false)

    Box {
        OutlinedButton(
            onClick = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (selectedAccount == null) {
                Text(text = "Select an account")
            } else {
                RenderAccount(
                    dbAccount = selectedAccount,
                    apiAccount = accountsMap[selectedAccount],
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            dbAccounts.forEach { dbAccount ->
                DropdownMenuItem(
                    text = {
                        RenderAccount(
                            dbAccount = dbAccount,
                            apiAccount = accountsMap[dbAccount],
                        )
                    },
                    onClick = {
                        setSelectedAccount(dbAccount)
                        expanded = false
                    },
                )
            }

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(10.dp),
                        )
                        Text(text = "Add new account")
                    }
                },
                onClick = {
                    navController.navigate("mastodon/accounts/create")
                    expanded = false
                },
            )
        }
    }
}

@Composable
@Preview(widthDp = 300, heightDp = 300)
private fun PreviewPickAccount() {
    val dbAccountProvider = PreviewAccountProvider()
    val apiAccountProvider = net.accelf.contral.mastodon.api.PreviewAccountProvider()
    val accountsMap = remember {
        mutableStateMapOf(dbAccountProvider.values.first() to apiAccountProvider.values.first())
    }

    ContralTheme {
        Column {
            PickAccount(
                selectedAccount = null,
                setSelectedAccount = {},
                dbAccounts = dbAccountProvider.values.toList(),
                accountsMap = accountsMap,
                navController = rememberNavController(),
            )

            PickAccount(
                selectedAccount = dbAccountProvider.values.first(),
                setSelectedAccount = {},
                dbAccounts = dbAccountProvider.values.toList(),
                accountsMap = accountsMap,
                navController = rememberNavController(),
            )
        }
    }
}
