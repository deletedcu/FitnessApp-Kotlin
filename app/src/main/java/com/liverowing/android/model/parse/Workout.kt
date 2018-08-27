package com.liverowing.android.model.parse

import android.util.Base64
import com.liverowing.android.extensions.secondsToTimespan
import com.liverowing.android.model.pm.SplitType
import com.liverowing.android.model.pm.WorkoutState
import com.parse.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("Workout")
class Workout : ParseObject() {
    var caloriesBurned by ParseDelegate<Int?>()
    var finishTime by ParseDelegate<Date?>()
    var workoutType by ParseDelegate<WorkoutType?>()
    var averageSPM by ParseDelegate<Number?>()
    var startTime by ParseDelegate<Date?>()
    var averageWatts by ParseDelegate<Int?>()
    var duration by ParseDelegate<Double?>()
    var averageSplitTime by ParseDelegate<Number?>()
    var isDone by ParseDelegate<Boolean?>()
    var averageHeartRate by ParseDelegate<Int?>()
    var meters by ParseDelegate<Int?>()
    var data: Data
        get() = Data.fromMap(getMap<Any>("data"))
        set(value) = put("data", JSONObject(JSON.stringify(value)))
    var createdBy by ParseDelegate<User?>()
    var isChallenge by ParseDelegate<Boolean?>()
    var dataPoints by ParseDelegate<ParseFile?>()
    var totalStrokeCount by ParseDelegate<Int?>()
    var totalTime by ParseDelegate<Number?>()
    var affiliate by ParseDelegate<Affiliate?>()
    var dragFactor by ParseDelegate<Int?>()
    var reGrown by ParseDelegate<Int?>()
    var withPersonalBest by ParseDelegate<Boolean?>()
    var pbWorkout by ParseDelegate<Workout?>()

    var playbackData = arrayListOf<DataPoint>()

    companion object {
        fun forUser(user: ParseUser, createdAt: Date? = null, isDESC: Boolean = true, page: Int = 0, limit: Int = 50): ParseQuery<Workout> {
            val userWorkouts = ParseQuery.getQuery(Workout::class.java)
            userWorkouts.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
            userWorkouts.whereEqualTo("createdBy", user)

            if (createdAt != null) {
                userWorkouts.whereGreaterThan("createdAt", createdAt)
            }

            userWorkouts.include("workoutType.createdBy")

            if (isDESC) {
                userWorkouts.addDescendingOrder("createdAt")
            } else {
                userWorkouts.addAscendingOrder("createdAt")
            }

            if (page > 0) {
                userWorkouts.skip = limit * page
            }
            userWorkouts.limit = limit

            return userWorkouts
        }
    }

    fun loadForPlayback() {
        playbackData.clear()
        if (dataPoints != null && dataPoints?.file != null) {
            try {
                val json = JSONObject(String(Base64.decode(dataPoints?.file?.readText(), Base64.DEFAULT)))
                if (json.has("objects")) {
                    val objects = json.getJSONArray("objects")
                    (0..(objects.length() - 1))
                            .map { objects.getJSONObject(it) }
                            .forEach {
                                playbackData.add(
                                        DataPoint(
                                                it.getInt("caloriesBurned"),
                                                it.getInt("dragFactor"),
                                                it.getInt("heartRate"),
                                                it.getInt("interval"),
                                                it.getBoolean("isRow"),
                                                it.getDouble("meters"),
                                                it.getInt("split"),
                                                it.getDouble("splitTime"),
                                                it.getInt("strokeLength"),
                                                it.getInt("strokeTime"),
                                                it.getInt("strokesPerMinute"),
                                                it.getDouble("time"),
                                                it.getLong("timestamp"),
                                                it.getInt("watts")
                                        )
                                )
                            }
                }
            } catch (e: JSONException) {
            }
        }

    }


