package com.liverowing.liverowing.model.parse

import android.util.Base64
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.Map

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("Workout")
class Workout : ParseObject() {
    var caloriesBurned by ParseDelegate<Int?>()
    var finishTime by ParseDelegate<Date?>()
    var workoutType by ParseDelegate<WorkoutType?>()
    var averageSPM by ParseDelegate<Int?>()
    var startTime by ParseDelegate<Date?>()
    var averageWatts by ParseDelegate<Int?>()
    var duration by ParseDelegate<Double?>()
    var averageSplitTime by ParseDelegate<Float?>()
    var isDone by ParseDelegate<Boolean?>()
    var averageHeartRate by ParseDelegate<Int?>()
    var meters by ParseDelegate<Int?>()
    var data: Data
        get() = Data.fromMap(getMap<Any>("data"))
        set(value) = put("data", value)
    var createdBy by ParseDelegate<User?>()
    var isChallenge by ParseDelegate<Boolean?>()
    var dataPoints by ParseDelegate<ParseFile?>()
    var totalStrokeCount by ParseDelegate<Int?>()
    var totalTime by ParseDelegate<Int?>()
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
                                                it.getInt("splitTime"),
                                                it.getInt("strokeLength"),
                                                it.getInt("strokeTime"),
                                                it.getInt("strokesPerMinute"),
                                                it.getDouble("time"),
                                                it.getInt("timestamp"),
                                                it.getInt("watts")
                                        )
                                )
                            }
                }
            } catch (e: JSONException) {}
        }

    }


    data class Data(
            val workoutMachineType: String,
            val StrokeData: List<Stroke>,
            val WorkoutData: WorkoutData
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>) : Data {
                return Data(
                        map["workoutMachineType"] as String,
                        (map["StrokeData"] as List<Map<String, Any>>).map { Stroke.fromMap(it) },
                        WorkoutData.fromMap(map["WorkoutData"] as Map<String, Any>)
                )
            }
        }
    }


    class WorkoutData(
            val SplitsWatts: Double,
            val SplitsCals: Double,
            val SplitsAvgDrag: Double,
            val SplitsAvgDriveLength: Double,
            val SplitsAvgDPS: Double,
            val workDistance: Int,
            val fastestPace: Double,
            val splitsAvgPace: Double,
            val splitType: Int,
            val heartRateZone1Time: Double,
            val maxWatt: Double,
            val heartRate: Int,
            val maxHeartRate: Double,
            val heartRateNormalZoneTime: Double,
            val heartRateZone2Time: Double,
            val heartRateZone3Time: Double,
            val strokeRate: Int,
            val workTime: Double,
            val maxSPM: Double,
            val strokeCount: Int,
            val splitSize: Int,
            val splits: List<Split>
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>) : WorkoutData {
                return WorkoutData(
                        map["SplitsWatts"].toString().toDouble(),
                        map["SplitsCals"].toString().toDouble(),
                        map["SplitsAvgDrag"].toString().toDouble(),
                        map["SplitsAvgDriveLength"].toString().toDouble(),
                        map["SplitsAvgDPS"].toString().toDouble(),
                        map["workDistance"] as Int,
                        map["fastestPace"].toString().toDouble(),
                        map["splitsAvgPace"].toString().toDouble(),
                        map["splitType"] as Int,
                        map["heartRateZone1Time"].toString().toDouble(),
                        map["maxWatt"].toString().toDouble(),
                        map["heartRate"] as Int,
                        map["maxHeartRate"].toString().toDouble(),
                        map["heartRateNormalZoneTime"].toString().toDouble(),
                        map["heartRateZone2Time"].toString().toDouble(),
                        map["heartRateZone3Time"].toString().toDouble(),
                        map["strokeRate"] as Int,
                        map["workTime"].toString().toDouble(),
                        map["maxSPM"].toString().toDouble(),
                        map["strokeCount"] as Int,
                        map["splitSize"] as Int,
                        (map["splits"] as List<Map<String, Any>>).map { Split.fromMap(it) }
                )
            }
        }
    }

    data class DataPoint(
            val caloriesBurned: Int,
            val dragFactor: Int,
            val heartRate: Int,
            val interval: Int,
            val isRow: Boolean,
            val meters: Double,
            val split: Int,
            val splitTime: Int,
            val strokeLength: Int,
            val strokeTime: Int,
            val strokesPerMinute: Int,
            val time: Double,
            val timestamp: Int,
            val watts: Int
    )

    data class Stroke(
            val t: Int,
            val d: Int,
            val p: Int,
            val spm: Int,
            val hr: Int
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>) : Stroke {
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

    data class Split(
            val splitAvgDragFactor: Int,
            val splitAvgWatts: Double,
            val splitAvgPace: Double,
            val splitAvgDPS: Double,
            val splitHeartRate: Double,
            val splitStrokeCount: Int,
            val splitTime: Int,
            val splitDistance: Int,
            val splitAvgDriveLength: Double,
            val splitTimeDistance: Double,
            val splitCals: Double,
            val splitStrokeRate: Int,
            val splitNumber: Int
    ) {
        companion object {
            fun fromMap(map: Map<String, Any>) : Split {
                return Split(
                        map["splitAvgDragFactor"] as Int,
                        map["splitAvgWatts"].toString().toDouble(),
                        map["splitAvgPace"].toString().toDouble(),
                        map["splitAvgDPS"].toString().toDouble(),
                        map["splitHeartRate"].toString().toDouble(),
                        map["splitStrokeCount"] as Int,
                        map["splitTime"] as Int,
                        map["splitDistance"] as Int,
                        map["splitAvgDriveLength"].toString().toDouble(),
                        map["splitTimeDistance"].toString().toDouble(),
                        map["splitCals"].toString().toDouble(),
                        map["splitStrokeRate"] as Int,
                        map["splitNumber"] as Int
                )
            }
        }
    }
}
