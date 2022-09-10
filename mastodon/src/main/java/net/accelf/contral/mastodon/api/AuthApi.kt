package net.accelf.contral.mastodon.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/apps")
    @FormUrlEncoded
    suspend fun createApp(
        @Field("client_name") clientName: String,
        @Field("redirect_uris") redirectUris: String,
        @Field("scopes") scopes: String? = null,
        @Field("website") website: String? = null,
    ): Application

    @POST("/oauth/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("scope") scope: String,
    ): Token
}
