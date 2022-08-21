package net.accelf.contral.mastodon.pages.accounts.create

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import net.accelf.contral.api.ui.utils.useState

@Composable
internal fun CreateAccountPage() {
    Column {
        var domain by useState<String?>(value = null)
        ConfigureDomain(
            onDomainSubmit = {
                domain = it
            },
        )

        domain?.let {
            AuthorizationOptions(domain = it)
        }
    }
}
