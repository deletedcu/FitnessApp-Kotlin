package com.liverowing.android.activity.workouttype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.R.id.*
import com.liverowing.android.adapter.DashboardWorkoutTypeAdapter
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.screenWidth
import com.liverowing.android.util.SimpleItemDecorator
import khronos.Dates
import khronos.Duration
import khronos.endOfDay
import khronos.minus
import kotlinx.android.synthetic.main.activity_workout_type_grid.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.lapism.searchview.Search
import com.lapism.searchview.Search.Version.MENU_ITEM
import com.lapism.searchview.database.SearchHistoryTable
import com.lapism.searchview.widget.SearchAdapter
import com.lapism.searchview.widget.SearchItem
import com.liverowing.android.LiveRowing
import com.liverowing.android.extensions.default
import kotlinx.android.synthetic.main.search_view.view.*


fun Context.WorkoutTypeGridIntent(workoutCategory: Int): Intent {
    return Intent(this, WorkoutTypeGridActivity::class.java).apply {
        putExtra(INTENT_WORKOUT_CATEGORY, workoutCategory)
    }
}

private const val INTENT_WORKOUT_CATEGORY = "workoutCategory"
const val WORKOUT_CATEGORY_FEATURED = 0
const val WORKOUT_CATEGORY_COMMUNITY = 1
const val WORKOUT_CATEGORY_RECENT_AND_LIKED = 2
const val WORKOUT_CATEGORY_CUSTOM = 3
const val WORKOUT_CATEGORY_AFFILIATE = 4

