package com.liverowing.liverowing.api.model

import com.parse.*
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("WorkoutType")
class WorkoutType : ParseObject() {
    var descriptionText by ParseDelegate<String?>()
    var name by ParseDelegate<String?>()
    var value by ParseDelegate<Int?>()
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
    var isDone by ParseDelegate<Boolean?>()
    var isPublic by ParseDelegate<Boolean?>()
    var filterTags by ParseDelegate<List<String>?>()
    var likes by ParseDelegate<Int?>()
    var namedChallenger by ParseDelegate<User?>()
    var fixedChallenge by ParseDelegate<Workout?>()
    var isAutoCompete by ParseDelegate<Boolean?>()

    companion object {
        fun featuredWorkouts() : ParseQuery<WorkoutType> {
            val featuredUsers = ParseQuery.getQuery(User::class.java)
            featuredUsers.whereEqualTo("isFeatured", true)

            val featuredWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            featuredWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK

            featuredWorkouts.include("createdBy")
            featuredWorkouts.whereEqualTo("isFeatured", true)
            featuredWorkouts.whereMatchesQuery("createdBy", featuredUsers)
            featuredWorkouts.addAscendingOrder("createdBy")
            featuredWorkouts.addDescendingOrder("createdAt")

            return featuredWorkouts
        }

        fun recentAndLikedWorkouts() : ParseQuery<WorkoutType> {
            val completedWorkouts = ParseQuery.getQuery(Workout::class.java)
            completedWorkouts.whereEqualTo("createdBy", ParseUser.getCurrentUser())
            completedWorkouts.whereEqualTo("isDone", true)

            val recentAndLikedWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            recentAndLikedWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            recentAndLikedWorkouts.whereMatchesKeyInQuery("objectId", "workoutType", completedWorkouts)
            recentAndLikedWorkouts.limit = 10

            return recentAndLikedWorkouts
        }

        fun communityWorkouts(): ParseQuery<WorkoutType> {
            val communityWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            communityWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            communityWorkouts.include("createdBy")
            communityWorkouts.whereEqualTo("isPublic", true)

            return communityWorkouts
        }

        fun affiliateWorkouts(): ParseQuery<WorkoutType> {
            val affiliatedUsers = ParseQuery.getQuery(User::class.java)
            affiliatedUsers.whereEqualTo("isAffiliate", true)

            val affiliateWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
            affiliateWorkouts.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK

            affiliateWorkouts.include("createdBy")
            affiliateWorkouts.whereMatchesQuery("createdBy", affiliatedUsers)

            return affiliateWorkouts
        }
    }
}
