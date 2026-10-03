package com.example.data.backend

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * ZuccoCloudApi defines the REST API endpoints for communicating with the
 * secure Zucco Cloud Backend.
 */
interface ZuccoCloudApi {

    @GET("api/v1/users")
    suspend fun getBackendUsers(): List<UserBackendDto>

    @POST("api/v1/users/sync")
    suspend fun syncUsers(@Body users: List<UserBackendDto>): Map<String, Any>

    @GET("api/v1/providers")
    suspend fun getBackendProviders(): List<ProviderBackendDto>

    @POST("api/v1/providers/sync")
    suspend fun syncProviders(@Body providers: List<ProviderBackendDto>): Map<String, Any>

    @PUT("api/v1/providers/{id}")
    suspend fun updateProvider(@Path("id") id: Long, @Body provider: ProviderBackendDto): ProviderBackendDto

    companion object {
        private const val BASE_URL = "https://api.zucco.ng/"

        fun create(): ZuccoCloudApi {
            val authInterceptor = Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val timestamp = System.currentTimeMillis().toString()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer zucco_cloud_sec_token_${timestamp.takeLast(6)}")
                    .header("X-Zucco-Client", "Zucco-Android-SecureApp")
                    .header("X-Zucco-Timestamp", timestamp)
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ZuccoCloudApi::class.java)
        }
    }
}
