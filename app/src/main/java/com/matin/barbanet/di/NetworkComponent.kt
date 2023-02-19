package com.matin.barbanet.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.matin.barbanet.BuildConfig
import com.matin.barbanet.data.remote.ConfigApi
import com.matin.barbanet.data.repository.ConfigRepositoryImpl
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.network.ConnectivityInterceptor
import com.matin.barbanet.network.EkOkHttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkComponent {

    @Provides
    fun provideBaseUrl(): String =
        BuildConfig.BASE_URL_API

    @Singleton
    @Provides
    fun provideConnectivityInterceptor(
        app: Application
    ): ConnectivityInterceptor = ConnectivityInterceptor(app)


    @Singleton
    @Provides
    fun provideOkHttp(
        connectivityInterceptor: ConnectivityInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(
                EkOkHttpInterceptor(
                    showHeaders = true,
                    showLongResponsesInChunks = true,
                    showAuthorizationTokenInOkHttpLogs = true
                )
            ).hostnameVerifier { p0, p1 -> true }
            .build()


    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideConfigApi(
        retrofit: Retrofit
    ): ConfigApi = retrofit.create(ConfigApi::class.java)

    @Singleton
    @Provides
    fun provideConfigRepository(
        configApi: ConfigApi
    ): ConfigRepository =
        ConfigRepositoryImpl(configApi)

}