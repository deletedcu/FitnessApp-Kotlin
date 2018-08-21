package com.liverowing.android.race

import android.util.Base64
import com.liverowing.android.Preferences
import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.parse.Affiliate
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.android.model.pm.*
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.util.metric.NumericMetricFormatter
import com.liverowing.android.util.metric.TimeMetricFormatter
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.serialization.json.JSON
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class RacePresenter : EventBusPresenter<RaceView>() {
    private var mWorkoutType: WorkoutType? = null

    private var mWorkout: Workout = Workout()
    private var mWorkoutStarted = false
    private var mResting = false
    private var mWorkoutFinished = false
    private var mCurrentIntervalOrSplit = 0

    private var mOpponentLastIndex = 0
    private var mOpponentWorkout: Workout? = null
    private var mPersonalBestLastIndex = 0
    private var mPersonalBestWorkout: Workout? = null

    private var mDataPoints = arrayListOf<Workout.DataPoint>()
    private var mStrokes = arrayListOf<Workout.Stroke>()
    private var mSplits = arrayListOf<Workout.Split>()
    
    data class AggregateMetric(var num: Int, var total: Float, var high: Float)

    private var mPrimaryMetricLeft = Preferences.primaryMetricLeft
    private var mPrimaryMetricRight = Preferences.primaryMetricRight
    private var mSecondaryMetricLeft = Preferences.secondaryMetricLeft
    private var mSecondaryMetricCenter = Preferences.secondaryMetricCenter
    private var mSecondaryMetricRight = Preferences.secondaryMetricRight

    private val primaryMetricAggregates = mapOf(
            Metric.SECONDARY_METRIC_POWER to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_CALORIES_PER_HOUR to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_RATE to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_PACE to AggregateMetric(0, 0f, 0f)
    )
    private val metrics = mapOf(
            Metric.PRIMARY_METRIC_LEFT_PACE_WITH_AVG to Metric.Primary("Pace", "/Avg", TimeMetricFormatter(false), 180f, 60f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_PACE_WITH_PEAK to Metric.Primary("Pace", "/Peak", TimeMetricFormatter(false), 180f, 60f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_POWER_WITH_AVG to Metric.Primary("Power", "/Avg", NumericMetricFormatter("%.0f"), 20f, 260f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_POWER_WITH_PEAK to Metric.Primary("Power", "/Peak", NumericMetricFormatter("%.0f"), 20f, 260f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_AVG to Metric.Primary("Rate", "/Avg", NumericMetricFormatter("%.0f"), 16f, 40f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_PEAK to Metric.Primary("Rate", "/Peak", NumericMetricFormatter("%.0f"), 16f, 40f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_AVG to Metric.Primary("Cals/hr", "/Avg", NumericMetricFormatter("%.0f"), 400f, 1200f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_PEAK to Metric.Primary("Cals/hr", "/Peak", NumericMetricFormatter("%.0f"), 400f, 1200f, 0f, 0f),

            Metric.SECONDARY_METRIC_POWER to Metric.Secondary("Power", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_PACE to Metric.Secondary("Pace", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_RATE to Metric.Secondary("Rate", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_HEART_RATE to Metric.Secondary("Heart rate", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_SPEED to Metric.Secondary("Speed", NumericMetricFormatter("%.2f"), 0f),
            Metric.SECONDARY_METRIC_PREVIOUS_RATE to Metric.Secondary("Previous rate", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_PREVIOUS_PACE to Metric.Secondary("Previous pace", TimeMetricFormatter(true), null, "N/A"),
            Metric.SECONDARY_METRIC_PREVIOUS_POWER to Metric.Secondary("Previous avg. power", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_LENGTH to Metric.Secondary("Stroke length", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_DISTANCE to Metric.Secondary("Stroke distance", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_RATIO to Metric.Secondary("Stroke ratio", NumericMetricFormatter("%.0f%%"), 0f),
            Metric.SECONDARY_METRIC_STROKE_COUNT to Metric.Secondary("Stroke count", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_TIME to Metric.Secondary("Time", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_DISTANCE to Metric.Secondary("Distance", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_TOTAL_TIME to Metric.Secondary("Total time", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_CALORIE_COUNT to Metric.Secondary("Calorie count", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_CALORIES_PER_HOUR to Metric.Secondary("Calories/hr", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_SPI to Metric.Secondary("Stroke Power Index", NumericMetricFormatter("%.1f"), null, "N/A"),
            Metric.SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER to Metric.Secondary("Split/Interval", NumericMetricFormatter("%.0f"), 1f),
            Metric.SECONDARY_METRIC_AVERAGE_PACE to Metric.Secondary("Avg. pace", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_POWER to Metric.Secondary("Avg. power", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_RATE to Metric.Secondary("Avg. rate", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR to Metric.Secondary("Avg. calories/hr", NumericMetricFormatter("%.0f"), 0f),

            Metric.SECONDARY_METRIC_DRIVE_TIME to Metric.Secondary("Drive time", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_RECOVERY_TIME to Metric.Secondary("Stroke recovery time", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_TARGET_RATE to Metric.Secondary("Target rate", NumericMetricFormatter("%.0f"), null, "Not set"),
            Metric.SECONDARY_METRIC_PROJECTED_WORK_TIME to Metric.Secondary("Projected time", TimeMetricFormatter(true), null, "N/A"),
            Metric.SECONDARY_METRIC_PROJECTED_WORK_DISTANCE to Metric.Secondary("Projected distance", NumericMetricFormatter("%.0f"), null, "N/A")
    )

    init {
        mWorkout.createdBy = ParseUser.getCurrentUser() as User
        mWorkout.isDone = false
        mWorkout.withPersonalBest = false
        mWorkout.isChallenge = false
    }

    fun loadWorkoutTypeById(id: String) {
        ifViewAttached {
            it.setLoadingMessage("Loading workout..")
            it.showLoading(false)
        }
        val query = ParseQuery.getQuery(WorkoutType::class.java)
        query.include("createdBy")
        query.include("segments")
        query.getInBackground(id) { workoutType: WorkoutType?, e: ParseException? ->
            if (e !== null) {
                ifViewAttached { it.showError(e, false) }
            } else {
                eventBus.postSticky(workoutType)
            }
        }
    }

    fun switchPrimaryMetricLeft() {
        mPrimaryMetricLeft++
        if (mPrimaryMetricLeft >= Metric.PRIMARY_METRIC_LEFT_HIGH) {
            mPrimaryMetricLeft = Metric.PRIMARY_METRIC_LEFT_LOW
        }
        Preferences.primaryMetricLeft = mPrimaryMetricLeft

        ifViewAttached {
            it.primaryMetricLeftUpdated(metrics[mPrimaryMetricLeft] as Metric.Primary, true)
        }
    }

    fun switchPrimaryMetricRight() {
        mPrimaryMetricRight++
        if (mPrimaryMetricRight >= Metric.PRIMARY_METRIC_RIGHT_HIGH) {
            mPrimaryMetricRight = Metric.PRIMARY_METRIC_RIGHT_LOW
        }
        Preferences.primaryMetricRight = mPrimaryMetricRight

        ifViewAttached {
            it.primaryMetricRightUpdated(metrics[mPrimaryMetricRight] as Metric.Primary, true)
        }
    }

    fun switchSecondaryMetricLeft() {
        mSecondaryMetricLeft++
        if (mSecondaryMetricLeft >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricLeft = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricLeft = mSecondaryMetricLeft

        ifViewAttached {
            it.secondaryMetricLeftUpdated(metrics[mSecondaryMetricLeft] as Metric.Secondary)
        }
    }
    
    fun switchSecondaryMetricCenter() {
        mSecondaryMetricCenter++
        if (mSecondaryMetricCenter >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricCenter = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricCenter = mSecondaryMetricCenter

        ifViewAttached {
            it.secondaryMetricCenterUpdated(metrics[mSecondaryMetricCenter] as Metric.Secondary)
        }
    }
    
    fun switchSecondaryMetricRight() {
        mSecondaryMetricRight++
        if (mSecondaryMetricRight >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricRight = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricRight = mSecondaryMetricRight

        ifViewAttached {
            it.secondaryMetricRightUpdated(metrics[mSecondaryMetricRight] as Metric.Secondary)
        }
    }

    fun setAffiliate(affiliate: Affiliate) {
        mWorkout.affiliate = affiliate
    }

    fun setPersonalBestWorkout(workout: Workout) {
        mPersonalBestWorkout = workout
        mWorkout.pbWorkout = workout
        mWorkout.withPersonalBest = true
    }

    fun setOpponentWorkout(workout: Workout) {
        mOpponentWorkout = workout
        mWorkout.isChallenge = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutTypeMainThread(workoutType: WorkoutType) {
        Timber.d("** onWorkoutTypeMainThread")
        mWorkoutType = workoutType
        mWorkout.workoutType = workoutType

        ifViewAttached {
            updateViewMetrics()
            
            it.setData(workoutType)
            it.showContent()
        }
    }

    private fun updateViewMetrics() {
        ifViewAttached {
            it.primaryMetricLeftUpdated(metrics[mPrimaryMetricLeft] as Metric.Primary, false)
            it.primaryMetricRightUpdated(metrics[mPrimaryMetricRight] as Metric.Primary, false)

            it.secondaryMetricLeftUpdated(metrics[mSecondaryMetricLeft] as Metric.Secondary)
            it.secondaryMetricCenterUpdated(metrics[mSecondaryMetricCenter] as Metric.Secondary)
            it.secondaryMetricRightUpdated(metrics[mSecondaryMetricRight] as Metric.Secondary)
        }
    }

    private fun workoutStarting(data: RowingStatus) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, -(data.elapsedTime * 1000).toInt())
        mWorkout.startTime = calendar.time

        ifViewAttached { it.workoutStarting() }
    }

    private fun workoutResting() {
        ifViewAttached { it.workoutResting() }
    }

    private fun workoutContinuing() {
        ifViewAttached { it.workoutContinuing() }
    }

    private fun workoutFinished() {
        var json = "["
        mStrokes.forEach {
            json += "{\"d\": ${it.d}, \"p\": ${it.p}, \"hr\": ${it.hr}, \"spm\": ${it.spm}, \"t\": ${it.t}},"
        }
        json = json.substring(0, json.length - 1) + "]"

        mWorkoutFinished = true
        mWorkout.apply {
            finishTime = Calendar.getInstance().time
            isDone = true
        }

        ifViewAttached { it.workoutFinishing() }
    }

    private fun workoutTerminated() {
        mWorkoutFinished = true
        mWorkout.apply {
            finishTime = Calendar.getInstance().time
            isDone = false
        }

        /*
        EventBus.getDefault().unregister(this)
        workoutListener?.workoutTerminated()
        */
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
    fun onRowingStatusMainThread(data: RowingStatus) {
        Timber.d("** onRowingStatusMainThread")
        lastRowingStatus = data

        when (data.workoutState) {
            WorkoutState.WORKOUTROW -> {
                if (!mWorkoutStarted) {
                    mWorkoutStarted = true
                    workoutStarting(data)
                }
                logDataPoint()
            }

            WorkoutState.INTERVALREST -> {
                if (!mResting) {
                    mResting = true
                    workoutResting()
                }
                logDataPoint()
            }

            WorkoutState.INTERVALWORKTIME,
            WorkoutState.INTERVALWORKDISTANCE -> {
                if (!mWorkoutStarted) {
                    mWorkoutStarted = true
                    workoutStarting(data)
                } else {
                    logDataPoint()
                    if (mResting) {
                        mResting = false
                        workoutContinuing()
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

            else -> {}
        }

        if (mWorkoutStarted) {
            var opponentPlaybackData: Workout.DataPoint? = null
            var personalBestPlaybackData: Workout.DataPoint? = null
            if (mPersonalBestWorkout != null && mPersonalBestWorkout!!.playbackData.size > 0) {
                mPersonalBestLastIndex = getPlaybackPosition(mPersonalBestWorkout!!, mPersonalBestLastIndex, data)
                personalBestPlaybackData = normalizePlaybackDistance(data.elapsedTime, mPersonalBestWorkout!!.playbackData, mPersonalBestLastIndex)
            }

            if (mOpponentWorkout is Workout && mOpponentWorkout!!.playbackData.size > 0) {
                mOpponentLastIndex = getPlaybackPosition(mOpponentWorkout!!, mOpponentLastIndex, data)
                opponentPlaybackData = normalizePlaybackDistance(data.elapsedTime, mOpponentWorkout!!.playbackData, mOpponentLastIndex)
            }

            //workoutListener?.workoutRowingStatus(mCurrentIntervalOrSplit, data, opponentPlaybackData, personalBestPlaybackData)
        }
    }

    private var lastAdditionalRowingStatus1: ExtraRowingStatus1? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalRowingStatus1(data: ExtraRowingStatus1) {
        Timber.d("** onAdditionalRowingStatus1")
        lastAdditionalRowingStatus1 = data

        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_RATE] as Metric.Secondary).value = data.strokeRate.toFloat()
            if (data.heartRate != 255) {
                (metrics[Metric.SECONDARY_METRIC_HEART_RATE] as Metric.Secondary).value = data.heartRate.toFloat()
            }
            (metrics[Metric.SECONDARY_METRIC_SPEED] as Metric.Secondary).value = data.speed.toFloat()
            (metrics[Metric.SECONDARY_METRIC_PACE] as Metric.Secondary).value = data.currentPace


            primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.apply {
                num += 1
                total += data.currentPace
                high = if (high > data.currentPace) data.currentPace else high
            }
            (metrics[Metric.PRIMARY_METRIC_LEFT_PACE_WITH_AVG] as Metric.Primary).apply {
                value = data.currentPace
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.num
            }
            (metrics[Metric.PRIMARY_METRIC_LEFT_PACE_WITH_PEAK] as Metric.Primary).apply {
                value = data.currentPace
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.high
            }

            primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.apply {
                num += 1
                total += data.strokeRate
                high = if (high < data.strokeRate) data.strokeRate.toFloat() else high
            }
            (metrics[Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_AVG] as Metric.Primary).apply {
                value = data.strokeRate.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.num
            }

            (metrics[Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_PEAK] as Metric.Primary).apply {
                value = data.strokeRate.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.high
            }

            updateViewMetrics()
        }
    }

    private var lastAdditionalRowingStatus2: ExtraRowingStatus2? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalRowingStatus2(data: ExtraRowingStatus2) {
        Timber.d("** onAdditionalRowingStatus2")
        lastAdditionalRowingStatus2 = data

        if (mCurrentIntervalOrSplit != data.intervalCount) {
            mCurrentIntervalOrSplit = data.intervalCount
        }

        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_AVERAGE_PACE] as Metric.Secondary).value = data.splitIntAvgPace
            (metrics[Metric.SECONDARY_METRIC_CALORIE_COUNT] as Metric.Secondary).value = data.caloriesBurned.toFloat()
            (metrics[Metric.SECONDARY_METRIC_AVERAGE_POWER] as Metric.Secondary).value = data.splitIntAvgPower.toFloat()
            (metrics[Metric.SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR] as Metric.Secondary).value = data.splitIntAvgCals.toFloat()

            updateViewMetrics()
        }
    }

    private var lastStrokeCountInWorkPeriod = 0
    private lateinit var lastStrokeData: StrokeData
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStrokeData(data: StrokeData) {
        Timber.d("** onStrokeData")
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

        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_STROKE_LENGTH] as Metric.Secondary).value = data.driveLength
            (metrics[Metric.SECONDARY_METRIC_STROKE_DISTANCE] as Metric.Secondary).value = data.strokeDistance
            (metrics[Metric.SECONDARY_METRIC_STROKE_COUNT] as Metric.Secondary).value = data.strokeCount.toFloat()
            (metrics[Metric.SECONDARY_METRIC_STROKE_RECOVERY_TIME] as Metric.Secondary).value = data.recoveryTime
            (metrics[Metric.SECONDARY_METRIC_DRIVE_TIME] as Metric.Secondary).value = data.driveTime
            (metrics[Metric.SECONDARY_METRIC_STROKE_RATIO] as Metric.Secondary).value = (data.recoveryTime / (data.driveTime + data.recoveryTime)) * 100

            updateViewMetrics()
            //race_racing_stroke_ratio.setStrokeRatio(data.driveTime, data.recoveryTime)
        }
    }

    private var lastAdditionalStrokeData: ExtraStrokeData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalStrokeData(data: ExtraStrokeData) {
        Timber.d("** onAdditionalStrokeData")
        lastAdditionalStrokeData = data

        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_PROJECTED_WORK_DISTANCE] as Metric.Secondary).value = data.projWorkDist.toFloat()
            (metrics[Metric.SECONDARY_METRIC_PROJECTED_WORK_TIME] as Metric.Secondary).value = data.projWorkTime.toFloat()
            (metrics[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR] as Metric.Secondary).value = data.calories.toFloat()
            (metrics[Metric.SECONDARY_METRIC_POWER] as Metric.Secondary).value = data.power.toFloat()
            val spm = (metrics[Metric.SECONDARY_METRIC_RATE] as Metric.Secondary).value
            if (spm != null && spm > 0) {
                (metrics[Metric.SECONDARY_METRIC_SPI] as Metric.Secondary).value = data.power.toFloat() / spm
            }

            primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.apply {
                num += 1
                total += data.power
                high = if (high < data.power) data.power.toFloat() else high
            }
            (metrics[Metric.PRIMARY_METRIC_LEFT_POWER_WITH_AVG] as Metric.Primary).apply {
                value = data.power.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.num
            }
            (metrics[Metric.PRIMARY_METRIC_LEFT_POWER_WITH_PEAK] as Metric.Primary).apply {
                value = data.power.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.high
            }

            primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.apply {
                num += 1
                total += data.calories
                high = if (high < data.calories) data.calories.toFloat() else high
            }
            (metrics[Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_AVG] as Metric.Primary).apply {
                value = data.calories.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.num
            }
            (metrics[Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_PEAK] as Metric.Primary).apply {
                value = data.calories.toFloat()
                subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.high
            }

            updateViewMetrics()
        }
    }

    private var lastSplitIntervalData: SplitIntervalData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSplitIntervalData(data: SplitIntervalData) {
        Timber.d("** onSplitIntervalData")
        lastSplitIntervalData = data
    }

    private var lastAdditionalSplitIntervalData: ExtraSplitIntervalData? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalSplitIntervalData(data: ExtraSplitIntervalData) {
        Timber.d("** onAdditionalSplitIntervalData")
        lastAdditionalSplitIntervalData = data

        var splitStrokeCount = 0
        if (mWorkoutType!!.valueType == VALUE_TYPE_CUSTOM) {
            splitStrokeCount =  lastStrokeCountInWorkPeriod
        } else {
            val previousStrokeCount = mSplits.sumBy { it.splitStrokeCount }
            splitStrokeCount =  lastStrokeCountInWorkPeriod - previousStrokeCount
        }

        val splitNumber = if (mWorkoutType!!.valueType == VALUE_TYPE_CUSTOM) data.intervalNumber else data.intervalNumber-1
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

        mSplits.add(Workout.Split(
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

        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_POWER] as Metric.Secondary).value = data.power.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_RATE] as Metric.Secondary).value = data.spm.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_PACE] as Metric.Secondary).value = data.pace
        (metrics[Metric.SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER] as Metric.Secondary).value = data.intervalNumber + 1f

        updateViewMetrics()
    }

    private lateinit var workoutSummary: RowingSummary
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRowingSummary(data: RowingSummary) {
        Timber.d("** onRowingSummary")
        workoutSummary = data
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onExtraRowingSummary(data: ExtraRowingSummary) {
        Timber.d("** onExtraRowingSummary")
        // TODO: Unregister EventBus here?

        val workTimeDataPoints = mDataPoints.filter { it.workoutState != WorkoutState.INTERVALREST }

        val maxSPM = workTimeDataPoints.maxBy { it.strokesPerMinute }!!.strokesPerMinute
        val maxWatt = workTimeDataPoints.maxBy { it.watts }!!.watts
        val fastestPace = workTimeDataPoints.minBy { it.splitTime }!!.splitTime
        val mSplitsDistance = mSplits.sumByDouble { it.splitDistance }
        val mSplitsStrokeCount = mSplits.sumBy { it.splitStrokeCount }
        val splitAvgDPS = (mSplitsDistance / mSplitsStrokeCount)
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
                splitSize = mWorkoutType!!.calculatedSplitLength,
                splits = mSplits
        )

        val dataPointWrapper = Workout.DataPointWrapper(mDataPoints)
        val dataPoints = Base64.encode(JSON.stringify(dataPointWrapper).toByteArray(), Base64.DEFAULT)
        val file = ParseFile(dataPoints)
        //file.save()

        val avgSplitTime =
                mSplits
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
        //mWorkout.save()
    }
}