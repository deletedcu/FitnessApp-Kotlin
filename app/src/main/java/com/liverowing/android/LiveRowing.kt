package com.liverowing.android

import android.app.Application
import android.content.Context
import com.liverowing.android.model.parse.*
import com.parse.Parse
import com.parse.ParseException
import com.parse.ParseObject
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import androidx.multidex.MultiDex

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
                else -> "Unknown error with code ${e.code}"
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
        //ParseUser.logOut()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

}