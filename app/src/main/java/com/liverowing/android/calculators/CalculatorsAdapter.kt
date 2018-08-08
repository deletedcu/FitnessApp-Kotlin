package com.liverowing.android.calculators

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.calculators.pacecalculator.PaceCalculatorFragment
import com.liverowing.android.calculators.wattcalculator.WattCalculatorFragment
import androidx.viewpager.widget.PagerAdapter



class CalculatorsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PaceCalculatorFragment()
            1 -> WattCalculatorFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}