package com.liverowing.android.model.parse

import android.util.Base64
import com.liverowing.android.model.pm.WorkoutState
import com.parse.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import org.json.JSONException
import org.json.JSONObject
import java.util.*

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
            val strokeRate: Int,
            @Serializable(with = StringSerializer::class) val SplitsAvgDPS: Number,
            @Serializable(with = StringSerializer::class) val heartRateNormalZoneTime: Number,
            @Serializable(with = StringSerializer::class) val SplitsCals: Number,
            @Serializable(with = StringSerializer::class) val heartRateZone1Time: Number,
            @Serializable(with = StringSerializer::class) val SplitsAvgDrag: Number,
            @Serializable(with = StringSerializer::class) val SplitsAvgDriveLength: Number,
            @Serializable(with = StringSerializer::class) val heartRateZone2Time: Number,
            val maxWatt: Int,
            @Serializable(with = StringSerializer::class) val splitsAvgPace: Number,
            @Serializable(with = StringSerializer::class) val fastestPace: Number,
            @Serializable(with = StringSerializer::class) val heartRateZone3Time: Number,
            @Serializable(with = StringSerializer::class) val maxHeartRate: Number,
            val splitSize: Int,
            @Serializable(with = StringSerializer::class) val SplitsWatts: Number,
            val maxSPM: Int,
            val workDistance: Int,
            val heartRate: Int,
            @Serializable(with = StringSerializer::class) val workTime: Number,
            val strokeCount: Int,
            val splitType: Int,
            val splits: List<Split>
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): WorkoutData {
                return WorkoutData(
                        strokeRate = map["strokeRate"] as Int,
                        SplitsAvgDPS = map["SplitsAvgDPS"] as Number,
                        heartRateNormalZoneTime = map["heartRateNormalZoneTime"] as Number,
                        SplitsCals = map["SplitsCals"] as Number,
                        heartRateZone1Time = map["heartRateZone1Time"] as Number,
                        SplitsAvgDrag = map["SplitsAvgDrag"] as Number,
                        SplitsAvgDriveLength = map["SplitsAvgDriveLength"] as Number,
                        heartRateZone2Time = map["heartRateZone2Time"] as Number,
                        maxWatt = map["maxWatt"] as Int,
                        splitsAvgPace = map["splitsAvgPace"] as Number,
                        fastestPace = map["fastestPace"] as Number,
                        heartRateZone3Time = map["heartRateZone3Time"] as Number,
                        maxHeartRate = map["maxHeartRate"] as Number,
                        splitSize = map["splitSize"] as Int,
                        SplitsWatts = map["SplitsWatts"] as Number,
                        maxSPM = map["maxSPM"] as Int,
                        workDistance = map["workDistance"] as Int,
                        heartRate = map["heartRate"] as Int,
                        workTime = map["workTime"] as Number,
                        strokeCount = map["strokeCount"] as Int,
                        splitType = map["splitType"] as Int,
                        splits = (map["splits"] as List<Map<String, Any>>).map { Split.fromMap(it) }
                )
            }
        }
    }

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
            val splitNumber: Int,
            @Serializable(with = StringSerializer::class) val splitTime: Number,
            val splitStrokeRate: Int,
            @Serializable(with = StringSerializer::class) val splitAvgDPS: Number,
            @Serializable(with = StringSerializer::class) val splitDistance: Number,
            @Serializable(with = StringSerializer::class) val splitRestTime: Number,
            val splitAvgDragFactor: Int,
            @Serializable(with = StringSerializer::class) val splitAvgWatts: Number,
            @Serializable(with = StringSerializer::class) val splitTimeDistance: Number,
            @Serializable(with = StringSerializer::class) val splitHeartRate: Number,
            @Serializable(with = StringSerializer::class) val splitAvgPace: Number,
            @Serializable(with = StringSerializer::class) val splitRestDistance: Number,
            @Serializable(with = StringSerializer::class) val splitCals: Number,
            val splitStrokeCount: Int,
            @Serializable(with = StringSerializer::class) val splitAvgDriveLength: Number
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>): Split {
                return Split(
                        splitNumber = map["splitNumber"] as Int,
                        splitTime = map["splitTime"] as Number,
                        splitStrokeRate = map["splitStrokeRate"] as Int,
                        splitAvgDPS = map["splitAvgDPS"] as Number,
                        splitDistance = map["splitDistance"] as Number,
                        splitRestTime = map["splitRestTime"] as Number,
                        splitAvgDragFactor = map["splitAvgDragFactor"] as Int,
                        splitAvgWatts = map["splitAvgWatts"] as Number,
                        splitTimeDistance = map["splitTimeDistance"] as Number,
                        splitHeartRate = map["splitHeartRate"] as Number,
                        splitAvgPace = map["splitAvgPace"] as Number,
                        splitRestDistance = map["splitRestDistance"] as Number,
                        splitCals = map["splitCals"] as Number,
                        splitStrokeCount = map["splitStrokeCount"] as Int,
                        splitAvgDriveLength = map["splitAvgDriveLength"] as Number
                )
            }
        }
    }
}
