package com.rhathe.monstertipper.ui

import android.content.Context
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
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class TipperDetailActivity : BaseActivity() {
	var tipper: Tipper? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.tipper_detail)
		tipper = CurrentService.getCurrent(Tipper::class.java) as Tipper? ?: Tipper()
		binding.setVariable(BR.tipper, tipper)

		setupRecyclerView((tipper as Tipper)::consumedItems::get, consumed_items)
		setupRecyclerView((tipper as Tipper)::avoidedItems::get, avoided_items)
	}

	override fun onResume() {
		consumed_items.adapter?.notifyDataSetChanged()
		avoided_items.adapter?.notifyDataSetChanged()
		super.onResume()
	}

	fun addConsumedItem(v: View? = null) {
		addItemDialog { item: Consumable? ->
			item?.addAsConsumed(tipper as Tipper)
			addItem(item ?: tipper?.addConsumedItem(), consumed_items)
		}
	}

	fun addAvoidedItem(v: View? = null) {
		addItemDialog { item: Consumable? ->
			item?.addAsAvoided(tipper as Tipper)
			addItem(item ?: tipper?.addAvoidedItem(), consumed_items)
		}
	}

	fun addItem(item: Consumable?, v: RecyclerView) {
		val adapter = v.adapter as ConsumableItemListAdapter?
		adapter?.notifyDataSetChanged()
		adapter?.goToItem(item as Any, applicationContext)
	}

	fun addItem(type: String) {
		if (type == "avoided") addAvoidedItem()
		else if (type == "consumed") addConsumedItem()
	}

	fun setupRecyclerView(getter: () -> MutableList<Consumable>, v: RecyclerView) {
		val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
		v.layoutManager = layoutManager
		val theTipper = tipper as Tipper
		v.adapter = ConsumableItemListAdapter(getter, theTipper)
	}

	fun addItemDialog(fn: (Consumable?) -> Unit) {
		val dialog = AlertDialog.Builder(this)
		val str_items = listOf("New") + (tipper?.meal?.consumables?.map { it.name } ?: listOf())
		val items = str_items.toTypedArray()
		dialog.setTitle("Add Or Share Food Item")
		dialog.setItems(items, { _, i: Int ->
			fn(tipper?.meal?.consumables?.getOrNull(i - 1))
		})

		val alert = dialog.create()
		alert.show()
	}
}
