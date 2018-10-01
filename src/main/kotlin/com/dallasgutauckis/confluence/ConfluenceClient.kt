package com.dallasgutauckis.confluence

import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ConfluenceClient(
    baseUrl: String,
    auth: Authentication?,
    logger: ((message: String) -> Unit)? = null,
    logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
) {
    private val restClient: ConfluenceService

    init {
        val okHttpClient = OkHttpClient.Builder()

        if (auth != null) {
            okHttpClient.addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader("Authorization", auth.getCredentials())
                        .build()
                )
            }
        }

        // Add last so it logs everything
        if (logger != null) {
            val loggingInterceptor =
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> logger.invoke(message) })
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

    interface Authentication {
        fun getCredentials(): String
    }

    data class BasicAuthentication(
        val user: String,
        val token: String
    ) : Authentication {
        override fun getCredentials(): String = Credentials.basic(user, token)

        override fun toString(): String {
            return "Basic %s:%s[...]%s".format(
                user,
                token.subSequence(0, 3),
                token.subSequence(token.length - 4, token.length - 1)
            )
        }
    }

    data class TokenAuthentication(
        val token: String
    ) : Authentication {
        override fun getCredentials(): String = "Bearer $token"

        override fun toString(): String {
            return "Bearer %s[...]%s".format(
                token.subSequence(0, 3),
                token.substring(token.length - 4, token.length - 1)
            )
        }
    }
}