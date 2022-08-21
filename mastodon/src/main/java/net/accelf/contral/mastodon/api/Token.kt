package net.accelf.contral.mastodon.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("access_token") val accessToken: String,
    @SerialName("scope") val scope: String,
)
