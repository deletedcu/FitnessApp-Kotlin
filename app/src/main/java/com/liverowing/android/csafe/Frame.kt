package com.liverowing.android.csafe
import android.util.Log
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import kotlin.experimental.xor

class Frame(val type: Byte = CSAFE_STANDARD_START_FLAG) {
    private val buffer = ByteArrayOutputStream()

    companion object {
        const val CSAFE_CTRL_CMD_SHORT_MIN = 0x80

        const val CSAFE_EXTENDED_START_FLAG = 0xF0.toByte()
        const val CSAFE_STANDARD_START_FLAG = 0xF1.toByte()
        const val CSAFE_STOP_FLAG = 0xF2.toByte()
        const val CSAFE_STUFF_FLAG = 0xF3.toByte()

        const val CSAFE_FRAME_STUFF_BYTE = 0xF3
    }

    fun addCommand(cmd: Command) {
        val b = ByteArrayOutputStream()
        buffer.write(cmd.command)
        b.write(cmd.command)
        if (cmd.command >= CSAFE_CTRL_CMD_SHORT_MIN) {
            if (cmd.detailCommand != null || cmd.data != null) {
                throw Exception("short commands can not contain data or a detail command")
            }
        } else {
            if (cmd.detailCommand != null) {
                var dataLength = 1
                if (cmd.data != null && cmd.data.isNotEmpty()) {
                    dataLength += cmd.data.size + 1
                }

                b.write(dataLength)
                b.write(cmd.detailCommand)
                buffer.write(dataLength)
                buffer.write(cmd.detailCommand)
            }

            if (cmd.data != null && cmd.data.isNotEmpty()) {
                buffer.write(cmd.data.size)
                cmd.data.forEach({ buffer.write(it); b.write(it) })
            }
        }

        Log.d("LiveRowing", b.toByteArray().contentToString())
    }

    fun formattedFrame(): ByteArray {
        val frame = ByteArrayOutputStream()
        var checksum: Byte = 0x00
        val byteArray = buffer.toByteArray()

        frame.write(type.toInt())
        for (b in byteArray) {
            checksum = checksum xor b

            if (b == CSAFE_EXTENDED_START_FLAG ||
                    b == CSAFE_STANDARD_START_FLAG ||
                    b == CSAFE_STOP_FLAG ||
                    b == CSAFE_STUFF_FLAG) {
                frame.write(CSAFE_FRAME_STUFF_BYTE)
                frame.write(b.toInt() - CSAFE_EXTENDED_START_FLAG)
            } else {
                frame.write(b.toInt())
            }
        }

        if (checksum == CSAFE_EXTENDED_START_FLAG ||
                checksum == CSAFE_STANDARD_START_FLAG ||
                checksum == CSAFE_STOP_FLAG ||
                checksum == CSAFE_STUFF_FLAG) {
            frame.write(CSAFE_FRAME_STUFF_BYTE)
            frame.write(checksum.toInt() - CSAFE_EXTENDED_START_FLAG)
        } else {
            frame.write(checksum.toInt())
        }

        frame.write(CSAFE_STOP_FLAG.toInt())

        return frame.toByteArray()
    }

    fun unstuff() : ByteArray {
        return buffer.toByteArray()
    }

    data class Command(val command: Int, val detailCommand: Int?, val data: List<Int>?)
}