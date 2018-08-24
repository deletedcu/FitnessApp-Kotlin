package com.liverowing.android.workoutshared.leaderboards

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.UserStats
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseCloud
import com.parse.ParseException

class WorkoutLeaderBoardsPresenter : MvpBasePresenter<WorkoutLeaderBoardsView>() {
    fun loadUserStats(pullToRefresh: Boolean, workoutType: WorkoutType, user: User) {
        ifViewAttached {  it.showLoading(pullToRefresh) }
        val arguments = hashMapOf(
                "userClass" to user.userClass,
                "record" to "affiliateAndFeaturedWorkouts.WorkoutType\$${workoutType.objectId}"
        )

        ParseCloud.callFunctionInBackground<List<UserStats>>("query_userStats", arguments) { objects, e ->
            if (e != null) {
                if (e.code != ParseException.CACHE_MISS) {
                    ifViewAttached { it.showError(e, pullToRefresh) }
                }
            } else {
                ifViewAttached { it.setData(objects) }
                ifViewAttached { it.showContent()}
            }
        }
    }
}