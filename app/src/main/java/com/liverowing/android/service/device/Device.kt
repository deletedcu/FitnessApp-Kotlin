package com.liverowing.android.service.device

import com.liverowing.android.model.parse.WorkoutType

/**
 * Created by henrikmalmberg on 2017-12-17.
 */
abstract class Device {
    abstract var name: String
    abstract var connected: Boolean

    abstract fun connect()
    abstract fun disconnect()
    abstract fun setupWorkout(workoutType: WorkoutType, targetPace: Int?)

    companion object {
        const val CSAFE_SETPMCFG_CMD = 0x76

        const val CSAFE_PM_SET_WORKOUTDURATION = 0x03

        const val CSAFE_PM_SET_WORKOUTTYPE = 0x01
        const val CSAFE_PM_SET_RESTDURATION = 0x04
        const val CSAFE_PM_SET_SPLITDURATION = 0x05
        const val CSAFE_PM_SET_TARGETPACETIME = 0x06
        const val CSAFE_PM_SET_SCREENSTATE = 0x13
        const val CSAFE_PM_CONFIGURE_WORKOUT = 0x14
        const val CSAFE_PM_SET_INTERVALTYPE = 0x17
        const val CSAFE_PM_SET_WORKOUTINTERVALCOUNT = 0x18

        const val SCREENTYPE_WORKOUT = 0x01
        const val SCREENVALUEWORKOUT_PREPARETOROWWORKOUT = 0x01
        const val SCREENVALUEWORKOUT_TERMINATEWORKOUT = 0x02

        const val PM_TIME_TYPE = 0
        const val PM_DISTANCE_TYPE = 128

    }
}