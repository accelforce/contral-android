package net.accelf.contral.mastodon

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.room.Room
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.models.Account
import net.accelf.contral.mastodon.pages.accounts.create.CreateAccountPage
import net.accelf.contral.mastodon.pages.accounts.show.ShowAccountPage
import net.accelf.contral.mastodon.timelines.HomeTimeline

@Suppress("unused")
fun PluginResolver.mastodonPlugin() {
    name = "Mastodon"
    version = 0 minor 0 patch 0

    require("core", 0 minor 5)

    inject {
        val context = LocalContext.current
        val db = Room
            .databaseBuilder(context, MastodonDatabase::class.java, "net.accelf.contral.mastodon.MastodonDatabase")
            .build()
        LocalMastodonDatabase provides db
    }

    addRoutes {
        composable("mastodon/accounts/create") { CreateAccountPage() }
        composable(
            "mastodon/accounts/{domain}/{id}?sourceId={sourceId}",
            listOf(
                navArgument("domain") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("sourceId") {
                    type = NavType.StringType
                    nullable = true
                },
            ),
        ) {
            val id = it.arguments!!.getString("id")!!
            val domain = it.arguments!!.getString("domain")!!
            val sourceId = it.arguments!!.getString("sourceId")
            var sourceAccount by useState<Account?>(null)

            sourceId?.let {
                val db = LocalMastodonDatabase.current
                LaunchedEffect(db, domain, sourceId) {
                    sourceAccount = db.accountDao().getAccount(domain, sourceId)
                }
            }

            ShowAccountPage(
                id = id,
                domain = domain,
                sourceDBAccount = sourceAccount,
            )
        }
    }

    addTimelines {
        val db = LocalMastodonDatabase.current

        LaunchedEffect(Unit) {
            db.accountDao().listAccounts()
                .forEach {
                    addTimeline(HomeTimeline(it))
                }
        }
    }

    addTimelineAdder(
        render = { Text(text = "Login to Mastodon") },
        onClick = { it("mastodon/accounts/create") },
    )
}

internal val LocalMastodonDatabase = staticCompositionLocalOf<MastodonDatabase> {
    error("LocalMastodonDatabase is not set")
}
