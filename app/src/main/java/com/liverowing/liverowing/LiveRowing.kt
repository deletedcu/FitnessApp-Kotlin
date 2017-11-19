package com.liverowing.liverowing

import android.app.Application
import android.util.Log
import com.bumptech.glide.Glide
import com.liverowing.liverowing.model.parse.*
import com.parse.*
import com.squareup.otto.Bus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException


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
        ParseObject.registerSubclass(UserStats::class.java)

        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(LoggingInterceptor())

        val conf = Parse.Configuration.Builder(this)
                .applicationId("ugn8WWO3EcgFvcTaFIMyOaE6RldMWwkDScwC1hwo")
                //.server("http://10.0.2.2:1337")
                .server("https://api.liverowing.com")
                .clientBuilder(clientBuilder)
                .build()
        Parse.initialize(conf)

        ParseInstallation.getCurrentInstallation().saveInBackground()
        ParseConfig.getInBackground(object: ConfigCallback {
            override fun done(config: ParseConfig?, e: ParseException?) {
                Log.d("LiveRowing", config.toString())
            }
        })
    }

    companion object {
        val eventBus = Bus()
    }
}

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        Log.i(TAG, String.format("Sending %s request %s on %s%n%s", request.method(),
                request.url(), chain.connection(),
                stringifyRequestBody(request)))

        val response = chain.proceed(request)

        val t2 = System.nanoTime()

        Log.i(TAG, String.format("Received response for %s in %.1fms", response.request().url(),
                (t2 - t1) / 1e6))

        return response
    }

    private fun stringifyRequestBody(request: Request): String {
        if (request.body() != null) {
            try {
                val copy = request.newBuilder().build()
                val buffer = Buffer()
                copy.body()!!.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                Log.w(TAG, "Failed to stringify request body: " + e.message)
            }

        }
        return ""
    }

    companion object {
        private val TAG = "LiveRowing"
    }
}