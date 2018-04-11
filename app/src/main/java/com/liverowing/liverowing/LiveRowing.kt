package com.liverowing.liverowing

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.*
import android.util.Log
import com.liverowing.liverowing.model.parse.*
import com.liverowing.liverowing.model.pm.RowingStatus
import com.liverowing.liverowing.model.pm.WorkoutState
import com.liverowing.liverowing.service.device.BleDevice
import com.liverowing.liverowing.service.device.Device
import com.parse.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException
import android.os.Build
import android.net.*
import com.liverowing.liverowing.service.messages.*

val preferences: Preferences by lazy {
    LiveRowing.preferences!!
}

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
class LiveRowing : Application() {
    override fun onCreate() {
        super.onCreate()

        preferences = Preferences(applicationContext)

        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
        EventBus.getDefault().register(this)

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
                .server("https://api.liverowing.com")
                .clientBuilder(clientBuilder)
                .build()
        Parse.initialize(conf)

        ParseInstallation.getCurrentInstallation().saveInBackground()
        ParseConfig.getInBackground()
        ParseUser.getCurrentUser()?.fetchInBackground<User>()

        registerForNetworkChanges(applicationContext)

        /*
        val isoCountryCodes = Locale.getISOCountries()
        for (countryCode in isoCountryCodes) {
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry

            Log.d("LiveRowing", locale.country + ", " + locale.displayCountry + ", " + locale.language + ", " + locale.displayLanguage + ", " + locale.displayName + ", " + locale.displayScript + ", " + locale.displayVariant + ", " + locale.isO3Country  + ", " + locale.isO3Language + ", " + locale.script + ", " + locale.variant + ", " + locale.toLanguageTag())
        }
        */
    }

    private fun registerForNetworkChanges(context: Context) {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.registerNetworkCallback(NetworkRequest.Builder().build(), object: ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network?) {
                    super.onLost(network)
                    EventBus.getDefault().postSticky(NetworkChange(false))
                }

                override fun onAvailable(network: Network?) {
                    super.onAvailable(network)
                    EventBus.getDefault().postSticky(NetworkChange(true))
                }
            })
        } else {
            registerReceiver(object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Log.d("LiveRowing", "IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).onReceive")
                }
            }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        val activeNetwork = cm.activeNetworkInfo
        EventBus.getDefault().postSticky(NetworkChange(activeNetwork != null && activeNetwork.isConnected))
    }

    @Subscribe
    fun onRowingStatus(data: RowingStatus) {
        workoutState = data.workoutState
    }

    @Subscribe
    fun onDeviceConnected(message: DeviceConnected) {
        deviceConnected = true
        device = message.device
    }

    @Subscribe
    fun onDeviceDisconnected(message: DeviceDisconnected) {
        deviceConnected = false
        device = null
    }

    @Subscribe
    fun onDeviceConnectRequest(message: DeviceConnectRequest) {
        val device = message.device
        if (device is BluetoothDevice) {
            BleDevice(this@LiveRowing, device).apply {
                connect()
            }
        } else if (device is android.hardware.usb.UsbDevice) {
            com.liverowing.liverowing.service.device.usb.UsbDevice(this@LiveRowing, device).apply {
                connect()
            }
        }
    }

    @Subscribe
    fun onProgramWorkoutRequest(message: WorkoutProgramRequest) {
        device?.setupWorkout(message.workoutType, message.targetPace)
    }

    companion object {
        lateinit var preferences: Preferences
        var device: Device? = null
        var deviceConnected = false
        var workoutState: WorkoutState? = null
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
        private const val TAG = "LiveRowing"
    }
}