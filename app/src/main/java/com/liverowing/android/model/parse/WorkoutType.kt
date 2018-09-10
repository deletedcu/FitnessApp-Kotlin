package com.liverowing.android.model.parse

import android.util.Log
import com.parse.*
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("WorkoutType")
class WorkoutType : ParseObject() {
    var descriptionText by ParseDelegate<String?>()
    var name by ParseDelegate<String?>()
    var value by ParseDelegate<Int>()
    var valueType by ParseDelegate<Int?>()
    var type by ParseDelegate<Int?>()
    var segments by ParseDelegate<List<Segment>?>()
    var image by ParseDelegate<ParseFile?>()
    var isDefault by ParseDelegate<Boolean?>()
    var isFeatured by ParseDelegate<Boolean?>()
    var isPremium by ParseDelegate<Boolean?>()
    var createdBy by ParseDelegate<User?>()
    var sharedWith by ParseDelegate<ParseRelation<User>?>()
    var isDeleted by ParseDelegate<Boolean?>()
    var affiliate by ParseDelegate<Affiliate?>()
    var emailTemplate by ParseDelegate<String?>()
    var emailMergeLanguage by ParseDelegate<String?>()
    var linkedWorkoutTypes by ParseDelegate<Dictionary<String, String>?>()
    var splits by ParseDelegate<List<Int>?>()
    var splitLength by ParseDelegate<Int?>()
    val calculatedSplitLength: Int
        get() {
            if (has("splitLength") && splitLength!! > 0) {
                return splitLength!!
            }

            return when (valueType) {
                VALUE_TYPE_DISTANCE -> if (value >= 500) value / 5 else 100
                VALUE_TYPE_TIMED -> if (value == 240) 60 else if (value in 0..60) 20 else value / 5
                else -> value
            }
        }

    val calculatedSplitNum: Int
        get() {
            return value / calculatedSplitLength
        }

    var isDone by ParseDelegate<Boolean?>()
    var isPublic by ParseDelegate<Boolean?>()
    var filterTags by ParseDelegate<List<String>?>()
    var likes by ParseDelegate<Int?>()
    var namedChallenger by ParseDelegate<User?>()
    var fixedChallenge by ParseDelegate<Workout?>()
    var isAutoCompete by ParseDelegate<Boolean?>()
    val friendlySegmentDescription: String
        get() {
            val parts = mutableListOf<String>()
            if (segments != null) {
                for (segment in segments!!) {
                    if (segment.isDataAvailable) {
                        parts.add(segment.friendlyValue)
                        parts.add(segment.friendlyRestValue)
                    }
                }
            }

            return parts.joinToString("/")
        }
    val hasLeaderboards: Boolean
        get() {
            return isFeatured == true || affiliate != null
        }

    companion object {
        const val VALUE_TYPE_DISTANCE = 1
        const val VALUE_TYPE_TIMED = 2
        const val VALUE_TYPE_CALORIE = 3
        const val VALUE_TYPE_CUSTOM = 4
        const val VALUE_TYPE_JUSTROW = 5

        const val SEGMENT_VALUE_TYPE_TIMED = 1
        const val SEGMENT_VALUE_TYPE_DISTANCE = 2

        const val REST_TYPE_NORMAL = 0
        const val REST_TYPE_VARIABLE = 1

        fun featuredWorkouts(user: User? = null): ParseQuery<WorkoutType> {
            val featuredWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            featuredWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK

            featuredWorkouts.whereEqualTo("isFeatured", true)
            featuredWorkouts.whereNotEqualTo("isDeleted", true)

            featuredWorkouts.include("segments")
            featuredWorkouts.include("createdBy")

            featuredWorkouts.addDescendingOrder("createdAt")

            return featuredWorkouts
        }

        fun recentAndLikedWorkouts(): ParseQuery<WorkoutType> {
            val completedWorkouts = ParseQuery.getQuery(Workout::class.java)
            completedWorkouts.whereEqualTo("createdBy", ParseUser.getCurrentUser())
            completedWorkouts.whereEqualTo("isDone", true)

            val recentAndLikedWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            recentAndLikedWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            recentAndLikedWorkouts.whereMatchesKeyInQuery("objectId", "workoutType.objectId", completedWorkouts)

            recentAndLikedWorkouts.include("createdBy")
            recentAndLikedWorkouts.include("segments")

            return recentAndLikedWorkouts
        }

        fun communityWorkouts(): ParseQuery<WorkoutType> {
            val communityWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            communityWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            communityWorkouts.whereEqualTo("isPublic", true)

            communityWorkouts.include("createdBy")
            communityWorkouts.include("segments")

            return communityWorkouts
        }

        fun affiliateWorkouts(): ParseQuery<WorkoutType> {
            val affiliatedUsers = ParseQuery.getQuery(User::class.java)
            affiliatedUsers.whereEqualTo("isAffiliate", true)

            val affiliateWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            affiliateWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            affiliateWorkouts.whereMatchesQuery("createdBy", affiliatedUsers)

            affiliateWorkouts.include("createdBy")
            affiliateWorkouts.include("segments")

            return affiliateWorkouts
        }

        fun myCustomWorkouts(): ParseQuery<WorkoutType> {
            val myCustomWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            myCustomWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            myCustomWorkouts.whereEqualTo("createdBy", ParseUser.getCurrentUser())

            myCustomWorkouts.include("createdBy")
            myCustomWorkouts.include("segments")

            return myCustomWorkouts
        }

        fun fetchWorkout(objectId: String): WorkoutType {
            val search = ParseQuery.getQuery(WorkoutType::class.java)
            search.include("createdBy")
            search.include("segments")

            return search.get(objectId)
        }
    }
}
