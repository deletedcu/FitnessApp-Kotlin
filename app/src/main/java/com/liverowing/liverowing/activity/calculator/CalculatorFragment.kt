package com.liverowing.liverowing.activity.calculator

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.liverowing.liverowing.R
import kotlinx.android.synthetic.main.fragment_calculator.*

class CalculatorFragment : Fragment() {
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSectionsPagerAdapter = SectionsPagerAdapter(activity!!.supportFragmentManager)
        f_calculator_container.adapter = mSectionsPagerAdapter
        f_calculator_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(f_calculator_tabs))
        f_calculator_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(f_calculator_container))
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PaceCalculatorFragment()
                1 -> WeightAdjustmentCalculatorFragment()
                2 -> WattCalculatorFragment()
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
