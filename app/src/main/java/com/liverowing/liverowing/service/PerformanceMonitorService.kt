package com.liverowing.liverowing.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.bluetooth.BluetoothDevice
import android.hardware.usb.UsbDevice
import com.liverowing.liverowing.service.device.BleDevice
import com.liverowing.liverowing.service.device.Device
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.liverowing.liverowing.service.messages.*


class PerformanceMonitorService : Service() {
    private lateinit var mDevice: Device

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        Log.d("LiveRowing", "Service: onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // The service is starting, due to a call to startService()
        Log.d("LiveRowing", "Service: onStartCommand")

        return Service.START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

        Log.d("LiveRowing", "Service: onDestroy")
        // The service is no longer used and is being destroyed
    }


    @Subscribe
    fun onDeviceConnectRequest(message: DeviceConnectRequest) {
        val device = message.device
        if (device is BluetoothDevice) {
            mDevice = BleDevice(this@PerformanceMonitorService, device).apply {
                connect()
            }
        } else if (device is UsbDevice) {

        }
    }

    @Subscribe
    fun onProgramWorkoutRequest(message: WorkoutProgramRequest) {
        Log.d("LiveRowing", "Should program workout: " + message.workoutType.name)
        mDevice.setupWorkout(message.workoutType, 130)
    }
}
