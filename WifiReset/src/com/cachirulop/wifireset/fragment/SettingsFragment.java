package com.cachirulop.wifireset.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cachirulop.wifireset.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_settings);
    }
}
