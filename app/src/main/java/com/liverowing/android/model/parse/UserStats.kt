package com.liverowing.android.model.parse

import com.parse.ParseClassName
import com.parse.ParseCloud
import com.parse.ParseObject

/**
 * Created by henrikmalmberg on 2017-11-01.
 */
@ParseClassName("UserStatsRow")
class UserStats : ParseObject() {
    var user by ParseDelegate<User?>()
    val record: Record get() {
        val record = this.getJSONObject("record")
        return Record(
            record.getDouble("value"),
            record.getInt("rank"),
            record.getDouble("percentile"),
            record.getString("rankTrend"),
            record.getString("percentileTrend")
        )
    }

    companion object {
        fun leaderboardForWorkoutType(workoutType: WorkoutType, user: User) : List<UserStats> {
            val arguments = hashMapOf(
                    "userClass" to user.userClass,
                    "record" to "affiliateAndFeaturedWorkouts.WorkoutType\$${workoutType.objectId}"
            )
            return ParseCloud.callFunction<List<UserStats>>("query_userStats", arguments)
        }
    }
}