    @Serializable
    data class Data(
            val WorkoutData: WorkoutData,
            val StrokeData: List<Stroke>,
            val workoutMachineType: String
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): Data {
                return Data(
                        WorkoutData.fromMap(map["WorkoutData"] as Map<String, Any>),
                        (map["StrokeData"] as List<Map<String, Any>>).map { Stroke.fromMap(it) },
                        map["workoutMachineType"] as String
                )
            }
        }
    }


    @Serializable
    class WorkoutData
    (
            @Serializable(with = StringSerializer::class) val SplitsWatts: Double,
            @Serializable(with = StringSerializer::class) val SplitsCals: Double,
            @Serializable(with = StringSerializer::class) val SplitsAvgDrag: Double,
            @Serializable(with = StringSerializer::class) val SplitsAvgDriveLength: Double,
            @Serializable(with = StringSerializer::class) val SplitsAvgDPS: Double,
            val workDistance: Int,
            @Serializable(with = StringSerializer::class) val fastestPace: Double,
            @Serializable(with = StringSerializer::class) val splitsAvgPace: Double,
            val splitType: Int,
            val maxWatt: Int,
            val heartRate: Int,
            @Serializable(with = StringSerializer::class) val maxHeartRate: Double,
            @Serializable(with = StringSerializer::class) val heartRateNormalZoneTime: Double,
            @Serializable(with = StringSerializer::class) val heartRateZone1Time: Double,
            @Serializable(with = StringSerializer::class) val heartRateZone2Time: Double,
            @Serializable(with = StringSerializer::class) val heartRateZone3Time: Double,
            val strokeRate: Int,
            @Serializable(with = StringSerializer::class) val workTime: Double,
            val maxSPM: Int,
            val strokeCount: Int,
            val splitSize: Int,
            val splits: List<Split>
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): WorkoutData {
                return WorkoutData(
                        SplitsWatts = map["SplitsWatts"].toString().toDouble(),
                        SplitsCals = map["SplitsCals"].toString().toDouble(),
                        SplitsAvgDrag = map["SplitsAvgDrag"].toString().toDouble(),
                        SplitsAvgDriveLength = map["SplitsAvgDriveLength"].toString().toDouble(),
                        SplitsAvgDPS = map["SplitsAvgDPS"].toString().toDouble(),
                        workDistance = map["workDistance"] as Int,
                        fastestPace = map["fastestPace"].toString().toDouble(),
                        splitsAvgPace = map["splitsAvgPace"].toString().toDouble(),
                        splitType = map["splitType"] as Int,
                        maxWatt = map["maxWatt"] as Int,
                        heartRate = map["heartRate"] as Int,
                        maxHeartRate = map["maxHeartRate"].toString().toDouble(),
                        heartRateNormalZoneTime = map["heartRateNormalZoneTime"].toString().toDouble(),
                        heartRateZone1Time = map["heartRateZone1Time"].toString().toDouble(),
                        heartRateZone2Time = map["heartRateZone2Time"].toString().toDouble(),
                        heartRateZone3Time = map["heartRateZone3Time"].toString().toDouble(),
                        strokeRate = map["strokeRate"] as Int,
                        workTime = map["workTime"].toString().toDouble(),
                        maxSPM = map["maxSPM"] as Int,
                        strokeCount = map["strokeCount"] as Int,
                        splitSize = map["splitSize"] as Int,
                        splits = (map["splits"] as List<Map<String, Any>>).map { Split.fromMap(it) }
                )
            }
        }

        fun getData() : MutableList<SummaryItem> {
            var list = mutableListOf<SummaryItem>()
            list.add(SummaryItem("PEAK RATE", maxSPM.toString()))
            list.add(SummaryItem("PEAK POWER", maxWatt.toString()))
            list.add(SummaryItem("HEART RATE", heartRate.toString()))
            list.add(SummaryItem("POWER", SplitsWatts.toString()))
            list.add(SummaryItem("PEAK PACE", fastestPace.secondsToTimespan(true)))
            list.add(SummaryItem("DISTANCE / STROKE", SplitsAvgDPS.toString()))
            list.add(SummaryItem("RATE", strokeRate.toString()))
            list.add(SummaryItem("DRAG", SplitsAvgDrag.toString()))
            list.add(SummaryItem("CALORIES", SplitsCals.toString()))
            list.add(SummaryItem("PEAK HEART RATE", maxHeartRate.toString()))
            list.add(SummaryItem("PACE", splitsAvgPace.secondsToTimespan(true)))
            list.add(SummaryItem("STROKES", strokeCount.toString()))
            list.add(SummaryItem("TIME", workTime.secondsToTimespan(true)))
            list.add(SummaryItem("STROKE LENGTH", SplitsAvgDriveLength.toString()))
            list.add(SummaryItem("SPLIT SIZE", splitSize.toString()))
            list.add(SummaryItem("DISTANCE", workDistance.toString()))
            return list
        }
    }

    data class SummaryItem (
            val key: String,
            val value: String
    )

    @Serializable
    data class DataPointWrapper(
            val objects: List<DataPoint>
    )

    @Serializable
    data class DataPoint(
            val caloriesBurned: Int,
            val dragFactor: Int,
            val heartRate: Int,
            val interval: Int,
            val isRow: Boolean,
            val meters: Double,
            val split: Int,
            val splitTime: Double,
            val strokeLength: Int,
            val strokeTime: Int,
            val strokesPerMinute: Int,
            val time: Double,
            val timestamp: Long,
            val watts: Int,
            val workoutState: WorkoutState = WorkoutState.WAITTOBEGIN
    )

    @Serializable
    data class Stroke(
            val t: Int,
            val d: Int,
            val p: Int,
            val spm: Int,
            val hr: Int
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): Stroke {
                return Stroke(
                        map["t"] as Int,
                        map["d"] as Int,
                        map["p"] as Int,
                        map["spm"] as Int,
                        map["hr"] as Int
                )
            }
        }
    }

    @Serializable
    data class Split(
            @Serializable(with = StringSerializer::class) val splitDistance: Double,
            @Serializable(with = StringSerializer::class) val splitHeartRate: Double,
            val splitStrokeRate: Int,
            @Serializable(with = StringSerializer::class) val splitTimeDistance: Double,
            @Serializable(with = StringSerializer::class) val splitRestDistance: Double,
            @Serializable(with = StringSerializer::class) val splitRestTime: Double,
            @Serializable(with = StringSerializer::class) val splitAvgDPS: Double,
            @Serializable(with = StringSerializer::class) val splitCals: Double,
            @Serializable(with = StringSerializer::class) val splitTime: Double,
            @Serializable(with = StringSerializer::class) val splitAvgWatts: Double,
            val splitAvgDragFactor: Int,
            @Serializable(with = StringSerializer::class) val splitAvgPace: Double,
            @Serializable(with = StringSerializer::class) val splitAvgDriveLength: Double,
            val splitStrokeCount: Int,
            val splitNumber: Int
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): Split {
                return Split(
                        splitDistance = map["splitDistance"].toString().toDouble(),
                        splitHeartRate = map["splitHeartRate"].toString().toDouble(),
                        splitStrokeRate = map["splitStrokeRate"] as Int,
                        splitTimeDistance = map["splitTimeDistance"].toString().toDouble(),
                        splitRestDistance = map["splitRestDistance"].toString().toDouble(),
                        splitRestTime = map["splitRestTime"].toString().toDouble(),
                        splitAvgDPS = map["splitAvgDPS"].toString().toDouble(),
                        splitCals = map["splitCals"].toString().toDouble(),
                        splitTime = map["splitTime"].toString().toDouble(),
                        splitAvgWatts = map["splitAvgWatts"].toString().toDouble(),
                        splitAvgDragFactor = map["splitAvgDragFactor"] as Int,
                        splitAvgPace = map["splitAvgPace"].toString().toDouble(),
                        splitAvgDriveLength = map["splitAvgDriveLength"].toString().toDouble(),
                        splitStrokeCount = map["splitStrokeCount"] as Int,
                        splitNumber = map["splitNumber"] as Int
                )
            }
        }

        fun getMap() : MutableMap<SplitType, String> {
            var map = mutableMapOf<SplitType, String>()
            map.put(SplitType.Number, splitNumber.toString())
            map.put(SplitType.Time, splitTime.secondsToTimespan(true))
            map.put(SplitType.StrokeRate, splitStrokeRate.toString())
            map.put(SplitType.DPS, splitAvgDPS.toString())
            map.put(SplitType.Dist, splitDistance.toInt().toString())
            map.put(SplitType.RestTime, splitRestTime.secondsToTimespan(true))
            map.put(SplitType.DragFactor, splitAvgDragFactor.toString())
            map.put(SplitType.Watts, splitAvgWatts.toString())
            map.put(SplitType.TimeDist, splitTimeDistance.toString())
            map.put(SplitType.HeartRate, splitHeartRate.toString())
            map.put(SplitType.Pace, splitAvgPace.secondsToTimespan(true))
            map.put(SplitType.RestDist, splitRestDistance.toString())
            map.put(SplitType.Cals, splitCals.toString())
            map.put(SplitType.StrokeCount, splitStrokeCount.toString())
            map.put(SplitType.DriveLength, splitAvgDriveLength.toString())

            return map
        }
    }
}
