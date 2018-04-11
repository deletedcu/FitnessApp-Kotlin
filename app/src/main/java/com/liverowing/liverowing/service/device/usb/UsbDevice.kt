package com.liverowing.liverowing.service.device.usb

import android.content.Context
import android.hardware.usb.*
import android.hardware.usb.UsbDevice
import android.os.CountDownTimer
import android.util.Log
import com.liverowing.liverowing.model.pm.*
import com.liverowing.liverowing.service.device.Device
import com.liverowing.liverowing.service.device.usb.c2.VPM
import com.liverowing.liverowing.service.messages.DeviceConnected
import org.greenrobot.eventbus.EventBus


class UsbDevice(val context: Context, val device: UsbDevice) : Device() {
    override lateinit var name: String
    override var connected: Boolean = false

    lateinit var vpm: VPM

    private lateinit var conn: UsbDeviceConnection
    private lateinit var interf: UsbInterface

    private lateinit var inReq: UsbRequest
    private lateinit var outReq: UsbRequest

    private lateinit var epIn: UsbEndpoint
    private lateinit var epOut: UsbEndpoint

    companion object {
        const val VENDOR_ID = 6052

        fun findDevice(context: Context): UsbDevice? {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            manager.deviceList.values
                    .filter { Log.d("LiveRowing", it.toString()); it.vendorId == VENDOR_ID }
                    .forEach { return it }

            return null
        }
    }

    override fun connect() {
        vpm = VPM()
        vpm.start(context)
        notificationLoop()
        return

        if (device.vendorId == VENDOR_ID) {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            conn = manager.openDevice(device)

            interf = device.getInterface(0)
            if (!conn.claimInterface(interf, true)) {
                conn.close()
                return
            }

            /*
            (0 until interf.endpointCount)
                    .map { interf.getEndpoint(it) }
                    .forEach { Log.d("LiveRowing", "ep type " + it.type + " ep dir " + it.direction + " ep addr " + String.format("0x%02X", it.address)) }
            */

            epIn = interf.getEndpoint(0)
            epOut = interf.getEndpoint(1)

            inReq = UsbRequest()
            inReq.initialize(conn, epIn)
            outReq = UsbRequest()
            outReq.initialize(conn, epOut)

            name = device.productName
            connected = true
            EventBus.getDefault().postSticky(DeviceConnected(this@UsbDevice))
        }
    }

    private var secondTimer: CountDownTimer? = null
    fun notificationLoop() {
        secondTimer = object : CountDownTimer(vpm.READ_INTERVAL.toLong(), vpm.READ_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                notificationLoop()
            }
        }.start()

        Log.d("LiveRowing", vpm.toString())

        val rowingStatus = RowingStatus(
                vpm.splitTime,
                vpm.splitDistance.toDouble(),
                WorkoutType.fromInt(vpm.workoutType),
                IntervalType.fromInt(vpm.intervalType),
                WorkoutState.fromInt(vpm.workoutState),
                RowingState.ACTIVE, // TODO: Ehh?
                StrokeState.fromInt(vpm.strokeState),
                vpm.workTime,
                DurationType.TIME,
                vpm.totalWorkDistance.toDouble(),
                vpm.dragFactor
        )
        Log.d("LiveRowing", rowingStatus.toString())

        vpm.status = -1
    }

    override fun disconnect() {

    }

    override fun setupWorkout(workoutType: com.liverowing.liverowing.model.parse.WorkoutType, targetPace: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}