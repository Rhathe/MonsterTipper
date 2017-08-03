package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.adapters.ConsumableItemListAdapter
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.services.CurrentService
import kotlinx.android.synthetic.main.tipper_detail.*


class TipperDetailActivity : AppCompatActivity() {
	var tipper: Tipper? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.tipper_detail)
		tipper = CurrentService.getCurrent(Tipper::class.java) as Tipper? ?: Tipper()
		binding.setVariable(BR.tipper, tipper)

		setupRecyclerView((tipper as Tipper).consumedItems, consumed_items)
		setupRecyclerView((tipper as Tipper).avoidedItems, avoided_items)
	}

	override fun onResume() {
		consumed_items.adapter?.notifyDataSetChanged()
		avoided_items.adapter?.notifyDataSetChanged()
		super.onResume()
	}

	fun addConsumedItem(v: View? = null) {
		addItem(tipper?.addConsumedItem(), consumed_items)
	}

	fun addAvoidedItem(v: View? = null) {
		addItem(tipper?.addAvoidedItem(), avoided_items)
	}

	fun removeBlahItem() {}

	fun addItem(item: Consumable?, v: RecyclerView) {
		val adapter = v.adapter as ConsumableItemListAdapter?
		adapter?.notifyDataSetChanged()
		adapter?.goToItem(item as Any, applicationContext)
	}

	fun setupRecyclerView(items: MutableList<Consumable>, v: RecyclerView) {
		val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
		v.layoutManager = layoutManager
		v.adapter = ConsumableItemListAdapter(items)
		v.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		v.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
	}

	fun finish(@Suppress("UNUSED_PARAMETER") v: View) {
		finish()
	}
}
