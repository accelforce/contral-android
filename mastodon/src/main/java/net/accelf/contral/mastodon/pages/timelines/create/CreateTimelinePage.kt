package net.accelf.contral.mastodon.pages.timelines.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.LocalRegisterTimeline
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.timelines.HomeTimeline
import net.accelf.contral.mastodon.api.Account as ApiAccount
import net.accelf.contral.mastodon.api.PreviewAccountProvider as PreviewApiAccountProvider
import net.accelf.contral.mastodon.models.Account as DBAccount
import net.accelf.contral.mastodon.models.PreviewAccountProvider as PreviewDBAccountProvider

@Composable
internal fun CreateTimelinePage() {
    val navController = LocalNavController.current
    val db = LocalMastodonDatabase.current
    val registerTimeline = LocalRegisterTimeline.current
    val dbAccounts by db.accountDao().listAccounts().collectAsState(emptyList())
    val accountsMap = remember { mutableStateMapOf<DBAccount, ApiAccount>() }

    LaunchedEffect(dbAccounts) {
        dbAccounts.forEach { dbAccount ->
            if (accountsMap[dbAccount] == null) {
                accountsMap[dbAccount] = dbAccount.mastodonApi.getSelfAccount()
            }
        }
    }

    CreateTimeline(
        dbAccounts = dbAccounts,
        accountsMap = accountsMap,
        navController = navController,
        registerTimeline = registerTimeline,
    )
}

@Composable
private fun CreateTimeline(
    dbAccounts: List<DBAccount>,
    accountsMap: Map<DBAccount, ApiAccount?>,
    navController: NavController,
    registerTimeline: suspend (Timeline) -> Long,
) {
    var selectedAccount by useState<DBAccount?>(null)
    var selectedKind by useState(Kind.HOME)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            LaunchedEffect(dbAccounts) {
                if (selectedAccount == null) {
                    selectedAccount = dbAccounts.firstOrNull()
                }
            }

            Text(
                text = "Step 1. Choose an account",
                style = MaterialTheme.typography.titleLarge,
            )

            PickAccount(
                selectedAccount = selectedAccount,
                setSelectedAccount = { selectedAccount = it },
                dbAccounts = dbAccounts,
                accountsMap = accountsMap,
                navController = navController,
            )

            Text(
                text = "Step 2. Choose a kind of a timeline",
                style = MaterialTheme.typography.titleLarge,
            )

            PickKind(
                enabled = selectedAccount != null,
                selectedKind = selectedKind,
                setSelectedKind = { selectedKind = it },
            )
        }

        val scope = rememberCoroutineScope()
        var loading by useState(false)
        ExtendedFloatingActionButton(
            text = {
                Text(
                    text = "Save",
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "",
                )
            },
            onClick = {
                if (selectedAccount != null && !loading) {
                    scope.launch {
                        loading = true
                        val id = registerTimeline(
                            when (selectedKind) {
                                Kind.HOME -> HomeTimeline(domain = selectedAccount!!.domain, id = selectedAccount!!.id)
                            },
                        )
                        loading = false
                        navController.popBackStack()
                        navController.navigate("timelines/$id")
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
@Preview(widthDp = 300, heightDp = 600)
private fun PreviewCreateTimeline() {
    val dbAccountProvider = PreviewDBAccountProvider()
    val apiAccountProvider = PreviewApiAccountProvider()
    val accountsMap = remember {
        mutableStateMapOf(dbAccountProvider.values.first() to apiAccountProvider.values.first())
    }

    ContralTheme {
        CreateTimeline(
            dbAccounts = dbAccountProvider.values.toList(),
            accountsMap = accountsMap,
            navController = rememberNavController(),
            registerTimeline = { 0 },
        )
    }
}
