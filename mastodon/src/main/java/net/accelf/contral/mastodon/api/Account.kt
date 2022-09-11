package net.accelf.contral.mastodon.api

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String,
    @SerialName("acct") val acct: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val nullableDisplayName: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("header") val header: String,
) {

    val displayName
        get() = nullableDisplayName.takeUnless(String::isEmpty) ?: username

    fun path(domain: String, sourceId: String?) = "mastodon/accounts/$domain/$id?sourceId=$sourceId"
}

internal class PreviewAccountProvider : PreviewParameterProvider<Account> {
    override val values: Sequence<Account>
        get() = sequenceOf(
            Account(
                id = "123456789123456789",
                acct = "test",
                username = "test",
                avatar = "https://robohash.org/sample.png?set=set4",
                nullableDisplayName = "Test",
                header = "https://picsum.photos/200/300",
            ),
            Account(
                id = "234567890234567890",
                acct = "announcement@remote.domain",
                username = "announcement",
                avatar = "https://robohash.org/sample.png?set=set4",
                nullableDisplayName = "",
                header = "https://picsum.photos/200/300",
            ),
        )
}
