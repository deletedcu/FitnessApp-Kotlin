package com.liverowing.liverowing.csafe

/**
 * Created by henrikmalmberg on 2017-11-05.
 */
class Communication {
    companion object {
        const val CSAFE_EXTENDED_START_FLAG = 0xF0
        const val CSAFE_STANDARD_START_FLAG = 0xF1
        const val CSAFE_STOP_FLAG = 0xF2
        const val CSAFE_STUFF_FLAG = 0xF3

        const val CSAFE_SETUSERCFG1_CMD = 0x1A
        const val CSAFE_SETPMCFG_CMD = 0x76

        const val CSAFE_PM_SET_WORKOUTTYPE = 0x01

        const val CSAFE_PM_SET_SCREENSTATE = 0x13
        const val CSAFE_PM_CONFIGURE_WORKOUT = 0x14

        const val SCREENTYPE_WORKOUT = 1

        const val SCREENVALUEWORKOUT_PREPARETOROWWORKOUT = 1

        const val CTRL_CMD_SHORT_MIN = 128
    }

    fun justRow(): ByteArray {
        val commands = listOf(
                Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_WORKOUTTYPE, listOf(1)),
                Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)),
                Command(CSAFE_SETPMCFG_CMD, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT))
        )

        return wrap(commands)
    }

    fun wrap(commands: List<Command>): ByteArray {
        val buffer = mutableListOf<Int>()

        for (command in commands) {
            buffer.add(command.command)

            if (command.command >= CTRL_CMD_SHORT_MIN) {
                if (command.detailCommand != null || command.data != null) {
                    throw Exception("short commands can not contain data or a detail command")
                }
            } else {
                if (command.detailCommand != null) {
                    var dataLength = 1
                    if (command.data != null && command.data.isNotEmpty()) {
                        dataLength += command.data.size + 1
                    }

                    buffer.add(dataLength)
                    buffer.add(command.detailCommand)

                }

                if (command.data != null && command.data.isNotEmpty()) {
                    buffer.add(command.data.size)
                    buffer.addAll(command.data)
                }
            }
        }

        val wrapped = ByteArray(buffer.size + 3)
        wrapped[0] = CSAFE_STANDARD_START_FLAG.toByte()
        buffer.forEachIndexed { index, item -> wrapped[index+1] = item.toByte() }
        wrapped[buffer.size+1] = checksum(buffer).toByte()
        wrapped[buffer.size+2] = CSAFE_STOP_FLAG.toByte()

        //Log.d("LiveRowing", wrapped.asList().toString())
        return wrapped
    }

    fun checksum(bytes: List<Int>) : Int {
        var checksum = 0
        for (i in bytes) checksum = checksum xor i
        return checksum
    }

    data class Command(val command: Int, val detailCommand: Int?, val data: List<Int>?)
}