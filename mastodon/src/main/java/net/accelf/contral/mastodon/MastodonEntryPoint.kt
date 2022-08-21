package net.accelf.contral.mastodon

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.room.Room
import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.mastodon.pages.accounts.create.CreateAccountPage

@Suppress("unused")
fun PluginResolver.mastodonPlugin() {
    name = "Mastodon"
    version = 0 minor 0 patch 0

    require("core", 0 minor 2)

    inject {
        val context = LocalContext.current
        val db = Room
            .databaseBuilder(context, MastodonDatabase::class.java, "net.accelf.contral.mastodon.MastodonDatabase")
            .build()
        LocalMastodonDatabase provides db
    }

    addRoutes {
        composable("mastodon/accounts/create") { CreateAccountPage() }
    }
}

internal val LocalMastodonDatabase = staticCompositionLocalOf<MastodonDatabase> {
    error("LocalMastodonDatabase is not set")
}
