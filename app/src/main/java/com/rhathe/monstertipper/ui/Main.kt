package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.rhathe.monstertipper.adapters.TipperItemListAdapter
import com.rhathe.monstertipper.services.CurrentService
import kotlinx.android.synthetic.main.content_main.*


class Main : AppCompatActivity() {
	var meal = Meal()

	init {
		CurrentService.setAsCurrent(meal)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.main)
		binding.setVariable(BR.meal, meal)

		addTippers()
		setupTipperItemRecyclerView()
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

	fun addTippers(v: View? = null) {
		meal.addTippers()
		tipper_items.adapter?.notifyDataSetChanged()
	}

	fun removeTippers(v: View? = null) {
		meal.removeTippers()
		tipper_items.adapter?.notifyDataSetChanged()
	}

	fun setupTipperItemRecyclerView() {
		val layoutManager = GridLayoutManager(this, 2)
		tipper_items.layoutManager = layoutManager
		tipper_items.adapter = TipperItemListAdapter(meal.tippers)
		tipper_items.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		tipper_items.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
	}
}
