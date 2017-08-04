package com.rhathe.monstertipper.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.rhathe.monstertipper.adapters.TipperItemListAdapter
import com.rhathe.monstertipper.services.CurrentService
import kotlinx.android.synthetic.main.content_main.*
import java.math.BigDecimal
import android.support.v7.widget.RecyclerView
import com.rhathe.monstertipper.models.Tipper


class Main : BaseActivity() {
	var meal: Meal? = null
	var binding: ViewDataBinding? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = DataBindingUtil.setContentView(this, R.layout.main)
		binding?.setVariable(BR.meal, meal)
		setupTipperItemRecyclerView()
		reset()
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
			R.id.action_reset -> { reset() }
			R.id.action_settings -> {
				startActivity(Intent(this, PreferencesActivity::class.java))
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun addTippers(v: View? = null) {
		meal?.addTippers()
		tipper_items.adapter?.notifyDataSetChanged()
	}

	fun removeTippers(v: View? = null) {
		meal?.removeTippers()
		tipper_items.adapter?.notifyDataSetChanged()
	}

	fun setupTipperItemRecyclerView() {
		val layoutManager = GridLayoutManager(this, 2)
		tipper_items.layoutManager = layoutManager
	}

	fun reset(): Boolean {
		val pref = PreferenceManager.getDefaultSharedPreferences(this)
		val meal = Meal(
			tax = BigDecimal(pref.getString("tax", "8.875")),
			tip = BigDecimal(pref.getString("tip", "15"))
		)

		this.meal?.destroy()
		this.meal = meal

		CurrentService.reset()
		Tipper.resetNames()
		CurrentService.setAsCurrent(meal)
		binding?.setVariable(BR.meal, meal)
		val adapter = TipperItemListAdapter(meal.tippers)
		tipper_items.adapter = adapter

		// Number of tippers needs to be updated
		adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() {
				meal.notifyTippersChanged()
				super.onChanged()
			}
		})

		return true
	}
}
