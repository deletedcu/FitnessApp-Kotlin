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
            val query = ParseQuery.getQuery(WorkoutType::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE

            query.whereMatchesQuery("createdBy", User.featuredUsers())
            query.whereNotEqualTo("isDeleted", true)
            query.whereEqualTo("isFeatured", true)

            query.include("segments")
            query.include("createdBy")

            query.orderByAscending("createdBy.rotationRank")
            query.addDescendingOrder("createdAt")

            return query
        }

        fun popularWorkouts(): ParseQuery<WorkoutType> {
            val query = communityWorkouts()
            query.addDescendingOrder("likes")
            query.limit = 15

            return query
        }

        fun recentWorkouts(): ParseQuery<WorkoutType> {
            val query = ParseQuery.getQuery(WorkoutType::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereMatchesKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())

            query.include("createdBy")
            query.include("segments")

            query.addDescendingOrder("createdAt")

            return query
        }

        fun recentAndLikedWorkouts(): ParseQuery<WorkoutType> {
            // TODO: This is wrong.
            val query = ParseQuery.getQuery(WorkoutType::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereMatchesKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())

            query.include("createdBy")
            query.include("segments")

            return query
        }

        fun communityWorkouts(): ParseQuery<WorkoutType> {
            val query = ParseQuery.getQuery(WorkoutType::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereEqualTo("isPublic", true)

            query.include("createdBy")
            query.include("segments")

            return query
        }

        fun affiliateWorkouts(): ParseQuery<WorkoutType> {
            val affiliatedUsers = ParseQuery.getQuery(User::class.java)
            affiliatedUsers.whereEqualTo("isAffiliate", true)

            val affiliateWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            affiliateWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            affiliateWorkouts.whereMatchesKeyInQuery("createdBy", "objectId", affiliatedUsers)

            affiliateWorkouts.include("createdBy")
            affiliateWorkouts.include("segments")

            return affiliateWorkouts
        }

        fun myCustomWorkouts(): ParseQuery<WorkoutType> {
            val query = ParseQuery.getQuery(WorkoutType::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereEqualTo("createdBy", ParseUser.getCurrentUser())

            query.include("createdBy")
            query.include("segments")

            return query
        }
    }
}
