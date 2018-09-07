package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.extensions.addMonths
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.pm.FilterItem
import com.parse.ParseException
import com.parse.ParseQuery
import java.util.*

class WorkoutBrowserPresenter : MvpBasePresenter<WorkoutBrowserView>() {
    private var query: ParseQuery<WorkoutType>? = null

    var category = mutableListOf<FilterItem>(FilterItem.defaultGroupByItem())
    var createdBy = mutableListOf<FilterItem>()
    var filter = mutableListOf<FilterItem>()
    var types = mutableListOf<FilterItem>()
    var tags = mutableListOf<FilterItem>()

    private var isFirstLoading = false

    fun reset() {
        category.clear()
        category.add(FilterItem.defaultGroupByItem())
        createdBy.clear()
        filter.clear()
        types.clear()
        tags.clear()
    }

    fun loadWorkoutTypes(pullToRefresh: Boolean) {
        if (query !== null && query!!.isRunning) {
            query?.cancel()
        }
        isFirstLoading = false

        // Category
        val userIds = createdBy.map { it.objectId!! }
        query = when(category[0].key) {
            FilterItem.CATEGORY_FEATURED -> WorkoutType.featuredWorkouts(userIds)
            FilterItem.CATEGORY_COMMUNITY -> WorkoutType.communityWorkouts()
            FilterItem.CATEGORY_RECENT -> WorkoutType.recentWorkouts()
            FilterItem.CATEGORY_MY_CUSTOM -> WorkoutType.myCustomWorkouts()
            FilterItem.CATEGORY_AFFILIATE -> WorkoutType.affiliateWorkouts()
            else -> WorkoutType.featuredWorkouts()
        }

        if (category[0].key == FilterItem.CATEGORY_FEATURED && createdBy.size == 0) {
            isFirstLoading = true
        }

        // Workout Types
        if (types.size > 0) {
            val valueTypes = types.map { it.key }
            query?.whereContainedIn("valueType", valueTypes)
            isFirstLoading = false
        }

        // Tags
        if (tags.size > 0) {
            isFirstLoading = false
            for (tag in tags) {
                query?.whereEqualTo("filterTags.${tag.key}", 1)
            }
        }

        // Filter
        if (filter.size > 0) {
            isFirstLoading = false
            when (filter[0].key) {
                FilterItem.FILTER_NEW -> query?.whereGreaterThanOrEqualTo("createdAt", Date().addMonths(-1))
                FilterItem.FILTER_POPULAR -> {
                    query?.whereGreaterThanOrEqualTo("likes", 10)
                    query?.orderByDescending("likes")
                }
                FilterItem.FILTER_COMPLETED -> query?.whereMatchesKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
                FilterItem.FILTER_NOT_COMPLETED -> query?.whereDoesNotMatchKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
            }
        }

        ifViewAttached { it.showLoading(pullToRefresh) }

        query?.findInBackground { objects, e ->
            run {
                if (e !== null) {
                    if (e.code != ParseException.CACHE_MISS) {
                        ifViewAttached { it.showError(e, pullToRefresh) }
                    }
                } else {
                    if (category[0].key == FilterItem.CATEGORY_FEATURED) {
                        val result = mutableListOf<WorkoutType>()
                        objects.forEach { item ->
                            item.createdBy!!.rotationRank = item.createdBy!!.rotationRank ?: 9999
                            result.add(item)
                        }
                        val comparator = compareBy<WorkoutType>{ it.createdBy!!.rotationRank }.thenByDescending { it.createdAt }
                        objects.clear()
                        objects.addAll(result.sortedWith(comparator))

                        if (isFirstLoading) {
                            val featuredUsers = mutableListOf<User>()
                            var userIds = mutableListOf<String>()
                            objects.forEach { item ->
                                // add 1 of the most recent featuredUser
                                if (!userIds.contains(item.createdBy!!.objectId)) {

                                    featuredUsers.add(item.createdBy!!)
                                    userIds.add(item.createdBy!!.objectId)
                                }
                            }

                            ifViewAttached {
                                it.setFeaturedUsers(featuredUsers)
                            }
                        }
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