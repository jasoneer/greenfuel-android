package com.isotix.nufuel;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import android.content.Intent;
import android.content.Context;

public class ManageActivity extends PreferenceActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_manage);
    }
}
