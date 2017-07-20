package com.rhathe.monstertipper.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import com.rhathe.monstertipper.models.Tipper
import java.util.*


class Main : AppCompatActivity() {
	var meal = Meal(setAsCurrentMeal = true)
	val tipperLayoutMap = mutableMapOf<Tipper, View>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.main)
		binding.setVariable(BR.meal, meal)

		setTipperButtons()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun setTipperButtons() {
		meal.onAddTippers = { x -> onAddTippers(x) }
		meal.onRemoveTippers = { x, y -> onRemoveTippers(x, y) }
		meal.addTippers()
	}

	fun onAddTippers(tipper: Tipper) {
		val inflater = LayoutInflater.from(applicationContext)
		val grid = findViewById<View>(R.id.tipper_grid) as GridLayout

		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.tipper_item, grid, false)
		binding.setVariable(BR.tipper, tipper)

		val layout = binding.root as LinearLayout
		layout.setOnClickListener { _ -> goToTipper(tipper) }

		tipperLayoutMap[tipper] = layout
		grid.addView(layout)
	}

	fun onRemoveTippers(tipper: Tipper, index: Int) {
		val grid = findViewById<View>(R.id.tipper_grid) as GridLayout
		grid.removeView(tipperLayoutMap[tipper])
	}

	fun goToTipper(tipper: Tipper) {
		val intent = Intent(this, TipperDetailActivity::class.java)
		val tipperIndex = meal.tippers.indexOf(tipper)
		intent.putExtra("tipperIndex", tipperIndex)
		startActivity(intent)
	}
}
