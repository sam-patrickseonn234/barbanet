package com.matin.barbanet.network

import android.annotation.SuppressLint
import android.app.Application
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.provider.Settings
import com.matin.barbanet.BuildConfig
import com.matin.common.utiles.CommonSharedPref


class ConnectivityInterceptor(
    val app: Application
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request().newBuilder()
        val request = requestHeader(app, originalRequest, chain.request().url.toString())
        return chain.proceed(request)
    }

    companion object {
        @SuppressLint("HardwareIds")
        fun requestHeader(app: Application, request: Request.Builder, url: String): Request {
            var userSharedPref = CommonSharedPref(app.baseContext)
            val oAuth = userSharedPref.token
            val CSN = userSharedPref.csn ?: Settings.Secure.getString(
                app.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            val CTY = userSharedPref.cty ?: "A"
            val CVS = userSharedPref.cvs ?: BuildConfig.VERSION_NAME

            if (oAuth != null) {
                return request
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .addHeader("Authorization", oAuth)
                    .addHeader("CTY", CTY)
                    .addHeader("CSN", CSN)
                    .addHeader("CVS", CVS)
                    .addHeader("LNG", "fa")
                    .build()
            } else {
                return request
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .addHeader("CTY", CTY)
                    .addHeader("CSN", CSN)
                    .addHeader("CVS", CVS)
                    .addHeader("LNG", "fa")
                    .build()
            }
        }
    }

}
