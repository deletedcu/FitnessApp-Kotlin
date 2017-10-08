package com.liverowing.liverowing.api.model

import com.parse.ParseFile
import com.parse.ParseRelation
import com.parse.ParseUser
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
class User : ParseUser() {
    var isMetric by ParseDelegate<Boolean?>()
    var boatColor by ParseDelegate<Int?>()
    var hatColor by ParseDelegate<Int?>()
    var gender by ParseDelegate<String?>()
    var height by ParseDelegate<Int?>()
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
    var isHeavyWeight by ParseDelegate<Boolean?>()
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
}
