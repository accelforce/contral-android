package net.accelf.contral.mastodon

import net.accelf.contral.api.composers.Composer
import net.accelf.contral.mastodon.api.MastodonApi

class MastodonComposer(
    private val mastodonApi: MastodonApi,
) : Composer {
    override suspend fun compose(content: String): Boolean =
        runCatching {
            mastodonApi
                .postStatus(
                    content = content,
                )
        }.isSuccess
}
