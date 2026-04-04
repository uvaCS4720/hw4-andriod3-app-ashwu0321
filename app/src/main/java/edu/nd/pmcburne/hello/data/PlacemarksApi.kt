package edu.nd.pmcburne.hello.data

import retrofit2.http.GET
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

interface PlacemarksApi {
    @GET("placemarks.json")
    suspend fun getLocations(): List<LocationDto>

    companion object {
        private const val BASE_URL = "https://www.cs.virginia.edu/~wxt4gm/"

        fun create(): PlacemarksApi {
            val contentType = "application/json".toMediaType()
            val json = Json { ignoreUnknownKeys = true }
            
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()

            return retrofit.create(PlacemarksApi::class.java)
        }
    }
}
