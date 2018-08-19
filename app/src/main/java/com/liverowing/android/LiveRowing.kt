package com.liverowing.android

import android.app.Application
import android.content.Context
import com.liverowing.android.model.parse.*
import com.parse.*
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class LiveRowing : Application() {
    companion object {
        @JvmStatic
        fun refWatcher(context: Context?): RefWatcher =
                (context?.applicationContext as LiveRowing).refWatcher

        fun parseErrorMessageFromException(e: ParseException): String {
            return when (e.code) {
                ParseException.CACHE_MISS -> ""
                ParseException.CONNECTION_FAILED -> "Connection to LiveRowing servers failed."
                ParseException.TIMEOUT -> "Connection to LiveRowing servers timed out."
                else -> "Unknown error with code ${e.code} - ${e.localizedMessage}"
            }
        }
    }

    lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        refWatcher = LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Parse.setLogLevel(Parse.LOG_LEVEL_INFO)
        }

        Preferences.init(this@LiveRowing)

        ParseObject.registerSubclass(Affiliate::class.java)
        ParseObject.registerSubclass(Goals::class.java)
        ParseObject.registerSubclass(Segment::class.java)
        ParseObject.registerSubclass(Stats::class.java)
        ParseObject.registerSubclass(User::class.java)
        ParseObject.registerSubclass(WorkoutType::class.java)
        ParseObject.registerSubclass(Workout::class.java)
        ParseObject.registerSubclass(UserStats::class.java)


        val clientBuilder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        clientBuilder.networkInterceptors().add(httpLoggingInterceptor)

        val conf = Parse.Configuration.Builder(this)
                .applicationId("ugn8WWO3EcgFvcTaFIMyOaE6RldMWwkDScwC1hwo")
                .clientBuilder(clientBuilder)
                .server("https://api.liverowing.com")
                .build()

        Parse.initialize(conf)

        val configRefreshInterval = 12 * 60 * 60 * 1000
        val currentTime = System.currentTimeMillis()
        if (currentTime - Preferences.lastParseConfigFetch > configRefreshInterval) {
            ParseConfig.getInBackground { _, e ->
                if (e != null) {
                    Timber.e(e, "Failed to fetch config from server.")
                } else {
                    Preferences.lastParseConfigFetch = currentTime
                }
            }
        }
    }

}