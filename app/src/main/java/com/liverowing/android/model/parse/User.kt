package com.liverowing.android.model.parse

import android.content.Context
import com.liverowing.android.R.array.hat_colors
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseRelation
import com.parse.ParseUser
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
class User : ParseUser() {
    var isMetric by ParseDelegate<Boolean?>()
    var boatColor by ParseDelegate<Int>()
    var hatColor by ParseDelegate<Int>()
    var gender by ParseDelegate<String?>()
    var height by ParseDelegate<Number?>()
    var dob by ParseDelegate<Date?>()
    var weight by ParseDelegate<Int?>()
    var description by ParseDelegate<String?>()
    var image by ParseDelegate<ParseFile?>()
    var friendships by ParseDelegate<ParseRelation<User>?>()
    var origin by ParseDelegate<String?>()
    var recurlyCode by ParseDelegate<String?>()
    var stats by ParseDelegate<Stats?>()
    var isFeatured by ParseDelegate<Boolean?>()
    var paidThru by ParseDelegate<Date?>()
    var affiliateJoined by ParseDelegate<Date?>()
    var displayName by ParseDelegate<String?>()
    var roles by ParseDelegate<String?>()
    var isHeavyweight by ParseDelegate<Boolean?>()
    var featureUsers by ParseDelegate<List<User>?>()
    var unfeatureUsers by ParseDelegate<List<User>?>()
    var reGrow by ParseDelegate<Date?>()
    var reGrown by ParseDelegate<Int?>()
    var currentGoal by ParseDelegate<Goals?>()
    var config by ParseDelegate<Dictionary<String, String>?>()
    var data by ParseDelegate<Dictionary<String, String>?>()
    var status by ParseDelegate<Int?>()
    var statusText by ParseDelegate<String?>()
    var getFullAccessLink by ParseDelegate<String?>()
    var maxHR by ParseDelegate<Int?>()
    var getFullAccessLinkLabel by ParseDelegate<String?>()
    var rotationRank by ParseDelegate<Int?>()
    var `class` by ParseDelegate<Int?>()

    // Default values
    fun initValues() {
        boatColor = 1
        hatColor = 1
        `class` = 1
    }

    // Calculated fields
    val userClass: String
        get() {
            val gender = if (this.gender.isNullOrEmpty()) "male" else this.gender!!.toLowerCase()
            val heavyWeight = if (this.isHeavyweight == true) 1 else 0
            return gender + heavyWeight.toString()
        }

    fun calcIsHeavyweight() {
        if (gender != null) {
            var value: Boolean
            if (gender!!.toLowerCase().equals("male")) {
                if (isMetric!!) {
                    value = weight!! > 75
                } else {
                    value = weight!! > 165
                }
            } else {
                if (isMetric!!) {
                    value = weight!! > 61.5
                } else {
                    value = weight!! > 135
                }
            }
            isHeavyweight = value
        }
    }

    fun getFlagColor(context: Context): Int {
            val colors = context.resources.getIntArray(hat_colors)
            if (hatColor in 0 until colors.size) {
                return colors[hatColor]
            }
            return colors[0]
        }

    companion object {
        fun completedWorkouts(): ParseQuery<Workout> {
            val query = ParseQuery.getQuery(Workout::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereEqualTo("createdBy", ParseUser.getCurrentUser())
            query.whereEqualTo("isDone", true)
            query.whereExists("workoutType")

            return query
        }

        fun featuredUsers(): ParseQuery<User> {
            val query = ParseQuery.getQuery(User::class.java)
            query.cachePolicy = ParseQuery.CachePolicy.CACHE_THEN_NETWORK
            query.whereEqualTo("isFeatured", true)

            return query
        }
    }
}
