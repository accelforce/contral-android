package net.accelf.contral.mastodon.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApi {
    @GET("/api/v1/accounts/{id}")
    suspend fun getAccount(@Path("id") id: String): Account

    @GET("/api/v1/accounts/verify_credentials")
    suspend fun getSelfAccount(): Account

    @GET("/api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("limit") limit: Int,
        @Query("min_id") minId: String? = null,
        @Query("max_id") maxId: String? = null,
    ): List<Status>

    @GET("/api/v1/statuses/{id}")
    suspend fun getStatus(
        @Path("id") id: String,
    ): Status

    @POST("/api/v1/statuses")
    @FormUrlEncoded
    suspend fun postStatus(
        @Field("status") content: String,
    ): Status

    @POST("/api/v1/statuses/{id}/favourite")
    suspend fun favouriteStatus(
        @Path("id") id: String,
    ): Status

    @POST("/api/v1/statuses/{id}/reblog")
    suspend fun boostStatus(
        @Path("id") id: String,
    ): Status

    @POST("/api/v1/statuses/{id}/unfavourite")
    suspend fun unFavouriteStatus(
        @Path("id") id: String,
    ): Status

    @POST("/api/v1/statuses/{id}/unreblog")
    suspend fun unBoostStatus(
        @Path("id") id: String,
    ): Status

    companion object {
        internal fun create(domain: String, accessToken: String?): MastodonApi = retrofitBuilder
            .client(
                OkHttpClient.Builder().apply {
                    accessToken?.let {
                        addInterceptor {
                            val request = it.request().newBuilder()
                                .addHeader("Authorization", "Bearer $accessToken")
                                .build()
                            it.proceed(request)
                        }
                    }
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                }
                    .build(),
            )
            .baseUrl(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(domain)
                    .build(),
            )
            .build()
            .create()
    }
}
