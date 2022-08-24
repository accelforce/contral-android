package net.accelf.contral.mastodon.timelines

internal data class LoadKey(
    val minId: String? = null,
    val maxId: String? = null,
)
