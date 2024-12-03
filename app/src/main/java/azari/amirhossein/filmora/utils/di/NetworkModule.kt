package azari.amirhossein.filmora.utils.di

import azari.amirhossein.filmora.BuildConfig
import azari.amirhossein.filmora.data.network.ApiService
import azari.amirhossein.filmora.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl(): String = Constants.Network.BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithHeaders = originalRequest.newBuilder()
                    .addHeader(Constants.Network.HEADER_ACCEPT, Constants.Network.CONTENT_TYPE_JSON)
                    .addHeader(Constants.Network.HEADER_CONTENT_TYPE, Constants.Network.CONTENT_TYPE_JSON)
                    .addHeader(Constants.Network.HEADER_AUTHORIZATION, "Bearer ${Constants.Network.API_KEY}")
                    .build()
                try {
                    chain.proceed(requestWithHeaders)
                } catch (e: Exception) {
                    throw Exception("Network error: ${e.localizedMessage}", e)
                }
            }
            .connectTimeout(Constants.Network.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.Network.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.Network.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient, gson: Gson): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

}