class WorkoutTypeGridActivity : AppCompatActivity() {
    private lateinit var hud: KProgressHUD
    private val workouts = mutableListOf<WorkoutType>()
    private val workoutTypes = mutableListOf<Int>()
    private val workoutTags = mutableListOf<Int>()
    private var currentTabIndex: Int = 0
    private var currentCategory: Int = 0
    private var currentCategoryText = ""
    private var currentQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_grid)
        setSupportActionBar(a_workout_type_grid_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        hud = KProgressHUD.create(this@WorkoutTypeGridActivity).default()

        val glide = Glide.with(this)
        val cardWidth = (screenWidth()) / 2
        val cardHeight = (cardWidth * 0.80).toInt()
        a_workout_type_grid_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
            adapter = DashboardWorkoutTypeAdapter(workouts, glide, cardWidth, cardHeight, { image, workoutType ->
                run {
                    EventBus.getDefault().postSticky(workoutType)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@WorkoutTypeGridActivity, image, "image")
                    this@WorkoutTypeGridActivity.startActivity(Intent(this@WorkoutTypeGridActivity, WorkoutTypeDetailActivity::class.java), options.toBundle())
                }
            })
            addItemDecoration(SimpleItemDecorator(15))
        }
        LinearSnapHelper().attachToRecyclerView(a_workout_type_grid_recyclerview)

        a_workout_type_grid_categories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("LiveRowing", "Item is selected! ${parent?.getItemAtPosition(position).toString()}")
                currentCategoryText = parent?.getItemAtPosition(position).toString()
                currentCategory = position

                invalidateOptionsMenu()
                workoutTypes.clear()
                a_workout_type_grid_tabbar.getTabAt(0)!!.select()
                runQueryAndPopulate()
            }
        }

        a_workout_type_grid_tabbar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab!!.position
                runQueryAndPopulate()
            }
        })

        val mHistoryDatabase = SearchHistoryTable(this)
        val searchAdapter = SearchAdapter(this)
        searchAdapter.setOnSearchItemClickListener { _, title, _ ->
            a_workout_type_grid_search.setQuery(title, true)

            val item = SearchItem(this@WorkoutTypeGridActivity)
            item.title = title

            mHistoryDatabase.addItem(item)
        }

        a_workout_type_grid_search.adapter = searchAdapter
        a_workout_type_grid_search.setOnOpenCloseListener(object : Search.OnOpenCloseListener {
            override fun onOpen() {
                a_workout_type_grid_search.apply {
                    setHint("Search ${currentCategoryText.toLowerCase()} workouts..")
                    setQuery(currentQuery, false)
                    showKeyboard()
                }
            }

            override fun onClose() {
                a_workout_type_grid_search.visibility = View.GONE
            }

        })

        a_workout_type_grid_search.setOnQueryTextListener(object : Search.OnQueryTextListener {
            override fun onQueryTextSubmit(query: CharSequence?): Boolean {
                val item = SearchItem(this@WorkoutTypeGridActivity)
                item.title = query

                mHistoryDatabase.addItem(item)

                currentQuery = query.toString()
                runQueryAndPopulate()

                a_workout_type_grid_search.close()
                return true
            }

            override fun onQueryTextChange(newText: CharSequence?) {}
        })

        val workoutCategory = intent.getIntExtra(INTENT_WORKOUT_CATEGORY, 0)
        a_workout_type_grid_categories.setSelection(workoutCategory, true)

    }

    private fun runQueryAndPopulate() {
        hud.show()
        val query = when (currentCategory) {
            WORKOUT_CATEGORY_FEATURED -> WorkoutType.featuredWorkouts()
            WORKOUT_CATEGORY_COMMUNITY -> WorkoutType.communityWorkouts()
            WORKOUT_CATEGORY_RECENT_AND_LIKED -> WorkoutType.recentAndLikedWorkouts()
            WORKOUT_CATEGORY_CUSTOM -> WorkoutType.myCustomWorkouts()
            WORKOUT_CATEGORY_AFFILIATE -> WorkoutType.affiliateWorkouts()
            else -> WorkoutType.featuredWorkouts()
        }

        if (workoutTypes.size > 0) query.whereContainedIn("valueType", workoutTypes)
        for (tag in workoutTags) {
            query.whereEqualTo("filterTags.$tag", 1)
        }

        when (currentTabIndex) {
            1 -> query.whereGreaterThanOrEqualTo("createdAt", Dates.today.minus(Duration(Calendar.MONTH, 1)).endOfDay)
            2 -> {
                query.whereGreaterThanOrEqualTo("likes", 10)
                query.orderByDescending("likes")
            }
            3 -> query.whereMatchesKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
            4 -> query.whereDoesNotMatchKeyInQuery("objectId", "workoutType.objectId", User.completedWorkouts())
        }

        if (currentQuery.isNotEmpty()) {
            query.whereMatches("name", currentQuery, "i")
        }

        query.findInBackground { objects, e ->
            if (e != null) {
                LiveRowing.globalParseExceptionHandler(this@WorkoutTypeGridActivity, e)
            } else {
                workouts.clear()
                workouts.addAll(objects)
                a_workout_type_grid_recyclerview.adapter.notifyDataSetChanged()
                hud.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_workouttype_grid, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val workoutTypesMap = hashMapOf(
                action_single_distance to 1,
                action_single_time to 2,
                action_intervals to 4
        )

        val tagsMap = hashMapOf(
                action_tag_power to 0,
                action_tag_cardio to 1,
                action_tag_cross_training to 2,
                action_tag_hiit to 3,
                action_tag_speed to 4,
                action_tag_weight_loss to 5
        )


        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            action_single_distance,
            action_single_time,
            action_intervals -> {
                val type = workoutTypesMap[item.itemId]!!
                if (item.isChecked) workoutTypes.remove(type) else workoutTypes.add(type)
                item.isChecked = !item.isChecked
                runQueryAndPopulate()
                return true
            }


            action_tag_hiit,
            action_tag_cardio,
            action_tag_cross_training,
            action_tag_power,
            action_tag_speed,
            action_tag_weight_loss -> {
                val tag = tagsMap[item.itemId]!!
                if (item.isChecked) workoutTags.remove(tag) else workoutTags.add(tag)
                item.isChecked = !item.isChecked
                runQueryAndPopulate()
                return true
            }

            app_bar_search -> {
                a_workout_type_grid_search.visibility = View.VISIBLE
                a_workout_type_grid_search.open(item)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
