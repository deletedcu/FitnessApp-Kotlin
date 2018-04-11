package com.liverowing.liverowing.model.pm

/**
 * Created by henrikmalmberg on 2017-11-03.
 */

enum class PMErgMachineType(val value: Int) {
    STATIC_D(0),
    STATIC_C(1),
    STATIC_A(2),
    STATIC_B(3),
    STATIC_E(4),
    SLIDES_C(5),
    SLIDES_D(6),
    SLIDES_E(7),
    STATIC_DYNAMIC(8),
    SLIDES_A(16),
    SLIDES_B(17),
    SLIDES_DYNAMIC(32),
    STATIC_DYNO(64),
    STATIC_SKI(128);

    companion object {
        private val map = PMErgMachineType.values().associateBy(PMErgMachineType::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class WorkoutType (val value: Int) {
    JUSTROW_NOSPLITS(0),
    JUSTROW_SPLITS(1),
    FIXEDDIST_NOSPLITS(2),
    FIXEDDIST_SPLITS(3),
    FIXEDTIME_NOSPLITS(4),
    FIXEDTIME_SPLITS(5),
    FIXEDTIME_INTERVAL(6),
    FIXEDDIST_INTERVAL(7),
    VARIABLE_INTERVAL(8),
    VARIABLE_UNDEFINEDREST_INTERVAL(9),
    FIXED_CALORIE(10),
    FIXED_WATTMINUTES(11);

    companion object {
        private val map = WorkoutType.values().associateBy(WorkoutType::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class IntervalType (val value: Int) {
    TIME(0),
    DIST(1),
    REST(2),
    TIMERESTUNDEFINED(3),
    DISTANCERESTUNDEFINED(4),
    RESTUNDEFINED(5),
    CAL(6),
    CALRESTUNDEFINED(7),
    WATTMINUTE(8),
    WATTMINUTERESTUNDEFINED(9),
    NONE(255);

    companion object {
        private val map = IntervalType.values().associateBy(IntervalType::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class WorkoutState (val value: Int) {
    WAITTOBEGIN(0),
    WORKOUTROW(1),
    COUNTDOWNPAUSE(2),
    INTERVALREST(3),
    INTERVALWORKTIME(4),
    INTERVALWORKDISTANCE(5),
    INTERVALRESTENDTOWORKTIME(6),
    INTERVALRESTENDTOWORKDISTANCE(7),
    INTERVALWORKTIMETOREST(8),
    INTERVALWORKDISTANCETOREST(9),
    WORKOUTEND(10),
    TERMINATE(11),
    WORKOUTLOGGED(12),
    REARM(13);

    companion object {
        private val map = WorkoutState.values().associateBy(WorkoutState::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class RowingState (val value: Int) {
    INACTIVE(0),
    ACTIVE(1);

    companion object {
        private val map = RowingState.values().associateBy(RowingState::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class StrokeState (val value: Int) {
    WAITING_FOR_WHEEL_TO_REACH_MIN_SPEED_STATE(0),
    WAITING_FOR_WHEEL_TO_ACCELERATE_STATE(1),
    DRIVING_STATE(2),
    DWELLING_AFTER_DRIVE_STATE(3),
    RECOVERY_STATE(4);

    companion object {
        private val map = StrokeState.values().associateBy(StrokeState::value)
        fun fromInt(type: Int) = map[type]!!
    }
}

enum class DurationType (val value: Int) {
    TIME(0),
    CALORIES(0x40),
    DISTANCE(0x80),
    WATTS(0xC0);

    companion object {
        private val map = DurationType.values().associateBy(DurationType::value)
        fun fromInt(type: Int) = map[type]!!
    }
}