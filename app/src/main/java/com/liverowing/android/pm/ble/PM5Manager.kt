package com.liverowing.android.pm.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.liverowing.android.model.messages.DeviceConnected
import com.liverowing.android.model.messages.DeviceDisconnected
import com.liverowing.android.model.pm.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*


class PM5Manager(context: Context) : BleManager<BleManagerCallbacks>(context) {
    companion object {
        // Peripheral UUID
        val PM_DEVICE_UUID = UUID.fromString("ce060000-43e5-11e4-916c-0800200c9a66")

        // Service UUIDs
        val PM_DEVICEINFO_SERVICE_UUID = UUID.fromString("ce060010-43e5-11e4-916c-0800200c9a66")
        val PM_CONTROL_SERVICE_UUID = UUID.fromString("ce060020-43e5-11e4-916c-0800200c9a66")
        val PM_ROWING_SERVICE_UUID = UUID.fromString("ce060030-43e5-11e4-916c-0800200c9a66")

        // Characteristic UUIDs for PM device info service
        val MODEL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("ce060011-43e5-11e4-916c-0800200c9a66")!!
        val SERIAL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("ce060012-43e5-11e4-916c-0800200c9a66")!!
        val HWREVISION_CHARACTERISTIC_UUID = UUID.fromString("ce060013-43e5-11e4-916c-0800200c9a66")!!
        val FWREVISION_CHARACTERISTIC_UUID = UUID.fromString("ce060014-43e5-11e4-916c-0800200c9a66")!!
        val MANUFNAME_CHARACTERISTIC_UUID = UUID.fromString("ce060015-43e5-11e4-916c-0800200c9a66")!!
        val MACHINE_TYPE_CHARACTERISTIC_UUID = UUID.fromString("ce060016-43e5-11e4-916c-0800200c9a66")!!

        // Characteristic UUIDs for PM control service
        val TRANSMIT_TO_PM_CHARACTERISTIC_UUID = UUID.fromString("ce060021-43e5-11e4-916c-0800200c9a66")!!
        val RECEIVE_FROM_PM_CHARACTERISTIC_UUID = UUID.fromString("ce060022-43e5-11e4-916c-0800200c9a66")!!

        // Characteristic UUIDs for rowing service
        val ROWING_STATUS_CHARACTERISTIC_UUID = UUID.fromString("ce060031-43e5-11e4-916c-0800200c9a66")!!
        val EXTRA_ROWING_STATUS1_CHARACTERISTIC_UUID = UUID.fromString("ce060032-43e5-11e4-916c-0800200c9a66")!!
        val EXTRA_ROWING_STATUS2_CHARACTERISTIC_UUID = UUID.fromString("ce060033-43e5-11e4-916c-0800200c9a66")!!
        val ROWINGSTATUS_SAMPLERATE_CHARACTERISTIC_UUID = UUID.fromString("ce060034-43e5-11e4-916c-0800200c9a66")!!
        val STROKEDATA_CHARACTERISTIC_UUID = UUID.fromString("ce060035-43e5-11e4-916c-0800200c9a66")!!
        val EXTRA_STROKEDATA_CHARACTERISTIC_UUID = UUID.fromString("ce060036-43e5-11e4-916c-0800200c9a66")!!
        val SPLITINTERVAL_DATA_CHARACTERISTIC_UUID = UUID.fromString("ce060037-43e5-11e4-916c-0800200c9a66")!!
        val EXTRA_SPLITINTERVAL_DATA_CHARACTERISTIC_UUID = UUID.fromString("ce060038-43e5-11e4-916c-0800200c9a66")!!
        val ROWING_SUMMARY_CHARACTERISTIC_UUID = UUID.fromString("ce060039-43e5-11e4-916c-0800200c9a66")!!
        val EXTRA_ROWING_SUMMARY_CHARACTERISTIC_UUID = UUID.fromString("ce06003a-43e5-11e4-916c-0800200c9a66")!!
        val HEARTRATE_BELTINFO_CHARACTERISTIC_UUID = UUID.fromString("ce06003b-43e5-11e4-916c-0800200c9a66")!!
        val FORCE_CURVE_CHARACTERISTIC_UUID = UUID.fromString("ce06003d-43e5-11e4-916c-0800200c9a66")!!
    }

    private var mMachineTypeCharacteristic: BluetoothGattCharacteristic? = null
    private var mHardwareRevisionCharacteristic: BluetoothGattCharacteristic? = null
    private var mFirmwareRevisionCharacteristic: BluetoothGattCharacteristic? = null
    private var mModelNumberCharacteristic: BluetoothGattCharacteristic? = null
    private var mSerialNumberCharacteristic: BluetoothGattCharacteristic? = null

