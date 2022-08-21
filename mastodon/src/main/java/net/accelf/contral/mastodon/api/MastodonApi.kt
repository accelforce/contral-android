package net.accelf.contral.mastodon.api

import at.connyduck.calladapter.networkresult.NetworkResult
import retrofit2.http.GET

interface MastodonApi {
    @GET("/api/v1/accounts/verify_credentials")
    suspend fun getSelfAccount(): NetworkResult<Account>
}
