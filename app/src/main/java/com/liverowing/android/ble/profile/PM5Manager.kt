package com.liverowing.android.ble.profile

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.Request
import timber.log.Timber
import java.util.*


class PM5Manager(context: Context) : BleManager<PM5ManagerCallbacks>(context) {
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

    private var mRowingStatusCharacteristic: BluetoothGattCharacteristic? = null
    private var mExtraStatus1Characteristic: BluetoothGattCharacteristic? = null
    private var mExtraStatus2Characteristic: BluetoothGattCharacteristic? = null
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

    private val mGattCallback: BleManagerGattCallback = object: BleManagerGattCallback() {
        override fun initGatt(gatt: BluetoothGatt): Deque<Request> {
            val requests = LinkedList<Request>()
            requests.push(Request.newReadRequest(mMachineTypeCharacteristic))
            requests.push(Request.newReadRequest(mHardwareRevisionCharacteristic))
            requests.push(Request.newReadRequest(mFirmwareRevisionCharacteristic))

            requests.push(Request.newEnableNotificationsRequest(mRowingStatusCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mExtraStatus1Characteristic))
            requests.push(Request.newEnableNotificationsRequest(mExtraStatus2Characteristic))
            requests.push(Request.newEnableNotificationsRequest(mStrokeDataCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mExtraStrokeDataCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mSplitIntervalDataCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mExtraSplitIntervalDataCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mRowingSummaryCharacteristic))
            requests.push(Request.newEnableNotificationsRequest(mExtraRowingSummaryCharacteristic))
            return requests
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            var supported = true

            val deviceInformationService = gatt.getService(PM_DEVICEINFO_SERVICE_UUID)
            if (deviceInformationService != null) {
                //mMachineTypeCharacteristic = deviceInformationService.getCharacteristic(MACHINE_TYPE_CHARACTERISTIC_UUID) // Does not exist?
                mHardwareRevisionCharacteristic = deviceInformationService.getCharacteristic(HWREVISION_CHARACTERISTIC_UUID)
                mFirmwareRevisionCharacteristic = deviceInformationService.getCharacteristic(FWREVISION_CHARACTERISTIC_UUID)

                supported = supported &&
                        mHardwareRevisionCharacteristic != null &&
                        mFirmwareRevisionCharacteristic != null

                Timber.d("Supported: $supported")
            }

            val rowingService = gatt.getService(PM_ROWING_SERVICE_UUID)
            if (rowingService != null) {
                mRowingStatusCharacteristic = rowingService.getCharacteristic(ROWING_STATUS_CHARACTERISTIC_UUID)
                mExtraStatus1Characteristic = rowingService.getCharacteristic(EXTRA_ROWING_STATUS1_CHARACTERISTIC_UUID)
                mExtraStatus2Characteristic = rowingService.getCharacteristic(EXTRA_ROWING_STATUS2_CHARACTERISTIC_UUID)
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
                        mExtraStatus1Characteristic != null &&
                        mExtraStatus2Characteristic != null &&
                        mRowingStatusSampleRateCharacteristic != null &&
                        mStrokeDataCharacteristic != null &&
                        mExtraStrokeDataCharacteristic != null &&
                        mSplitIntervalDataCharacteristic != null &&
                        mExtraSplitIntervalDataCharacteristic != null &&
                        mRowingSummaryCharacteristic != null &&
                        mExtraRowingSummaryCharacteristic != null &&
                        mHeartRateBeltCharacteristic != null

                Timber.d("Supported: $supported")
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
            Timber.d("Supported: $supported")
            Timber.d("WriteRequest: $writeRequest")

            return supported && writeRequest
        }

        override fun onDeviceDisconnected() {
            mMachineTypeCharacteristic = null
            mHardwareRevisionCharacteristic = null
            mFirmwareRevisionCharacteristic = null

            mRowingStatusCharacteristic = null
            mExtraStatus1Characteristic = null
            mExtraStatus2Characteristic = null
            mRowingStatusSampleRateCharacteristic = null
            mStrokeDataCharacteristic = null
            mExtraStrokeDataCharacteristic = null
            mSplitIntervalDataCharacteristic = null
            mExtraSplitIntervalDataCharacteristic = null
            mRowingSummaryCharacteristic = null
            mExtraRowingSummaryCharacteristic = null
            mHeartRateBeltCharacteristic = null
            mForceCurveCharacteristic = null

            mTransmitCharacteristic = null
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            Timber.d("onCharacteristicRead($gatt, $characteristic)")
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            Timber.d("onCharacteristicWrite($gatt, $characteristic)")
        }

        override fun onCharacteristicNotified(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            Timber.d("onCharacteristicNotified($gatt, $characteristic)")
        }
    }

    override fun shouldAutoConnect() = true

    override fun getGattCallback(): BleManagerGattCallback {
        return mGattCallback
    }
}