    private var mRowingStatusCharacteristic: BluetoothGattCharacteristic? = null
    private var mExtraRowingStatus1Characteristic: BluetoothGattCharacteristic? = null
    private var mExtraRowingStatus2Characteristic: BluetoothGattCharacteristic? = null
    private var mRowingStatusSampleRateCharacteristic: BluetoothGattCharacteristic? = null
    private var mStrokeDataCharacteristic: BluetoothGattCharacteristic? = null
    private var mExtraStrokeDataCharacteristic: BluetoothGattCharacteristic? = null
    private var mSplitIntervalDataCharacteristic: BluetoothGattCharacteristic? = null
    private var mExtraSplitIntervalDataCharacteristic: BluetoothGattCharacteristic? = null
    private var mRowingSummaryCharacteristic: BluetoothGattCharacteristic? = null
    private var mExtraRowingSummaryCharacteristic: BluetoothGattCharacteristic? = null
    private var mHeartRateBeltCharacteristic: BluetoothGattCharacteristic? = null
    private var mForceCurveCharacteristic: BluetoothGattCharacteristic? = null

    private var mTransmitCharacteristic: BluetoothGattCharacteristic? = null

    private val eventBus = EventBus.getDefault()

    private var firmwareRevision: String = ""
    private var hardwareRevision: String = ""

    override fun getGattCallback(): BleManagerGattCallback {
        Timber.d("** getGattCallback")
        return mGattCallback
    }

    override fun shouldAutoConnect() = false

