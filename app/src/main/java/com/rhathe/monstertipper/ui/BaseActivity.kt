package com.rhathe.monstertipper.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.graphics.Rect
import android.widget.EditText
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager


abstract class BaseActivity: AppCompatActivity() {
	fun finish(@Suppress("UNUSED_PARAMETER") v: View) {
		finish()
	}
}