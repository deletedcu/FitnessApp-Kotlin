package com.liverowing.android.ble.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.liverowing.android.R.drawable.ic_search
import com.liverowing.android.ble.profile.PM5Manager
import com.liverowing.android.ble.profile.PM5ManagerCallbacks
import no.nordicsemi.android.ble.BleManager
import timber.log.Timber


class PM5Service : BleProfileService(), PM5ManagerCallbacks {
    private val NOTIFICATION_ID = 267
    private var mManager: PM5Manager? = null
    private val mBinder = PM5Binder()

    inner class PM5Binder : BleProfileService.LocalBinder()

    override fun getBinder(): BleProfileService.LocalBinder {
        return mBinder
    }

    override fun initializeManager(): BleManager<PM5ManagerCallbacks> {
        mManager = PM5Manager(this)
        return mManager!!
    }

    override fun onDestroy() {
        // when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
        cancelNotification()

        super.onDestroy()
    }

    override fun onRebind() {
        // when the activity rebinds to the service, remove the notification
        cancelNotification()
    }

    override fun onUnbind() {
        // when the activity closes we need to show the notification that user is connected to the sensor
        createNotification("We are connected to a Performance Monitor", 0)
    }

    private fun createNotification(message: String, defaults: Int) {
        Timber.d("createNotification")
        val builder = NotificationCompat.Builder(this, "PM_CONNECTED_CHANNEL")
        builder.setContentTitle("LiveRowing").setContentText(message + deviceName)
        builder.setSmallIcon(ic_search)
        builder.setShowWhen(defaults != 0).setDefaults(defaults).setAutoCancel(true).setOngoing(true)

        val notification = builder.build()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, notification)
        startForeground(NOTIFICATION_ID, notification)

    }

    /**
     * Cancels the existing notification. If there is no active notification this method does nothing
     */
    private fun cancelNotification() {
        Timber.d("cancelNotification")
        //val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //nm.cancel(NOTIFICATION_ID)
    }

    override fun onDataReceived(state: Boolean) {

    }

    override fun onDataSent(state: Boolean) {

    }
}