    private val mGattCallback: BleManagerGattCallback = object : BleManagerGattCallback() {
        override fun initialize() {
            super.initialize()

            readCharacteristic(mFirmwareRevisionCharacteristic).enqueue()
            readCharacteristic(mHardwareRevisionCharacteristic).enqueue()

            enableNotifications(mRowingStatusCharacteristic).enqueue()
            enableNotifications(mExtraRowingStatus1Characteristic).enqueue()
            enableNotifications(mExtraRowingStatus2Characteristic).enqueue()
            enableNotifications(mStrokeDataCharacteristic).enqueue()
            enableNotifications(mExtraStrokeDataCharacteristic).enqueue()
            enableNotifications(mSplitIntervalDataCharacteristic).enqueue()
            enableNotifications(mExtraSplitIntervalDataCharacteristic).enqueue()
            enableNotifications(mRowingSummaryCharacteristic).enqueue()
            enableNotifications(mExtraRowingSummaryCharacteristic).enqueue()
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            when (characteristic) {
                mFirmwareRevisionCharacteristic -> firmwareRevision = characteristic.getStringValue(0)
                mHardwareRevisionCharacteristic -> hardwareRevision = characteristic.getStringValue(0)
                else -> Timber.d("** (Read) Unknown characteristic: ${characteristic.uuid}")
            }
        }

        override fun onCharacteristicIndicated(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Timber.d("** onCharacteristicIndicated ${characteristic.uuid}")
        }

        override fun onCharacteristicNotified(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            when (characteristic) {
                mRowingStatusCharacteristic -> Timber.d(RowingStatus.fromByteArray(characteristic.value).toString())
                mExtraRowingStatus1Characteristic -> Timber.d(ExtraRowingStatus1.fromByteArray(characteristic.value).toString())
                mExtraRowingStatus2Characteristic -> Timber.d(ExtraRowingStatus2.fromByteArray(characteristic.value).toString())

                mStrokeDataCharacteristic -> Timber.d(StrokeData.fromByteArray(characteristic.value).toString())
                mExtraStrokeDataCharacteristic -> Timber.d(ExtraStrokeData.fromByteArray(characteristic.value).toString())

                mSplitIntervalDataCharacteristic -> Timber.d(SplitIntervalData.fromByteArray(characteristic.value).toString())
                mExtraSplitIntervalDataCharacteristic -> Timber.d(ExtraSplitIntervalData.fromByteArray(characteristic.value).toString())

                mRowingSummaryCharacteristic -> Timber.d(RowingSummary.fromByteArray(characteristic.value).toString())
                mExtraRowingSummaryCharacteristic -> Timber.d(ExtraRowingSummary.fromByteArray(characteristic.value).toString())

                else -> Timber.d("** (Notification) Unknown characteristic: ${characteristic.uuid}")
            }
        }

        override fun onDeviceReady() {
            super.onDeviceReady()
            eventBus.post(DeviceConnected(bluetoothDevice))
        }

        override fun onDeviceDisconnected() {
            mHardwareRevisionCharacteristic = null
            mFirmwareRevisionCharacteristic = null

            mRowingStatusCharacteristic = null
            mExtraRowingStatus1Characteristic = null
            mExtraRowingStatus2Characteristic = null
            mRowingStatusSampleRateCharacteristic = null
            mStrokeDataCharacteristic = null
            mExtraStrokeDataCharacteristic = null
            mSplitIntervalDataCharacteristic = null
            mExtraSplitIntervalDataCharacteristic = null
            mRowingSummaryCharacteristic = null
            mExtraRowingSummaryCharacteristic = null
            mHeartRateBeltCharacteristic = null

            mTransmitCharacteristic = null

            eventBus.post(DeviceDisconnected(bluetoothDevice))
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            var supported = true

            val deviceInformationService = gatt.getService(PM_DEVICEINFO_SERVICE_UUID)
            if (deviceInformationService != null) {
                mHardwareRevisionCharacteristic = deviceInformationService.getCharacteristic(HWREVISION_CHARACTERISTIC_UUID)
                mFirmwareRevisionCharacteristic = deviceInformationService.getCharacteristic(FWREVISION_CHARACTERISTIC_UUID)

                supported = supported &&
                        mHardwareRevisionCharacteristic != null &&
                        mFirmwareRevisionCharacteristic != null
            }

            val rowingService = gatt.getService(PM_ROWING_SERVICE_UUID)
            if (rowingService != null) {
                mRowingStatusCharacteristic = rowingService.getCharacteristic(ROWING_STATUS_CHARACTERISTIC_UUID)
                mExtraRowingStatus1Characteristic = rowingService.getCharacteristic(EXTRA_ROWING_STATUS1_CHARACTERISTIC_UUID)
                mExtraRowingStatus2Characteristic = rowingService.getCharacteristic(EXTRA_ROWING_STATUS2_CHARACTERISTIC_UUID)
                mRowingStatusSampleRateCharacteristic = rowingService.getCharacteristic(ROWINGSTATUS_SAMPLERATE_CHARACTERISTIC_UUID)
                mStrokeDataCharacteristic = rowingService.getCharacteristic(STROKEDATA_CHARACTERISTIC_UUID)
                mExtraStrokeDataCharacteristic = rowingService.getCharacteristic(EXTRA_STROKEDATA_CHARACTERISTIC_UUID)
                mSplitIntervalDataCharacteristic = rowingService.getCharacteristic(SPLITINTERVAL_DATA_CHARACTERISTIC_UUID)
                mExtraSplitIntervalDataCharacteristic = rowingService.getCharacteristic(EXTRA_SPLITINTERVAL_DATA_CHARACTERISTIC_UUID)
                mRowingSummaryCharacteristic = rowingService.getCharacteristic(ROWING_SUMMARY_CHARACTERISTIC_UUID)
                mExtraRowingSummaryCharacteristic = rowingService.getCharacteristic(EXTRA_ROWING_SUMMARY_CHARACTERISTIC_UUID)
                mHeartRateBeltCharacteristic = rowingService.getCharacteristic(HEARTRATE_BELTINFO_CHARACTERISTIC_UUID)
                //mForceCurveCharacteristic = rowingService.getCharacteristic(FORCE_CURVE_CHARACTERISTIC_UUID)

                supported = supported &&
                        mRowingStatusCharacteristic != null &&
                        mExtraRowingStatus1Characteristic != null &&
                        mExtraRowingStatus2Characteristic != null &&
                        mRowingStatusSampleRateCharacteristic != null &&
                        mStrokeDataCharacteristic != null &&
                        mExtraStrokeDataCharacteristic != null &&
                        mSplitIntervalDataCharacteristic != null &&
                        mExtraSplitIntervalDataCharacteristic != null &&
                        mRowingSummaryCharacteristic != null &&
                        mExtraRowingSummaryCharacteristic != null &&
                        mHeartRateBeltCharacteristic != null
            }

            val controlService = gatt.getService(PM_CONTROL_SERVICE_UUID)
            if (controlService != null) {
                mTransmitCharacteristic = controlService.getCharacteristic(TRANSMIT_TO_PM_CHARACTERISTIC_UUID)
            }

            var writeRequest = false
            if (mTransmitCharacteristic != null) {
                val rxProperties = mTransmitCharacteristic!!.properties
                writeRequest = rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0
            }

            return supported && writeRequest
        }

    }
}