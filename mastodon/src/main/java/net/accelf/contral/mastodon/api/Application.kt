package net.accelf.contral.mastodon.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("website") val website: String?,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
)
