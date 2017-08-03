package com.rhathe.monstertipper.ui

import android.content.Context
import android.util.AttributeSet
import android.preference.EditTextPreference
import android.support.v7.app.AlertDialog


class EditTextWithValuePreference(val ctx: Context, attrs: AttributeSet): EditTextPreference(ctx, attrs) {
	override fun getSummary(): CharSequence {
		return String.format(super.getSummary().toString(), text)
	}

	init {
		this.setOnPreferenceChangeListener { preference, newValue ->
			try {
				java.math.BigDecimal(newValue.toString())
				true
			} catch(e: Exception) {
				val builder = AlertDialog.Builder(ctx)
				builder.setTitle("Invalid Input")
				builder.setMessage("Value must be a valid number")
				builder.setPositiveButton(android.R.string.ok, null)
				builder.show()
				false
			}
		}
	}
}