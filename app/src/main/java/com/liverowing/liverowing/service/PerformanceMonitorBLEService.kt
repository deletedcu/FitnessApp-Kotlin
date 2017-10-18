package com.liverowing.liverowing.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class PerformanceMonitorBLEService : Service() {
    companion object {
        const val ACTION_SERVICE_READY = "bleServiceReady"
        const val ACTION_SERVICE_MESSAGE = "bleServiceMessage"
    }

    override fun onCreate() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)

        localBroadcastManager.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra("operation")) {
                    "list-devices" -> {
                        Log.d("LiveRowing", "Test")
                    }
                    else -> {
                        Log.d("LiveRowing", "Got unknown action: " + intent.action)
                    }
                }
            }
        }, IntentFilter(ACTION_SERVICE_MESSAGE))

        localBroadcastManager.sendBroadcast(Intent(ACTION_SERVICE_READY))
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? = null
}
