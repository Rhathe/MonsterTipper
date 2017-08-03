package com.rhathe.monstertipper.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.graphics.Rect
import android.widget.EditText
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager


abstract class BaseActivity: AppCompatActivity() {

	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			val v = currentFocus
			if (v is EditText) {
				val outRect = Rect()
				v.getGlobalVisibleRect(outRect)
				if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
					v.clearFocus()
					val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(v.windowToken, 0)
				}
			}
		}
		return super.dispatchTouchEvent(event)
	}

	fun finish(@Suppress("UNUSED_PARAMETER") v: View) {
		finish()
	}
}