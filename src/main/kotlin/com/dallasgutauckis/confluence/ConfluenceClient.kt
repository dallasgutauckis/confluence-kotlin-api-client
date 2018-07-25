package com.dallasgutauckis.confluence

import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ConfluenceClient(private val baseUrl: String,
                       private val auth: Authentication?,
                       private val logger: ((message: String) -> Unit)? = null,
                       private val logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE) {
    private val restClient: ConfluenceService

    init {
        val okHttpClient = OkHttpClient.Builder()

        if (auth != null) {
            okHttpClient.addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                        .addHeader("Authorization", Credentials.basic(auth.user, auth.token))
                        .build())
            }
        }

        // Add last so it logs everything
        if (logger != null) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> logger.invoke(message) })
            loggingInterceptor.level = logLevel
            okHttpClient.addNetworkInterceptor(loggingInterceptor)
        }

        restClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .build()
                .create(ConfluenceService::class.java)
    }

    fun methods(): ConfluenceMethods {
        return ConfluenceMethodsRetrofit(restClient)
    }

    data class Authentication(val user: String,
                              val token: String) {
        override fun toString(): String {
            return "%s:%s[...]%s".format(user, token.subSequence(0, 3), token.subSequence(token.length - 4, token.length - 1))
        }
    }
}