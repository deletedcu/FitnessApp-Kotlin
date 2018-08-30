package com.liverowing.android.model.parse

import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_CARDIO
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_CROSS_TRAINING
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_HIIT
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_POWER
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_SPEED
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.TAG_WEIGHT_LOSS
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
    var filterTags by ParseDelegate<List<Int>?>()
    val filterTagsFriendly: List<String>
        get() {
            val mapping =  hashMapOf(
                    TAG_POWER to "Power",
                    TAG_CARDIO to "Cardio",
                    TAG_HIIT to "HIIT",
                    TAG_CROSS_TRAINING to "Cross Training",
                    TAG_SPEED to "Speed",
                    TAG_WEIGHT_LOSS to "Weight loss"
            )
            val tags = mutableListOf<String>()
            filterTags?.forEachIndexed { index, state -> if (state == 1) { tags.add(mapping[index]!!) } }

            return tags
        }
    var likes by ParseDelegate<Int?>()
    var namedChallenger by ParseDelegate<User?>()
    var fixedChallenge by ParseDelegate<Workout?>()
    var isAutoCompete by ParseDelegate<Boolean?>()
    val friendlySegmentDescription: String
        get() {
            val parts = mutableListOf<String>()
            if (segments != null) {
                var number = 1
                var lastSegment: Segment? = null
                for (segment in segments!!) {
                    if (segment.isDataAvailable) {
                        if (lastSegment != null && segment.friendlyValue == lastSegment.friendlyValue && segment.friendlyRestValue == lastSegment.friendlyRestValue) {
                            number++
                        }
                        lastSegment = segment
                        parts.add(segment.friendlyValue)
                        parts.add(segment.friendlyRestValue)
                    }
                }

                if (segments!!.size == number && lastSegment != null) {
                    var string = "$number x ${lastSegment.friendlyValue}"
                    if (lastSegment.restValue != null && lastSegment.restValue!! > 0) {
                        string += "/${lastSegment.friendlyRestValue}"
                    }
                    return string
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

        fun featuredWorkouts(): ParseQuery<WorkoutType> {
            var featuredUsers = ParseQuery.getQuery(User::class.java)
            featuredUsers.whereEqualTo("isFeatured", true)

            val featuredWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            featuredWorkouts.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE

            featuredWorkouts.whereMatchesKeyInQuery("createdBy", "objectId", featuredUsers)
            featuredWorkouts.whereNotEqualTo("isDeleted", true)

            featuredWorkouts.include("segments")
            featuredWorkouts.include("createdBy")

            featuredWorkouts.addDescendingOrder("createdAt")

            return featuredWorkouts
        }

        fun popularWorkouts(): ParseQuery<WorkoutType> {
            val popularWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            popularWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            popularWorkouts.whereEqualTo("isPublic", true)

            popularWorkouts.include("createdBy")
            popularWorkouts.include("segments")
            popularWorkouts.addDescendingOrder("likes")
            popularWorkouts.limit = 15

            return popularWorkouts
        }

        fun recentWorkouts(): ParseQuery<WorkoutType> {
            val workouts = ParseQuery.getQuery(Workout::class.java)
            workouts.whereEqualTo("createdBy", ParseUser.getCurrentUser())

            val recentWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            recentWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            recentWorkouts.whereMatchesKeyInQuery("objectId", "workoutType", workouts)

            recentWorkouts.include("createdBy")
            recentWorkouts.include("segments")

            recentWorkouts.addDescendingOrder("createdAt")

            return recentWorkouts
        }

        fun recentAndLikedWorkouts(): ParseQuery<WorkoutType> {
            val completedWorkouts = ParseQuery.getQuery(Workout::class.java)
            completedWorkouts.whereEqualTo("createdBy", ParseUser.getCurrentUser())
            completedWorkouts.whereEqualTo("isDone", true)

            val recentAndLikedWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            recentAndLikedWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            recentAndLikedWorkouts.whereMatchesKeyInQuery("objectId", "workoutType", completedWorkouts)

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
