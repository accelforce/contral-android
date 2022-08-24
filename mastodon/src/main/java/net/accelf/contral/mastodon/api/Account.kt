package net.accelf.contral.mastodon.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String,
    @SerialName("acct") val acct: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val nullableDisplayName: String,
    @SerialName("avatar") val avatar: String,
) {

    val displayName
        get() = nullableDisplayName.takeUnless(String::isEmpty) ?: username
}
