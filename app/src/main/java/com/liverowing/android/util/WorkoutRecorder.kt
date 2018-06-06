package com.liverowing.android.util

import android.util.Base64
import android.util.Log
import com.liverowing.android.model.parse.*
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.android.model.pm.*
import com.parse.ParseFile
import com.parse.ParseUser
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class WorkoutRecorder(val workoutType: WorkoutType, private val personalBest: Workout?, private val opponent: Workout?, private val affiliate: Affiliate?) {
    private var mWorkout: Workout = Workout()
    private var mWorkoutStarted = false
    private var mResting = false
    private var mWorkoutFinished = false
    private var mCurrentIntervalOrSplit = 0

    private var mPersonalBestLastIndex = 0
    private var mOpponentLastIndex = 0

    private var mDataPoints = arrayListOf<Workout.DataPoint>()
    private var mStrokes = arrayListOf<Workout.Stroke>()

    var splits = arrayListOf<Workout.Split>()

    var workoutListener: Callback? = null

    init {
        mWorkout.createdBy = ParseUser.getCurrentUser() as User
        mWorkout.workoutType = workoutType
        mWorkout.affiliate = affiliate
        mWorkout.pbWorkout = personalBest
        mWorkout.withPersonalBest = personalBest is Workout
        mWorkout.isChallenge = opponent is Workout
        mWorkout.isDone = false

        EventBus.getDefault().register(this)
    }

    private fun getPlaybackPosition(workout: Workout, lastIndex: Int, data: RowingStatus): Int {
        for (index in lastIndex until workout.playbackData.size) {
            val playbackData = workout.playbackData[index]
            if (playbackData.time > data.elapsedTime) {
                return index
            }
        }

        return lastIndex
    }

    private fun normalizePlaybackDistance(time: Double, data: List<Workout.DataPoint>, index: Int): Workout.DataPoint {
        return data[index]
        /*
        val lastData = if (index == 0) data[0].copy(time = 0.0) else data[index - 1]
        val normalizedMeters = lastData.meters + (((data[index].time - lastData.time) / (data[index].meters - lastData.meters)) * (time - lastData.time))
        return data[index].copy(meters = normalizedMeters)
        */
    }

    private var lastRowingStatus: RowingStatus? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRowingStatus(data: RowingStatus) {
        lastRowingStatus = data

        //Log.d("LiveRowing", data.toString())
        when (data.workoutState) {
            WorkoutState.WORKOUTROW -> {
                if (!mWorkoutStarted) {
                    mWorkoutStarted = true
                    workoutStarted(data)
                }
                logDataPoint()
            }

            WorkoutState.INTERVALREST -> {
                if (!mResting) {
                    mResting = true
                    workoutResting(data)
                }
                logDataPoint()
            }

            WorkoutState.INTERVALWORKTIME,
            WorkoutState.INTERVALWORKDISTANCE -> {
                if (!mWorkoutStarted) {
                    mWorkoutStarted = true
                    workoutStarted(data)
                } else {
                    logDataPoint()
                    if (mResting) {
                        mResting = false
                        workoutListener?.workoutContinue()
                    }
                }
            }

            WorkoutState.WORKOUTEND -> {
                if (!mWorkoutFinished) {
                    logDataPoint()
                    workoutFinished()
                }
            }

            WorkoutState.TERMINATE -> {
                if (!mWorkoutFinished) {
                    workoutTerminated()
                }
            }

            WorkoutState.WAITTOBEGIN,
            WorkoutState.COUNTDOWNPAUSE,
            WorkoutState.INTERVALRESTENDTOWORKTIME,
            WorkoutState.INTERVALRESTENDTOWORKDISTANCE,
            WorkoutState.INTERVALWORKTIMETOREST,
            WorkoutState.INTERVALWORKDISTANCETOREST,
            WorkoutState.WORKOUTLOGGED,
            WorkoutState.REARM -> Log.d("LiveRowing", "WorkoutState = ${data.workoutState}")
        }

        if (mWorkoutStarted) {
            var opponentPlaybackData: Workout.DataPoint? = null
            var personalBestPlaybackData: Workout.DataPoint? = null
            if (personalBest is Workout && personalBest.playbackData.size > 0) {
                mPersonalBestLastIndex = getPlaybackPosition(personalBest, mPersonalBestLastIndex, data)
                personalBestPlaybackData = normalizePlaybackDistance(data.elapsedTime, personalBest.playbackData, mPersonalBestLastIndex)
            }

            if (opponent is Workout && opponent.playbackData.size > 0) {
                mOpponentLastIndex = getPlaybackPosition(opponent, mOpponentLastIndex, data)
                opponentPlaybackData = normalizePlaybackDistance(data.elapsedTime, opponent.playbackData, mOpponentLastIndex)
            }

            workoutListener?.workoutRowingStatus(mCurrentIntervalOrSplit, data, opponentPlaybackData, personalBestPlaybackData)
        }
    }

    private fun logDataPoint(fromStrokeData: Boolean = false) {
        val dp = Workout.DataPoint(
                workoutState = lastRowingStatus!!.workoutState,
                caloriesBurned = lastAdditionalStrokeData!!.calories,
                interval = mCurrentIntervalOrSplit,
                meters = lastRowingStatus!!.distance,
                splitTime = lastAdditionalRowingStatus1!!.currentPace.toDouble(),
                strokesPerMinute = lastAdditionalRowingStatus1!!.strokeRate,
                strokeLength = (lastStrokeData.strokeDistance * 10).toInt(),
                watts = lastAdditionalStrokeData!!.power,
                split = mCurrentIntervalOrSplit,
                dragFactor = lastRowingStatus!!.dragFactor,
                heartRate = if (lastAdditionalRowingStatus1!!.heartRate == 255) 0 else lastAdditionalRowingStatus1!!.heartRate,
                strokeTime = (lastStrokeData.driveTime * 100).toInt(),
                time = lastRowingStatus!!.elapsedTime,
                timestamp = System.currentTimeMillis() / 1000,
                isRow = fromStrokeData
        )

        mDataPoints.add(dp)
    }

    private var lastAdditionalRowingStatus1: AdditionalRowingStatus1? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalRowingStatus1(data: AdditionalRowingStatus1) {
        lastAdditionalRowingStatus1 = data
        workoutListener?.workoutAdditionalRowingStatus1(mCurrentIntervalOrSplit, data)
    }

    private var lastAdditionalRowingStatus2: AdditionalRowingStatus2? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalRowingStatus2(data: AdditionalRowingStatus2) {
        lastAdditionalRowingStatus2 = data

        if (mCurrentIntervalOrSplit != data.intervalCount) {
            mCurrentIntervalOrSplit = data.intervalCount
        }

        workoutListener?.workoutAdditionalRowingStatus2(mCurrentIntervalOrSplit, data)
    }

    private var lastStrokeCountInWorkPeriod = 0
    private lateinit var lastStrokeData: StrokeData
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStrokeData(data: StrokeData) {
        lastStrokeData = data

        if (lastRowingStatus!!.workoutState != WorkoutState.INTERVALREST) {
            lastStrokeCountInWorkPeriod = data.strokeCount
        }

        logDataPoint(true)

        // TODO: If custom workout, use total time, not elapsedTime
        mStrokes.add(
                Workout.Stroke(
                        (data.elapsedTime * 10).toInt(),
                        (data.distance * 10).toInt(),
                        (lastAdditionalRowingStatus2!!.splitIntAvgPace * 10).toInt(),
                        lastAdditionalRowingStatus1!!.strokeRate,
                        if (lastAdditionalRowingStatus1!!.heartRate == 255) 0 else lastAdditionalRowingStatus1!!.heartRate
                )
        )

        workoutListener?.workoutStrokeData(mCurrentIntervalOrSplit, data)
    }

    private var lastAdditionalStrokeData: AdditionalStrokeData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalStrokeData(data: AdditionalStrokeData) {
        lastAdditionalStrokeData = data
        workoutListener?.workoutAdditionalStrokeData(mCurrentIntervalOrSplit, data)
    }

    private var lastSplitIntervalData: SplitIntervalData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSplitIntervalData(data: SplitIntervalData) {
        lastSplitIntervalData = data
        workoutListener?.workoutSplitIntervalData(data.intervalNumber, data)
    }

    private var lastAdditionalSplitIntervalData: AdditionalSplitIntervalData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalSplitIntervalData(data: AdditionalSplitIntervalData) {
        lastAdditionalSplitIntervalData = data

        var splitStrokeCount = 0
        if (workoutType.valueType == VALUE_TYPE_CUSTOM) {
            splitStrokeCount =  lastStrokeCountInWorkPeriod
        } else {
            val previousStrokeCount = splits.sumBy { it.splitStrokeCount }
            splitStrokeCount =  lastStrokeCountInWorkPeriod - previousStrokeCount
        }

        val splitNumber = if (workoutType.valueType == VALUE_TYPE_CUSTOM) data.intervalNumber else data.intervalNumber-1
        val splitAvgDPS = lastSplitIntervalData!!.distance / splitStrokeCount

        // Distance workouts: tenths of seconds, Time workouts: meters
        val splitTimeDistance = when (lastSplitIntervalData!!.intervalType) {
            IntervalType.TIME -> lastSplitIntervalData!!.distance
            IntervalType.DIST -> lastSplitIntervalData!!.elapsedTime * 10
            else -> 0.0
        }

        val splitAvgDriveLength =
                mDataPoints
                        .filter { it.workoutState != WorkoutState.INTERVALREST && it.isRow && it.split == data.intervalNumber-1 }
                        .map { it.strokeLength }
                        .average()
                        .toString()

        splits.add(Workout.Split(
                splitDistance = lastSplitIntervalData!!.distance,
                splitHeartRate = data.workHeartRate.toDouble(),
                splitStrokeRate = data.spm,
                splitTimeDistance = splitTimeDistance,
                splitRestDistance = lastSplitIntervalData!!.restDistance.toDouble(),
                splitRestTime = lastSplitIntervalData!!.restTime,
                splitAvgDPS = splitAvgDPS,
                splitCals = data.calories.toDouble(),
                splitTime = data.elapsedTime,
                splitAvgWatts = data.power.toDouble(),
                splitAvgDragFactor = data.averageDragFactor,
                splitAvgPace = data.pace.toDouble(),
                splitAvgDriveLength = splitAvgDriveLength.toDouble(),
                splitStrokeCount = splitStrokeCount,
                splitNumber = splitNumber
        ))

        workoutListener?.workoutAdditionalSplitIntervalData(data.intervalNumber, data)
    }

    private lateinit var workoutSummary: WorkoutSummary
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWorkoutSummary(data: WorkoutSummary) {
        Log.d("LiveRowing", data.toString())
        workoutSummary = data
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalWorkoutSummary(data: AdditionalWorkoutSummary) {
        EventBus.getDefault().unregister(this)

        val workTimeDataPoints = mDataPoints.filter { it.workoutState != WorkoutState.INTERVALREST }

        val maxSPM = workTimeDataPoints.maxBy { it.strokesPerMinute }!!.strokesPerMinute
        val maxWatt = workTimeDataPoints.maxBy { it.watts }!!.watts
        val fastestPace = workTimeDataPoints.minBy { it.splitTime }!!.splitTime
        val splitsDistance = splits.sumByDouble { it.splitDistance }
        val splitsStrokeCount = splits.sumBy { it.splitStrokeCount }
        val splitAvgDPS = (splitsDistance / splitsStrokeCount)
        val maxHeartRate = workTimeDataPoints.maxBy { it.heartRate }!!.heartRate
        val avgDriveLength =
                mDataPoints
                        .filter { it.isRow }
                        .map { it.strokeLength }
                        .average()


        val workoutData = Workout.WorkoutData(
                maxSPM = maxSPM,
                maxWatt = maxWatt,
                heartRate = workoutSummary.averageHeartRate,
                SplitsWatts = data.watts.toDouble(),
                splitType = data.type.value, // TODO: Really?
                fastestPace = fastestPace,
                SplitsAvgDPS = splitAvgDPS,
                strokeRate = workoutSummary.averageSpm,
                SplitsAvgDrag = workoutSummary.averageDragFactor.toDouble(),
                SplitsCals = data.calories.toDouble(),
                maxHeartRate = maxHeartRate.toDouble(),
                splitsAvgPace = workoutSummary.averagePace.toDouble(),
                strokeCount = lastStrokeData.strokeCount,
                workTime = workoutSummary.elapsedTime,
                SplitsAvgDriveLength = avgDriveLength,
                workDistance = workoutSummary.distance.roundToInt(),
                heartRateNormalZoneTime = 0.0, // TODO: Implement
                heartRateZone1Time = 0.0, // TODO: Implement
                heartRateZone2Time = 0.0, // TODO: Implement
                heartRateZone3Time = 0.0,  // TODO: Implement
                splitSize = workoutType.calculatedSplitLength,
                splits = splits
        )

        val dataPointWrapper = Workout.DataPointWrapper(mDataPoints)
        val datapoints = Base64.encode(JSON.stringify(dataPointWrapper).toByteArray(), Base64.DEFAULT)
        val file = ParseFile(datapoints)
        file.save()

        val avgSplitTime =
                splits
                        .map { it.splitTime }
                        .average()

        val calendar = Calendar.getInstance()
        val endTime = calendar.time

        mWorkout.dataPoints = file
        mWorkout.dragFactor = workoutSummary.averageDragFactor
        mWorkout.averageHeartRate = workoutSummary.averageHeartRate
        mWorkout.meters = workoutSummary.distance.roundToInt()
        mWorkout.averageSplitTime = avgSplitTime.toFloat()
        mWorkout.averageWatts = data.watts
        mWorkout.isDone = true
        mWorkout.totalTime = TimeUnit.MILLISECONDS.toSeconds(endTime.time - mWorkout.startTime!!.time).toInt()
        mWorkout.averageSPM = workoutSummary.averageSpm
        mWorkout.totalStrokeCount = lastStrokeCountInWorkPeriod // TODO: Probably wrong
        mWorkout.caloriesBurned = data.calories
        mWorkout.duration = workoutSummary.elapsedTime

        mWorkout.data = Workout.Data(workoutData, mStrokes,"standard")
        mWorkout.save()
    }


    private fun workoutStarted(data: RowingStatus) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, -(data.elapsedTime * 1000).toInt())
        mWorkout.startTime = calendar.time

        workoutListener?.workoutStarted()
    }

    private fun workoutResting(data: RowingStatus) {
        workoutListener?.workoutResting()
    }

    private fun workoutFinished() {
        Log.d("LiveRowing", "Splits: $splits")
        var json = "["
        mStrokes.forEach {
            json += "{\"d\": ${it.d}, \"p\": ${it.p}, \"hr\": ${it.hr}, \"spm\": ${it.spm}, \"t\": ${it.t}},"
        }
        json = json.substring(0, json.length - 1) + "]"

        Log.d("LiveRowing", "Strokes: $json")
        mWorkoutFinished = true
        mWorkout.apply {
            finishTime = Calendar.getInstance().time
            isDone = true
        }

        workoutListener?.workoutFinished()
    }

    private fun workoutTerminated() {
        mWorkoutFinished = true
        mWorkout.apply {
            finishTime = Calendar.getInstance().time
            isDone = false
        }

        EventBus.getDefault().unregister(this)
        workoutListener?.workoutTerminated()
    }

    interface Callback {
        fun workoutStarted()
        fun workoutResting()
        fun workoutContinue()

        fun workoutRowingStatus(splitOrIntervalNum: Int, data: RowingStatus, opponent: Workout.DataPoint?, personalBest: Workout.DataPoint?)
        fun workoutAdditionalRowingStatus1(splitOrIntervalNum: Int, data: AdditionalRowingStatus1)
        fun workoutAdditionalRowingStatus2(splitOrIntervalNum: Int, data: AdditionalRowingStatus2)

        fun workoutStrokeData(splitOrIntervalNum: Int, data: StrokeData)
        fun workoutAdditionalStrokeData(splitOrIntervalNum: Int, data: AdditionalStrokeData)

        fun workoutSplitIntervalData(splitOrIntervalNum: Int, data: SplitIntervalData)
        fun workoutAdditionalSplitIntervalData(splitOrIntervalNum: Int, data: AdditionalSplitIntervalData)

        fun workoutFinished()
        fun workoutTerminated()
    }
}