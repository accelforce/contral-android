package net.accelf.contral.mastodon.api

import at.connyduck.calladapter.networkresult.NetworkResult
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApi {
    @GET("/api/v1/accounts/{id}")
    suspend fun getAccount(@Path("id") id: String): NetworkResult<Account>

    @GET("/api/v1/accounts/verify_credentials")
    suspend fun getSelfAccount(): NetworkResult<Account>

    @GET("/api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("limit") limit: Int,
        @Query("min_id") minId: String? = null,
        @Query("max_id") maxId: String? = null,
    ): NetworkResult<List<Status>>

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
