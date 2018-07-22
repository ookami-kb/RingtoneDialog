package com.example.ookami.ringtonedialog

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        findPreference("ringtone").connectRingtoneDialog(fragmentManager)
    }
}
