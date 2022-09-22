package net.accelf.contral.mastodon

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.api.ui.utils.staticCompositionLocalOf
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.models.Account
import net.accelf.contral.mastodon.pages.accounts.create.CreateAccountPage
import net.accelf.contral.mastodon.pages.accounts.show.ShowAccountPage
import net.accelf.contral.mastodon.pages.timelines.create.CreateTimelinePage
import net.accelf.contral.mastodon.timelines.HomeTimeline
import net.accelf.contral.mastodon.timelines.actions.BoostAction
import net.accelf.contral.mastodon.timelines.actions.FavoriteAction

@Suppress("unused")
fun PluginResolver.mastodonPlugin() {
    name = "Mastodon"
    version = 0 minor 0 patch 0

    require("core", 0 minor 9)

    addDatabase(LocalMastodonDatabase)

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

        composable("mastodon/timelines/create") { CreateTimelinePage() }
    }

    addTimeline(HomeTimeline::class)

    addTimelineAdder(
        render = { Text(text = "Add a Mastodon timeline") },
        onClick = { it("mastodon/timelines/create") },
    )

    addTimelineItemAction(FavoriteAction)
    addTimelineItemAction(BoostAction)
}

internal val LocalMastodonDatabase by staticCompositionLocalOf<MastodonDatabase>()
