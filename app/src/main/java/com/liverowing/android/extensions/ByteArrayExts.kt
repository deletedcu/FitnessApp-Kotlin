package com.liverowing.android.extensions

import java.util.*

const val FORMAT_UINT8 = 0x11
const val FORMAT_UINT16 = 0x12
const val FORMAT_UINT32 = 0x14
const val FORMAT_SINT8 = 0x21
const val FORMAT_SINT16 = 0x22
const val FORMAT_SINT32 = 0x24


fun ByteArray.getIntValue(formatType: Int, offset: Int): Int {
    if (offset + getTypeLen(formatType) > this.size) return 0

    when (formatType) {
        FORMAT_UINT8 -> return unsignedByteToInt(this[offset])

        FORMAT_UINT16 -> return unsignedBytesToInt(this[offset], this[offset + 1])

        FORMAT_UINT32 -> return unsignedBytesToInt(this[offset], this[offset + 1],
                this[offset + 2], this[offset + 3])
        FORMAT_SINT8 -> return unsignedToSigned(unsignedByteToInt(this[offset]), 8)

        FORMAT_SINT16 -> return unsignedToSigned(unsignedBytesToInt(this[offset],
                this[offset + 1]), 16)

        FORMAT_SINT32 -> return unsignedToSigned(unsignedBytesToInt(this[offset],
                this[offset + 1], this[offset + 2], this[offset + 3]), 32)

        else -> return 0
    }
}

fun ByteArray.calcLogEntryDateTime(offset: Int) : Date {
    return Date()
}

fun ByteArray.calcTime(offset: Int) : Double {
    val time = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8) or (getIntValue(FORMAT_UINT8, offset + 2) shl 16)).toDouble()

    // Time is in 0.01 sec resolution
    return time / 100
}

fun ByteArray.calcSplitTime(offset: Int): Double {
    val time = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8) or (getIntValue(FORMAT_UINT8, offset + 2) shl 16)).toDouble()

    // Split time is in 0.1 sec resolution
    return time / 10
}

fun ByteArray.calcDistance(offset: Int): Double {
    val distance = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8) or (getIntValue(FORMAT_UINT8, offset + 2) shl 16)).toDouble()

    // Distance is in 0.1 m resolution
    return distance / 10
}

fun ByteArray.calcSplitDistance(offset: Int): Double {
    val distance = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8)).toDouble()

    // Split distance is in 1 m resolution
    return distance
}

fun ByteArray.calcRestTime(offset: Int): Double {
    val time = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8)).toDouble()

    // Rest time is in 1 sec resolution
    return time
}

fun ByteArray.calcWorkoutDurationDistance(offset: Int): Double {
    val distance = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8)).toDouble()

    // Workout duration distance is in 1 m resolution
    return distance
}

fun ByteArray.calcSpeed(offset: Int): Double {
    val speed = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8)).toDouble()

    // Speed is in 0.001 m/s resolution
    return speed / 1000
}

fun ByteArray.calcCalories(offset: Int): Double {
    val calories = (getIntValue(FORMAT_UINT8, offset) or (getIntValue(FORMAT_UINT8, offset + 1) shl 8)).toDouble()

    return calories
}


/**
 * Returns the size of a give value type.
 */
private fun getTypeLen(formatType: Int): Int {
    return formatType and 0xF
}

/**
 * Convert a signed byte to an unsigned int.
 */
private fun unsignedByteToInt(b: Byte): Int {
    return b.toInt() and 0xFF
}

/**
 * Convert signed bytes to a 16-bit unsigned int.
 */
private fun unsignedBytesToInt(b0: Byte, b1: Byte): Int {
    return unsignedByteToInt(b0) + (unsignedByteToInt(b1) shl 8)
}

/**
 * Convert signed bytes to a 32-bit unsigned int.
 */
private fun unsignedBytesToInt(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
    return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) shl 8)
            + (unsignedByteToInt(b2) shl 16) + (unsignedByteToInt(b3) shl 24))
}

/**
 * Convert an unsigned integer value to a two's-complement encoded
 * signed value.
 */
private fun unsignedToSigned(unsigned: Int, size: Int): Int {
    var unsigned = unsigned
    if (unsigned and (1 shl size - 1) != 0) {
        unsigned = -1 * ((1 shl size - 1) - (unsigned and (1 shl size - 1) - 1))
    }
    return unsigned
}
