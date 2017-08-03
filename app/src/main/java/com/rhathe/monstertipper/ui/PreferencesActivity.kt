package com.rhathe.monstertipper.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rhathe.monstertipper.R
import android.preference.PreferenceFragment


class PreferencesActivity : AppCompatActivity() {
	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.preferences)
		fragmentManager.beginTransaction().replace(R.id.content_frame, MyPreferenceFragment()).commit()
	}

	class MyPreferenceFragment : PreferenceFragment() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.preferences)
		}
	}
}

