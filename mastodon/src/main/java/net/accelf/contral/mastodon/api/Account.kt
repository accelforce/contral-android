package net.accelf.contral.mastodon.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String,
)
