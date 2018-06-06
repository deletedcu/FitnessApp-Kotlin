package com.liverowing.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_DISTANCE
import com.liverowing.android.model.parse.WorkoutType.Companion.SEGMENT_VALUE_TYPE_TIMED
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_CUSTOM
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_DISTANCE
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_TIMED
import com.liverowing.android.model.parse.WorkoutType.Companion.fetchWorkout
import kotlinx.android.synthetic.main.race_racing.*
import com.liverowing.android.model.pm.*
import com.liverowing.android.util.metric.Metric

class RaceTestActivity : AppCompatActivity() {
    private lateinit var mWorkoutType: com.liverowing.android.model.parse.WorkoutType

    private lateinit var mOpponent: com.liverowing.android.model.parse.Workout
    private lateinit var mOpponentSplits: List<Workout.Split>

    private lateinit var mPersonalBest: com.liverowing.android.model.parse.Workout
    private lateinit var mPersonalBestSplits: List<Workout.Split>

    private val mSplits = arrayListOf<Workout.Split>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_test)

        race_racing_metric_center.metricId = Metric.SECONDARY_METRIC_HEART_RATE

        mWorkoutType = fetchWorkout("nVUiYpu5ay")
        race_racing_overview_progress.addTimeWithSetSplitSize(240, 5)

        mOpponent = Workout.fetchWorkout("f91xAlF4PW")
        //mOpponent.loadForPlayback()
        mOpponentSplits = mOpponent.data.WorkoutData.splits


        mPersonalBest = Workout.fetchWorkout("f91xAlF4PW")
        //mPersonalBest.loadForPlayback()
        mPersonalBestSplits = mPersonalBest.data.WorkoutData.splits

        race_racing_gauge_left.metricId = Metric.PRIMARY_METRIC_LEFT_PACE_WITH_AVG
        race_racing_gauge_left.setValue(124f, 128f, true)

        race_racing_gauge_right.metricId = Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_AVG
        race_racing_gauge_right.setValue(22f, 24f, true)

        race_racing_stroke_ratio.setStrokeRatio(1f, 2f)

        val me = RowingStatus(
                65.0,
                1000.0,
                WorkoutType.FIXEDTIME_SPLITS,
                IntervalType.TIME,
                WorkoutState.INTERVALWORKTIME,
                RowingState.ACTIVE,
                StrokeState.RECOVERY_STATE,
                240.0,
                DurationType.TIME,
                0.0,
                110
        )

        val pb = Workout.DataPoint(0, 0, 0, 0, true, 1000.0, 2, 0.0, 0, 0, 0, 65.0, 0, 0)
        val opponent = Workout.DataPoint(0, 0, 0, 0, true, 1000.0, 2, 0.0, 0, 0, 0, 65.0, 0, 0)

        val positions = getPositions(2, me, Progress(mPersonalBestSplits, pb), Progress(mOpponentSplits, opponent))
        race_racing_me_progress.hasPersonalBest = true
        race_racing_overview_progress.setProgress(positions.index, positions.overview, true)
        race_racing_me_progress.setProgress(positions.me, positions.pb, true)
        race_racing_opponent_progress.setProgress(positions.opponent)
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
                val mySplitDistance = me.distance - getDistanceInPreviousSplits(mSplits, currentSplit)
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
}
