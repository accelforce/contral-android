package net.accelf.contral.mastodon.pages.accounts.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.LocalRegisterTimeline
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.mastodon.LocalMastodonDatabase
import net.accelf.contral.mastodon.MastodonDatabase
import net.accelf.contral.mastodon.api.Application
import net.accelf.contral.mastodon.api.AuthApi
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.retrofitBuilder
import net.accelf.contral.mastodon.models.Account
import net.accelf.contral.mastodon.timelines.HomeTimeline
import okhttp3.HttpUrl
import retrofit2.create

private fun createAuthApi(domain: String): AuthApi = retrofitBuilder
    .baseUrl(
        HttpUrl.Builder()
            .scheme("https")
            .host(domain)
            .build(),
    )
    .build()
    .create()

private const val SCOPES = "read write follow push"

private fun authorizeUrl(domain: String, application: Application) =
    HttpUrl.Builder()
        .scheme("https")
        .host(domain)
        .addPathSegments("oauth/authorize")
        .addQueryParameter("client_id", application.clientId)
        .addQueryParameter("scope", SCOPES)
        .addQueryParameter("redirect_uri", application.redirectUri)
        .addQueryParameter("response_type", "code")
        .build()
        .toString()

private suspend fun authorize(
    domain: String,
    authApi: AuthApi,
    db: MastodonDatabase,
    application: Application,
    code: String,
): Account {
    val token = authApi
        .getToken(
            clientId = application.clientId,
            clientSecret = application.clientSecret,
            redirectUri = application.redirectUri,
            grantType = "authorization_code",
            scope = SCOPES,
            code = code,
        )

    val mastodonApi = MastodonApi.create(domain, token.accessToken)
    val apiAccount = mastodonApi.getSelfAccount()
    val dbAccount = Account(
        domain = domain,
        id = apiAccount.id,
        accessToken = token.accessToken,
    )
    db.accountDao().insert(dbAccount)
    return dbAccount
}

@Composable
internal fun AuthorizationOptions(
    domain: String,
) {
    val authApi: AuthApi = remember { createAuthApi(domain) }
    val db = LocalMastodonDatabase.current
    val uriHandler = LocalUriHandler.current
    val navController = LocalNavController.current
    val registerTimeline = LocalRegisterTimeline.current
    var application: Application? by useState(null)

    CodeAuthorization(
        onStart = {
            application = authApi
                .createApp(
                    clientName = "Contral: Mastodon Plugin",
                    redirectUris = "urn:ietf:wg:oauth:2.0:oob",
                    scopes = SCOPES,
                    website = "https://accelf.net/contral",
                )
            uriHandler.openUri(authorizeUrl(domain, application!!))
        },
        showCodeField = application != null,
    ) { code ->
        val account = authorize(
            domain,
            authApi,
            db,
            application!!,
            code,
        )
        registerTimeline(HomeTimeline(domain, account.id))
        navController.navigate("timelines")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CodeAuthorization(
    onStart: suspend () -> Unit,
    showCodeField: Boolean,
    onCodeSubmit: suspend (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var error by useState("")

    Column {
        Button(
            onClick = {
                scope.launch {
                    runCatching { onStart() }
                        .onFailure {
                            error = it.message.toString()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Authorize using code")
        }

        if (showCodeField) {
            var code by useState("")

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(text = "Authorization Code") },
                )

                Button(
                    onClick = {
                        scope.launch {
                            runCatching {
                                onCodeSubmit(code)
                            }
                                .onFailure {
                                    error = it.message.toString()
                                }
                        }
                    },
                    modifier = Modifier
                        .requiredWidth(IntrinsicSize.Min)
                        .padding(start = 4.dp),
                ) {
                    Text(text = "Authorize")
                }
            }
        }

        if (error.isNotBlank()) {
            Text(
                text = error,
                color = Color.Red,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewCodeAuthorization() {
    ContralTheme {
        CodeAuthorization(
            onStart = {},
            showCodeField = true,
            onCodeSubmit = {},
        )
    }
}
