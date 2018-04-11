package com.liverowing.liverowing.activity.race

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.liverowing.liverowing.LiveRowing.Companion.deviceConnected
import com.liverowing.liverowing.R
import com.liverowing.liverowing.activity.devicescan.DeviceScanActivity
import com.liverowing.liverowing.model.parse.Workout
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_DISTANCE
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_TIMED
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_DISTANCE
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_TIMED
import com.liverowing.liverowing.model.pm.*
import com.liverowing.liverowing.service.messages.WorkoutProgramRequest
import com.liverowing.liverowing.service.messages.WorkoutProgrammed
import com.liverowing.liverowing.service.messages.WorkoutSetup
import com.liverowing.liverowing.util.WorkoutRecorder
import com.liverowing.liverowing.util.metric.Metric
import com.liverowing.liverowing.util.metric.NumericMetricFormatter
import com.liverowing.liverowing.util.metric.TimeMetricFormatter
import com.liverowing.liverowing.view.SplitIntervalOverviewView
import kotlinx.android.synthetic.main.activity_race.*
import kotlinx.android.synthetic.main.race_racing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.liverowing.liverowing.Preferences.Companion.customPrefs
import com.liverowing.liverowing.dpToPx
import com.liverowing.liverowing.model.parse.Segment
import com.liverowing.liverowing.model.parse.User
import com.liverowing.liverowing.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.liverowing.view.GaugeView
import com.liverowing.liverowing.view.TextMetricView
import com.parse.ParseUser


class RaceActivity : AppCompatActivity(), WorkoutRecorder.Callback {
    private lateinit var prefs: SharedPreferences
    private lateinit var mWorkoutType: WorkoutType
    private lateinit var mWorkoutRecorder: WorkoutRecorder
    private var mWorkoutProgrammed = false
    private var mProgrammingWorkout = false
    private var mWorkoutStarted = false
    private var mOpponent: Workout? = null
    private var mPersonalBest: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        prefs = customPrefs(this@RaceActivity, "LiveRowing")

