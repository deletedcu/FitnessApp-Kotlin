package com.liverowing.android.model.pm

data class FilterItem(
        val key: Int,
        val value: String
) {
    companion object {
        const val CATEGORY_FEATURED = 0
        const val CATEGORY_COMMUNITY = 1
        const val CATEGORY_RECENT = 2
        const val CATEGORY_MY_CUSTOM = 3
        const val CATEGORY_AFFILIATE = 4

        const val TYPE_SINGLE_DISTANCE = 1
        const val TYPE_SINGLE_TIME = 2
        const val TYPE_INTERVALS = 4

        const val SHOW_NEW = 0
        const val SHOW_POPULAR = 1
        const val SHOW_COMPLETED = 2
        const val SHOW_NOT_COMPLETED = 3

        const val TAG_POWER = 0
        const val TAG_CARDIO = 1
        const val TAG_CROSS_TRAINING = 2
        const val TAG_HIIT = 3
        const val TAG_SPEED = 4
        const val TAG_WEIGHT_LOSS = 5

        fun groupByItems(): List<FilterItem> {
            var list = mutableListOf<FilterItem>()

            list.add(FilterItem(CATEGORY_FEATURED, "FEATURED"))
            list.add(FilterItem(CATEGORY_COMMUNITY, "COMMUNITY"))
            list.add(FilterItem(CATEGORY_RECENT, "RECENT"))
            list.add(FilterItem(CATEGORY_MY_CUSTOM, "CUSTOM"))
            list.add(FilterItem(CATEGORY_AFFILIATE, "AFFILIATE"))

            return list
        }

        fun workoutTypeItems(): List<FilterItem> {
            var list = mutableListOf<FilterItem>()

            list.add(FilterItem(TYPE_SINGLE_DISTANCE, "DISTANCE"))
            list.add(FilterItem(TYPE_SINGLE_TIME, "TIME"))
            list.add(FilterItem(TYPE_INTERVALS, "INTERVALS"))

            return list
        }

        fun showOnlyItems(): List<FilterItem> {
            var list = mutableListOf<FilterItem>()

            list.add(FilterItem(SHOW_NEW, "NEW"))
            list.add(FilterItem(SHOW_POPULAR, "POPULAR"))
            list.add(FilterItem(SHOW_COMPLETED, "COMPLETED"))
            list.add(FilterItem(SHOW_NOT_COMPLETED, "NOT COMPLETED"))

            return list
        }

        fun tagItems(): List<FilterItem> {
            var list = mutableListOf<FilterItem>()

            list.add(FilterItem(TAG_POWER, "POWER"))
            list.add(FilterItem(TAG_CARDIO, "CARDIO"))
            list.add(FilterItem(TAG_CROSS_TRAINING, "CROSS TRAINING"))
            list.add(FilterItem(TAG_HIIT, "HITT"))
            list.add(FilterItem(TAG_SPEED, "SPEED"))
            list.add(FilterItem(TAG_WEIGHT_LOSS, "WEIGHT LOSS"))

            return list
        }

        fun defaultGroupByItem(): FilterItem {
            return FilterItem(CATEGORY_FEATURED, "FEATURED")
        }

    }
}