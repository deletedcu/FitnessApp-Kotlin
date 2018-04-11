package com.liverowing.liverowing.util

import android.util.Log
import com.liverowing.liverowing.model.parse.Affiliate
import com.liverowing.liverowing.model.parse.User
import com.liverowing.liverowing.model.parse.Workout
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.model.pm.*
import com.parse.ParseUser
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

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
            }

            WorkoutState.INTERVALREST -> {
                if (!mResting) {
                    mResting = true
                    workoutResting(data)
                }
            }

            WorkoutState.INTERVALWORKTIME,
            WorkoutState.INTERVALWORKDISTANCE -> {
                if (!mWorkoutStarted) {
                    mWorkoutStarted = true
                    workoutStarted(data)
                } else {
                    if (mResting) {
                        mResting = false
                        workoutListener?.workoutContinue()
                    }
                }
            }

            WorkoutState.WORKOUTEND -> {
                if (!mWorkoutFinished) {
                    workoutFinished()
                }
            }

            WorkoutState.TERMINATE -> {
                if (!mWorkoutFinished) {
                    workoutTerminated()
                }
            }

            WorkoutState.WAITTOBEGIN -> {
            }
            WorkoutState.COUNTDOWNPAUSE -> {
            }
            WorkoutState.INTERVALRESTENDTOWORKTIME -> {
            }
            WorkoutState.INTERVALRESTENDTOWORKDISTANCE -> {
            }
            WorkoutState.INTERVALWORKTIMETOREST -> {
            }
            WorkoutState.INTERVALWORKDISTANCETOREST -> {
            }
            WorkoutState.WORKOUTLOGGED -> {
            }
            WorkoutState.REARM -> {
            }
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

    private var lastStrokeData: StrokeData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStrokeData(data: StrokeData) {
        lastStrokeData = data
        mStrokes.add(Workout.Stroke((data.elapsedTime * 10).toInt(), (data.distance * 10).toInt(), (lastAdditionalRowingStatus2!!.splitIntAvgPace * 10).toInt(), lastAdditionalRowingStatus1!!.strokeRate, lastAdditionalRowingStatus1!!.heartRate))
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
        splits.add(Workout.Split(
                data.averageDragFactor,
                0.0,
                data.pace.toDouble(),
                0.0,
                data.workHeartRate.toDouble(),
                0,
                lastSplitIntervalData!!.splitTime.toInt(),
                lastSplitIntervalData!!.splitDistance.toInt(),
                0.0,
                lastSplitIntervalData!!.splitDistance,
                data.calories.toDouble(),
                0,
                data.intervalNumber-1
        ))

        workoutListener?.workoutAdditionalSplitIntervalData(data.intervalNumber, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWorkoutSummary(data: WorkoutSummary) {
        Log.d("LiveRowing", data.toString())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalWorkoutSummary(data: AdditionalWorkoutSummary) {
        Log.d("LiveRowing", data.toString())
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

        EventBus.getDefault().unregister(this)
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