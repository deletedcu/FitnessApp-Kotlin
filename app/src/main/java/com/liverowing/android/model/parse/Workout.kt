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
        fun fetchWorkout(objectId: String): Workout {
            val search = ParseQuery.getQuery(Workout::class.java)
            search.include("createdBy")
            return search.get(objectId)
        }

        fun forUser(user: ParseUser): ParseQuery<Workout> {
            val userWorkouts = ParseQuery.getQuery(Workout::class.java)
            userWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            userWorkouts.whereEqualTo("createdBy", user)

            userWorkouts.include("workoutType.createdBy")
            userWorkouts.addDescendingOrder("createdAt")

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
                        SplitsWatts = map["SplitsWatts"] as Double,
                        SplitsCals = map["SplitsCals"] as Double,
                        SplitsAvgDrag = map["SplitsAvgDrag"] as Double,
                        SplitsAvgDriveLength = map["SplitsAvgDriveLength"] as Double,
                        SplitsAvgDPS = map["SplitsAvgDPS"] as Double,
                        workDistance = map["workDistance"] as Int,
                        fastestPace = map["fastestPace"] as Double,
                        splitsAvgPace = map["splitsAvgPace"] as Double,
                        splitType = map["splitType"] as Int,
                        maxWatt = map["maxWatt"] as Int,
                        heartRate = map["heartRate"] as Int,
                        maxHeartRate = map["maxHeartRate"] as Double,
                        heartRateNormalZoneTime = map["heartRateNormalZoneTime"] as Double,
                        heartRateZone1Time = map["heartRateZone1Time"] as Double,
                        heartRateZone2Time = map["heartRateZone2Time"] as Double,
                        heartRateZone3Time = map["heartRateZone3Time"] as Double,
                        strokeRate = map["strokeRate"] as Int,
                        workTime = map["workTime"] as Double,
                        maxSPM = map["maxSPM"] as Int,
                        strokeCount = map["strokeCount"] as Int,
                        splitSize = map["splitSize"] as Int,
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
                        splitDistance = map["splitDistance"] as Double,
                        splitHeartRate = map["splitHeartRate"] as Double,
                        splitStrokeRate = map["splitStrokeRate"] as Int,
                        splitTimeDistance = map["splitTimeDistance"] as Double,
                        splitRestDistance = map["splitRestDistance"] as Double,
                        splitRestTime = map["splitRestTime"] as Double,
                        splitAvgDPS = map["splitAvgDPS"] as Double,
                        splitCals = map["splitCals"] as Double,
                        splitTime = map["splitTime"] as Double,
                        splitAvgWatts = map["splitAvgWatts"] as Double,
                        splitAvgDragFactor = map["splitAvgDragFactor"] as Int,
                        splitAvgPace = map["splitAvgPace"] as Double,
                        splitAvgDriveLength = map["splitAvgDriveLength"] as Double,
                        splitStrokeCount = map["splitStrokeCount"] as Int,
                        splitNumber = map["splitNumber"] as Int
                )
            }
        }
    }
}
