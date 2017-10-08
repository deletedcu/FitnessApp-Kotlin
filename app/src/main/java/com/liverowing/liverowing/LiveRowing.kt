package com.liverowing.liverowing

import android.app.Application
import com.liverowing.liverowing.api.model.*
import com.parse.Parse
import com.parse.ParseInstallation
import com.parse.ParseObject
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import okhttp3.OkHttpClient
import com.squareup.picasso.Picasso
import com.jakewharton.picasso.OkHttp3Downloader
import okhttp3.Cache
import java.io.File


/**
 * Created by henrikmalmberg on 2017-10-01.
 */
class LiveRowing : Application() {
    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(Affiliate::class.java)
        ParseObject.registerSubclass(Goals::class.java)
        ParseObject.registerSubclass(Segment::class.java)
        ParseObject.registerSubclass(Stats::class.java)
        ParseObject.registerSubclass(User::class.java)
        ParseObject.registerSubclass(WorkoutType::class.java)
        ParseObject.registerSubclass(Workout::class.java)

        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(LoggingInterceptor())

        val conf = Parse.Configuration.Builder(this)
                .applicationId("ugn8WWO3EcgFvcTaFIMyOaE6RldMWwkDScwC1hwo")
                .server("http://192.168.0.103:1337")
                .clientBuilder(clientBuilder)
                .build()
        Parse.initialize(conf)
        ParseInstallation.getCurrentInstallation().saveInBackground()

        val httpCacheDirectory = File(cacheDir, "picasso-cache")
        val cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)

        clientBuilder.cache(cache)
        val picassoBuilder = Picasso.Builder(applicationContext)
        picassoBuilder.downloader(OkHttp3Downloader(clientBuilder.build()))
        val picasso = picassoBuilder.build()
        try {
            Picasso.setSingletonInstance(picasso)
        } catch (ignored: IllegalStateException) {
            Log.e("LiveRowing", "Picasso instance already used")
        }

    }
}

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        Log.i(TAG, String.format("Sending %s request %s on %s%n%s%n%s", request.method(),
                request.url(), chain.connection(), request.headers(),
                request.body().toString()))

        val response = chain.proceed(request)

        val t2 = System.nanoTime()

        Log.i(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(),
                (t2 - t1) / 1e6, response.headers()))

        return response
    }

    companion object {
        private val TAG = "LiveRowing"
    }
}