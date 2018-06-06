package com.liverowing.android.activity.settings

import android.os.Bundle
import com.liverowing.android.R
import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.support.v7.preference.*
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        /* Here we get a refererence of the SharedPreferences and of the PreferenceScreen */
        /* Then we can iterate through it and get a reference of all the preferences, and we call our own setSummaryMethod */
        val sharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen = preferenceScreen
        val count = prefScreen.preferenceCount

        for (i in 0 until count) {
            val p = prefScreen.getPreference(i)

            if (p is PreferenceGroup) {
                for (j in 0 until p.preferenceCount) {
                    val p2 = p.getPreference(j)
                    if (p2 !is CheckBoxPreference) {
                        /* We do NOT call the method if the preference at index i is an instance of CheckBoxPreference */
                        val value = sharedPreferences.getString(p2.key, "")
                        setPreferenceSummary(p2, value)
                    }
                }
            } else if (p !is CheckBoxPreference) {
                /* We do NOT call the method if the preference at index i is an instance of CheckBoxPreference */
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }

    override fun getCallbackFragment(): Fragment {
        Log.d("LiveRowing", "getCallbackFragment")
        return this
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        Log.d("LiveRowing", "onPreferenceTreeClick")
        return super.onPreferenceTreeClick(preference)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        Log.d("LiveRowing", "onDisplayPreferenceDialog")
        super.onDisplayPreferenceDialog(preference)
    }

    override fun onPreferenceStartScreen(preferenceFragmentCompat: PreferenceFragmentCompat, preferenceScreen: PreferenceScreen): Boolean {
        preferenceFragmentCompat.preferenceScreen = preferenceScreen
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key)

        /* For good practice, we always check first if the preference object is not null */
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""))
            }
        }
    }

    /* Our own helper method to set the summary of a preference */
    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()

        if (preference is ListPreference) {
            /* For list preferences, look up the correct display value in the preference's 'entries' list*/
            val prefIndex = preference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(preference.entries[prefIndex])
            }
        } else {
            /* For other preferences, set the summary to the value's simple string representation. */
            preference.summary = stringValue
        }
    }

    override fun onStop() {
        super.onStop()
        /* Unregister the preference change listener */
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        /* Register the preference change listener */
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
}
