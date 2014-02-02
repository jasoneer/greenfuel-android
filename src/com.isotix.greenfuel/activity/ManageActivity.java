package com.isotix.greenfuel.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.isotix.greenfuel.R;

public class ManageActivity extends PreferenceActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_manage);
    }
}
