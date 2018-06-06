package com.liverowing.android.model.parse

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import java.util.*

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("Goals")
class Goals : ParseObject() {
    var isFixedDateRange by ParseDelegate<Boolean?>()
    var nJoinedUsers by ParseDelegate<Int?>()
    var isVisible by ParseDelegate<Boolean?>()
    var dayLength by ParseDelegate<Int?>()
    var badge by ParseDelegate<ParseFile?>()
    var subTitle by ParseDelegate<String?>()
    var endDate by ParseDelegate<Date?>()
    var scale by ParseDelegate<Int?>()
    var startDate by ParseDelegate<Date?>()
    var isUnlimited by ParseDelegate<Boolean?>()
    var title by ParseDelegate<String?>()
    var image by ParseDelegate<ParseFile?>()
    var descriptionText by ParseDelegate<String?>()
    var goalsType by ParseDelegate<Int?>()
    var createdBy by ParseDelegate<User?>()

}