        transitionTo(a_race_group_waiting, true)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

    }

    override fun onResume() {
        super.onResume()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        if (!deviceConnected) {
            transitionTo(a_race_group_waiting, true)
            a_race_overlay_text.text = "Click to connect"
            a_race_overlay_text.setOnClickListener {
                startActivity(Intent(this@RaceActivity, DeviceScanActivity::class.java))
            }
        } else if (!mWorkoutProgrammed && !mProgrammingWorkout) {
            transitionTo(a_race_group_waiting, true)
            a_race_overlay_text.text = "Wait for PM"
            a_race_overlay_text.setOnClickListener(null)

            mProgrammingWorkout = true
            EventBus.getDefault().post(WorkoutProgramRequest(mWorkoutType))
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onWorkoutSetup(setup: WorkoutSetup) {
        mWorkoutType = setup.workoutType
        mWorkoutRecorder = WorkoutRecorder(mWorkoutType, null, setup.opponent, null)

        // Background image
        if (mWorkoutType.image?.url != null) {
            Glide.with(this).load(mWorkoutType.image?.url).into(a_race_background_image)
        }

        // Opponent
        if (setup.opponent is Workout) {
            mOpponent = setup.opponent
            race_racing_opponent_progress.apply {
                visibility = View.VISIBLE
                name = setup.opponent.createdBy?.username
                flagColor = setup.opponent.createdBy?.getFlagColor(this@RaceActivity)
            }
        } else {
            race_racing_opponent_progress.visibility = View.GONE
        }

        // User
        val me = (ParseUser.getCurrentUser() as User)
        race_racing_me_progress.flagColor = me.getFlagColor(this@RaceActivity)
        Glide.with(this)
                .asBitmap()
                .load(me.image?.url)
                .apply(RequestOptions().transform(RoundedCorners(12)))
                .into(object : SimpleTarget<Bitmap>(60.dpToPx() - 12, 60.dpToPx() - 12) {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        race_racing_me_progress.image = resource
                    }
                })

        // Racing personal best?
        if (setup.personalBest is Workout) {
            mPersonalBest = setup.personalBest
            race_racing_me_progress.hasPersonalBest = true
        }

        // Split/Interval overview
        race_racing_overview_progress.reset()
        if (mWorkoutType.segments != null && mWorkoutType.segments!!.isNotEmpty()) {
            mWorkoutType.segments?.forEach {
                when (it.valueType) {
                    SEGMENT_VALUE_TYPE_TIMED -> race_racing_overview_progress.addInterval(SplitIntervalOverviewView.SplitIntervalType.SplitIntervalTime, it.value!!)
                    SEGMENT_VALUE_TYPE_DISTANCE -> race_racing_overview_progress.addInterval(SplitIntervalOverviewView.SplitIntervalType.SplitIntervalDistance, it.value!!)
                }
            }
        } else {
            when (mWorkoutType.valueType) {
                VALUE_TYPE_TIMED -> race_racing_overview_progress.addTimeWithSetSplitSize(mWorkoutType.value, mWorkoutType.calculatedSplitNum)
                VALUE_TYPE_DISTANCE -> race_racing_overview_progress.addDistanceWithSetSplitSize(mWorkoutType.value, mWorkoutType.calculatedSplitNum)
            }
        }

        // Primary metrics
        val primaryMetricLeftClickListener = View.OnClickListener {view ->
            val current = (view as GaugeView).metricId
            view.metricId = if (current >= Metric.PRIMARY_METRIC_LEFT_HIGH) Metric.PRIMARY_METRIC_LEFT_LOW else current+1
            prefs.edit().putInt("primary_metric_${view.tag}", view.metricId).apply()
            updateMetrics(true)
        }
        race_racing_gauge_left.setOnClickListener(primaryMetricLeftClickListener)

        val primaryMetricRightClickListener = View.OnClickListener {view ->
            val current = (view as GaugeView).metricId
            view.metricId = if (current >= Metric.PRIMARY_METRIC_RIGHT_HIGH) Metric.PRIMARY_METRIC_RIGHT_LOW else current+1
            prefs.edit().putInt("primary_metric_${view.tag}", view.metricId).apply()
            updateMetrics(true)
        }
        race_racing_gauge_right.setOnClickListener(primaryMetricRightClickListener)

        // Secondary metrics
        val secondaryMetricClickListener = View.OnClickListener {view ->
            val current = (view as TextMetricView).metricId
            view.metricId = if (current >= Metric.SECONDARY_METRIC_HIGH) Metric.SECONDARY_METRIC_LOW else current+1
            prefs.edit().putInt("secondary_metric_${view.tag}", view.metricId).apply()
            updateMetrics(true)
        }
        race_racing_metric_left.setOnClickListener(secondaryMetricClickListener)
        race_racing_metric_center.setOnClickListener(secondaryMetricClickListener)
        race_racing_metric_right.setOnClickListener(secondaryMetricClickListener)

        updateMetrics(false, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWorkoutProgrammed(message: WorkoutProgrammed) {
        mProgrammingWorkout = false
        mWorkoutProgrammed = true
        mWorkoutRecorder.workoutListener = this

        transitionTo(a_race_group_waiting, true)
        a_race_overlay_text.text = "Row to start"
        a_race_overlay_text.setOnClickListener(null)
    }

    override fun workoutStarted() {
        mWorkoutStarted = true
        transitionTo(a_race_group_racing, false)
    }

    override fun workoutResting() {
        transitionTo(a_race_group_resting, true)
    }

    override fun workoutContinue() {
        transitionTo(a_race_group_racing, true)
    }

    override fun workoutRowingStatus(splitOrIntervalNum: Int, data: RowingStatus, opponent: Workout.DataPoint?, personalBest: Workout.DataPoint?) {
        val positions = getPositions(splitOrIntervalNum, data, Progress(mPersonalBest?.data?.WorkoutData?.splits, personalBest), Progress(mOpponent?.data?.WorkoutData?.splits, opponent))
        race_racing_overview_progress.setProgress(positions.index, positions.overview, true)
        race_racing_me_progress.setProgress(positions.me, positions.pb, true)
        race_racing_opponent_progress.setProgress(positions.opponent, true)
    }

    data class Progress(val splits: List<Workout.Split>?, val dataPoint: Workout.DataPoint?)
    data class Positions(val index: Int, val overview: Float, val me: Float, val pb: Float = 0f, val opponent: Float = 0f)
    private fun getPositions(index: Int, me: RowingStatus, pb: Progress, opponent: Progress) : Positions {
        return when (mWorkoutType.valueType) {
            VALUE_TYPE_DISTANCE -> {
                val splitDistance = me.currentSplitSize(mWorkoutType.calculatedSplitLength)
                val currentSplit = (me.distance / splitDistance).toInt()
                val distance = me.distance.rem(splitDistance)
                val personalBestPosition = if (pb.dataPoint is Workout.DataPoint) getPositionByDistance(currentSplit, splitDistance, pb.dataPoint.meters) else 0f
                val opponentPosition = if (opponent.dataPoint is Workout.DataPoint) getPositionByDistance(currentSplit, splitDistance, opponent.dataPoint.meters) else 0f

                Positions(index, distance.toFloat(), (distance / splitDistance).toFloat(), personalBestPosition, opponentPosition)
            }

            VALUE_TYPE_TIMED -> {
                val splitTime = me.currentSplitSize((mWorkoutType.calculatedSplitLength))
                val currentSplit = (me.elapsedTime / splitTime).toInt()
                val time = me.elapsedTime.rem(splitTime)
                val mySplitDistance = me.distance - getDistanceInPreviousSplits(mWorkoutRecorder.splits, currentSplit)
                val pbSplitDistance = if (pb.dataPoint is Workout.DataPoint) pb.dataPoint.meters - getDistanceInPreviousSplits(pb.splits!!, currentSplit) else 0.0
                val opponentSplitDistance = if (opponent.dataPoint is Workout.DataPoint) opponent.dataPoint.meters - getDistanceInPreviousSplits(opponent.splits!!, currentSplit) else 0.0
                val longestDistance = maxOf(mySplitDistance, pbSplitDistance, opponentSplitDistance)
                val ratio = time / longestDistance

                Log.d("LiveRowing", "split time = $splitTime, time in split = $time")
                Log.d("LiveRowing", "ratio = $ratio, me = $mySplitDistance, pb = $pbSplitDistance, opponent = $opponentSplitDistance, longest = $longestDistance")

                Positions(index, time.toFloat(), ((ratio * mySplitDistance) / splitTime).toFloat(), ((ratio * pbSplitDistance) / splitTime).toFloat(), ((ratio * opponentSplitDistance) / splitTime).toFloat())
            }

            VALUE_TYPE_CUSTOM -> {
                if (index < mWorkoutType.segments!!.size) {
                    val segment = mWorkoutType.segments!![index]
                    when (segment.valueType) {
                        SEGMENT_VALUE_TYPE_DISTANCE -> Positions(index, 0f, 0f)
                        SEGMENT_VALUE_TYPE_TIMED -> Positions(index, 0f, 0f)
                        else -> Positions(index, 0f, 0f)
                    }
                } else Positions(index, 0f, 0f)
            }
            else -> Positions(index, 0f, 0f)
        }
    }

    private fun getDistanceInPreviousSplits(splits: List<Workout.Split>, index: Int): Double {
        return splits.sumByDouble { if (it.splitNumber < index) it.splitDistance.toDouble() else 0.0 }
    }

    private fun getPositionByDistance(index: Int, splitDistance: Double, distance: Double): Float {
        val split = Math.floor(distance / splitDistance).toInt()
        return when {
            index > split -> 0f
            index < split -> 1f
            else -> (distance.rem(splitDistance) / splitDistance).toFloat()
        }
    }

    override fun workoutAdditionalRowingStatus1(splitOrIntervalNum: Int, data: AdditionalRowingStatus1) {
        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_RATE] as Metric.Secondary).value = data.strokeRate.toFloat()
            if (data.heartRate != 255) { (metrics[Metric.SECONDARY_METRIC_HEART_RATE] as Metric.Secondary).value = data.heartRate.toFloat() }
            (metrics[Metric.SECONDARY_METRIC_SPEED] as Metric.Secondary).value = data.speed.toFloat()
            (metrics[Metric.SECONDARY_METRIC_PACE] as Metric.Secondary).value = data.currentPace


            primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.apply {
                num += 1
                total += data.currentPace
                high = if (high < data.currentPace) data.currentPace else high
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
        }
    }

    override fun workoutAdditionalRowingStatus2(splitOrIntervalNum: Int, data: AdditionalRowingStatus2) {
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_PACE] as Metric.Secondary).value = data.splitIntAvgPace
        (metrics[Metric.SECONDARY_METRIC_CALORIE_COUNT] as Metric.Secondary).value = data.caloriesBurned.toFloat()
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_POWER] as Metric.Secondary).value = data.splitIntAvgPower.toFloat()
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR] as Metric.Secondary).value = data.splitIntAvgCals.toFloat()

        updateMetrics(true)
    }

    override fun workoutStrokeData(splitOrIntervalNum: Int, data: StrokeData) {
        if (mWorkoutStarted) {
            (metrics[Metric.SECONDARY_METRIC_STROKE_LENGTH] as Metric.Secondary).value = data.driveLength
            (metrics[Metric.SECONDARY_METRIC_STROKE_DISTANCE] as Metric.Secondary).value = data.strokeDistance
            (metrics[Metric.SECONDARY_METRIC_STROKE_COUNT] as Metric.Secondary).value = data.strokeCount.toFloat()
            (metrics[Metric.SECONDARY_METRIC_STROKE_RECOVERY_TIME] as Metric.Secondary).value = data.recoveryTime
            (metrics[Metric.SECONDARY_METRIC_DRIVE_TIME] as Metric.Secondary).value = data.driveTime
        }
    }

    override fun workoutAdditionalStrokeData(splitOrIntervalNum: Int, data: AdditionalStrokeData) {
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
        }

        updateMetrics(true)
    }

    override fun workoutSplitIntervalData(splitOrIntervalNum: Int, data: SplitIntervalData) {
    }

    override fun workoutAdditionalSplitIntervalData(splitOrIntervalNum: Int, data: AdditionalSplitIntervalData) {
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_POWER] as Metric.Secondary).value = data.power.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_RATE] as Metric.Secondary).value = data.spm.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_PACE] as Metric.Secondary).value = data.pace
        (metrics[Metric.SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER] as Metric.Secondary).value = data.intervalNumber + 1f
    }

    override fun workoutFinished() {
        mWorkoutStarted = false
        transitionTo(a_race_group_waiting, false)

        a_race_overlay_text.text = "Finished please wait"
        a_race_overlay_text.setOnClickListener(null)
    }

    override fun workoutTerminated() {
        mWorkoutStarted = false
        transitionTo(a_race_group_waiting, false)

        a_race_overlay_text.text = "WTF just happened"
        a_race_overlay_text.setOnClickListener(null)
    }


    private fun transitionTo(view: View, instant: Boolean) {
        val waiting = a_race_group_waiting
        val racing  = a_race_group_racing
        val resting = a_race_group_resting

        val from = when {
            waiting.visibility == View.VISIBLE -> waiting
            racing.visibility == View.VISIBLE -> racing
            else -> resting
        }

        if (instant) {
            from.visibility = View.GONE
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            val fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
            val fadeOut = ObjectAnimator.ofFloat(from, View.ALPHA, 1f, 0f).apply {
                addListener(object: Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) { from.visibility = View.VISIBLE }
                    override fun onAnimationEnd(p0: Animator?) { from.visibility = View.GONE }
                })
            }

            AnimatorSet().apply {
                playTogether(fadeIn, fadeOut)
            }.start()
        }
    }

    data class AggregateMetric(var num: Int, var total: Float, var high: Float)
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


    private fun updateMetrics(animated: Boolean, loadFromPreferences: Boolean = false) {
        if (loadFromPreferences) {
            race_racing_metric_left.metricId = prefs.getInt("secondary_metric_left", 0)
            race_racing_metric_center.metricId = prefs.getInt("secondary_metric_center", 1)
            race_racing_metric_right.metricId = prefs.getInt("secondary_metric_right", 2)

            race_racing_gauge_left.metricId = prefs.getInt("primary_metric_left", 100)
            race_racing_gauge_right.metricId = prefs.getInt("primary_metric_right", 200)
        }


        runOnUiThread {
            race_racing_metric_left.apply {
                metric = (metrics[metricId] as Metric.Secondary).title
                value = (metrics[metricId] as Metric.Secondary).formattedValue
            }

            race_racing_metric_center.apply {
                metric = (metrics[metricId] as Metric.Secondary).title
                value = (metrics[metricId] as Metric.Secondary).formattedValue
            }

            race_racing_metric_right.apply {
                metric = (metrics[metricId] as Metric.Secondary).title
                value = (metrics[metricId] as Metric.Secondary).formattedValue
            }

            race_racing_gauge_left.apply {
                title = (metrics[metricId] as Metric.Primary).title
                subtitle = (metrics[metricId] as Metric.Primary).subtitle
                scaleStartValue = (metrics[metricId] as Metric.Primary).min
                scaleEndValue = (metrics[metricId] as Metric.Primary).max
                formatter = (metrics[metricId] as Metric.Primary).formatter
                setValue((metrics[metricId] as Metric.Primary).value, (metrics[metricId] as Metric.Primary).subvalue, animated)
            }

            race_racing_gauge_right.apply {
                title = (metrics[metricId] as Metric.Primary).title
                subtitle = (metrics[metricId] as Metric.Primary).subtitle
                scaleStartValue = (metrics[metricId] as Metric.Primary).min
                scaleEndValue = (metrics[metricId] as Metric.Primary).max
                formatter = (metrics[metricId] as Metric.Primary).formatter
                setValue((metrics[metricId] as Metric.Primary).value, (metrics[metricId] as Metric.Primary).subvalue, animated)
            }
        }
    }
}