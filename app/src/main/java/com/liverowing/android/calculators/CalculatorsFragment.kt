package com.liverowing.android.calculators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import kotlinx.android.synthetic.main.fragment_calculators.*



class CalculatorsFragment : MvpFragment<CalculatorsView, CalculatorsPresenter>(), CalculatorsView {
    private lateinit var calculatorsAdapter: CalculatorsAdapter

    override fun createPresenter(): CalculatorsPresenter {
        return CalculatorsPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculatorsAdapter = CalculatorsAdapter(childFragmentManager)
        f_calculators_container.adapter = calculatorsAdapter
        f_calculators_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(f_calculators_tabs))
        f_calculators_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(f_calculators_container))
    }
}