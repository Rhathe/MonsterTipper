package com.rhathe.monstertipper.ui

import android.content.Intent
import android.databinding.*
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
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.rhathe.monstertipper.models.Tipper
import android.databinding.BindingAdapter
import com.rhathe.monstertipper.adapters.ConsumableItemListAdapter


class MainActivity : BaseActivity() {
	var meal: Meal? = null
	var binding: ViewDataBinding? = null
	var listSwitch = ObservableInt(R.id.radio_tippers)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = DataBindingUtil.setContentView(this, R.layout.main)
		binding?.setVariable(BR.meal, meal)
		binding?.setVariable(BR.activity, this)
		binding?.setVariable(BR.listSwitch, listSwitch)

		setupTipperItemRecyclerView()
		setupConsumableItemRercyclerView()
		reset()
	}

	override fun onResume() {
		tipper_items.adapter.notifyDataSetChanged()
		consumable_items.adapter.notifyDataSetChanged()
		super.onResume()
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

	fun setupConsumableItemRercyclerView() {
		val layoutManager = GridLayoutManager(this, 2)
		consumable_items.layoutManager = layoutManager
	}

	fun onRadioListChanged(rg: RadioGroup, id: Int) {
		listSwitch.set(id)
	}

	fun showList(type: String, listSwitch: Int): Int {
		val included = when(type) {
			"tippers" -> listOf(R.id.radio_tippers)
			"consumables" -> listOf(R.id.radio_consumables)
			else -> listOf()
		}

		val tlayout = (tipper_items.layoutManager as GridLayoutManager)
		val clayout = (consumable_items.layoutManager as GridLayoutManager)
		when(listSwitch) {
			R.id.radio_tippers -> tlayout.spanCount = 2
			R.id.radio_consumables -> clayout.spanCount = 2
			else -> {
				tlayout.spanCount = 1
				clayout.spanCount = 1
			}
		}

		return if (listSwitch in (listOf(R.id.radio_both) + included)) View.VISIBLE else View.GONE
	}

	fun getListLayoutWeight(listSwitch: Int): Float {
		return when(listSwitch) {
			R.id.radio_both -> 1f
			else -> 2f
		}
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

		val tadapter = TipperItemListAdapter(meal::tippers::get)
		tipper_items.adapter = tadapter

		val cadapter = ConsumableItemListAdapter(meal::consumables::get, null)
		consumable_items.adapter = cadapter

		// Number of tippers needs to be updated
		tadapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() {
				meal.notifyTippersChanged()
				super.onChanged()
			}
		})

		return true
	}
}

@BindingAdapter("android:layout_weight")
fun setLayoutWeight(view: View, weight: Float) {
	(view.layoutParams as LinearLayout.LayoutParams).weight = weight
}
