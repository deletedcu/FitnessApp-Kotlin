package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.extensions.addMonths
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.CATEGORY_AFFILIATE
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.CATEGORY_COMMUNITY
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.CATEGORY_FEATURED
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.CATEGORY_MY_CUSTOM
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.CATEGORY_RECENT
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.FILTER_ALL
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.FILTER_COMPLETED
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.FILTER_NEW
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.FILTER_NOT_COMPLETED
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment.Companion.FILTER_POPULAR
import com.parse.ParseException
import com.parse.ParseQuery
import java.util.*

class WorkoutBrowserPresenter : MvpBasePresenter<WorkoutBrowserView>() {
    private var query: ParseQuery<WorkoutType>? = null

    var category: Int = CATEGORY_FEATURED
    var filter: Int = FILTER_ALL
    val types = mutableSetOf<Int>()
    val tags = mutableSetOf<Int>()

    fun reset() {
        category = CATEGORY_FEATURED
        filter = FILTER_ALL
        types.clear()
        tags.clear()
    }

    fun loadWorkoutTypes(pullToRefresh: Boolean) {
        if (query !== null && query!!.isRunning) {
            query?.cancel()
        }

        // Category
        query = when(category) {
            CATEGORY_FEATURED -> WorkoutType.featuredWorkouts()
            CATEGORY_COMMUNITY -> WorkoutType.communityWorkouts()
            CATEGORY_RECENT -> WorkoutType.recentWorkouts()
            CATEGORY_MY_CUSTOM -> WorkoutType.myCustomWorkouts()
            CATEGORY_AFFILIATE -> WorkoutType.affiliateWorkouts()
            else -> WorkoutType.featuredWorkouts()
        }

        // Workout Types
        if (types.size > 0) {
            query?.whereContainedIn("valueType", types)
        }

        // Tags
        for (tag in tags) {
            query?.whereEqualTo("filterTags.$tag", 1)
        }

        // Filter
        when (filter) {
            FILTER_NEW -> query?.whereGreaterThanOrEqualTo("createdAt", Date().addMonths(-1))
            FILTER_POPULAR -> {
                query?.whereGreaterThanOrEqualTo("likes", 10)
                query?.orderByDescending("likes")
            }
            FILTER_COMPLETED -> query?.whereMatchesKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
            FILTER_NOT_COMPLETED -> query?.whereDoesNotMatchKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
        }

        ifViewAttached { it.showLoading(pullToRefresh) }

        query?.findInBackground { objects, e ->
            run {
                if (e !== null) {
                    if (e.code != ParseException.CACHE_MISS) {
                        ifViewAttached { it.showError(e, pullToRefresh) }
                    }
                } else {
                    if (category == CATEGORY_FEATURED) {
                        val result = mutableListOf<WorkoutType>()
                        objects.forEach { item ->
                            item.createdBy!!.rotationRank = item.createdBy!!.rotationRank ?: 9999
                            result.add(item)
                        }
                        val comparator = compareBy<WorkoutType>{ it.createdBy!!.rotationRank }.thenByDescending { it.createdAt }
                        objects.clear()
                        objects.addAll(result.sortedWith(comparator))
                    }

                    ifViewAttached {
                        it.setData(objects)
                        it.showContent()
                    }
                }
            }
        }
    }
}