package com.liverowing.android.model.pm

import com.liverowing.android.extensions.FORMAT_UINT16
import com.liverowing.android.extensions.FORMAT_UINT8
import com.liverowing.android.extensions.calcTime
import com.liverowing.android.extensions.getIntValue
/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class ExtraStrokeData(val elapsedTime: Double,
                           val power: Int,
                           val calories: Int,
                           val strokeCount: Int,
                           val projWorkTime: Int,
                           val projWorkDist: Int
) {
    companion object {
        fun fromByteArray(data: ByteArray): ExtraStrokeData {
            val elapsedTime = data.calcTime(0)
            val strokePower = data.getIntValue(FORMAT_UINT16, 3)
            val strokeCalories = data.getIntValue(FORMAT_UINT16, 5)
            val strokeCount = data.getIntValue(FORMAT_UINT16, 7)
            val projWorkTime = (data.getIntValue(FORMAT_UINT8, 9) or (data.getIntValue(FORMAT_UINT8, 10) shl 8) or (data.getIntValue(FORMAT_UINT8, 11) shl 16))
            val projWorkDist = (data.getIntValue(FORMAT_UINT8, 12) or (data.getIntValue(FORMAT_UINT8, 13) shl 8) or (data.getIntValue(FORMAT_UINT8, 14) shl 16))

            return ExtraStrokeData(
                    elapsedTime, strokePower, strokeCalories, strokeCount, projWorkTime, projWorkDist
            )
        }
    }
}