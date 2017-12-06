package com.rhathe.monstertipper.ui

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.adapters.ConsumableItemListAdapter
import com.rhathe.monstertipper.adapters.TipperItemListAdapter
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.services.CurrentService
import kotlinx.android.synthetic.main.consumable_detail.*

class ConsumableDetailActivity : BaseActivity() {
	var tipper: Tipper? = null
	var item: Consumable? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.consumable_detail)
		tipper = CurrentService.getCurrent(Tipper::class.java) as Tipper? ?: Tipper()
		item = CurrentService.getCurrent(Consumable::class.java) as Consumable? ?: Consumable()
		binding.setVariable(BR.consumable, item)

		val consumable = item as Consumable
		setupRecyclerView(consumable::consumers::get, consumers_list_view)
		setupRecyclerView(consumable::avoiders::get, avoiders_list_view)
	}

	fun setupRecyclerView(getter: () -> MutableList<Tipper>, v: RecyclerView) {
		val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
		v.layoutManager = layoutManager
		val consumable = item as Consumable
		v.adapter = TipperItemListAdapter(getter, consumable)
	}

	fun addTipperDialog(type: String) {
		val dialog = AlertDialog.Builder(this)
		val _item = item as Consumable
		val (adder, existing_tippers) = when(type) {
			"consumer" -> Pair(_item::addAsConsumed, _item.consumers)
			"avoider" -> Pair(_item::addAsAvoided, _item.avoiders)
			else -> Pair(null, null)
		}

		val tippers = tipper?.meal?.tippers?.minus(existing_tippers ?: listOf()) as List<Tipper>
		val str_items = tippers.map { it.name }
		val items = str_items.toTypedArray()
		dialog.setTitle("Add Tipper To Food")
		dialog.setItems(items, { _, i: Int ->
			val tipper = tippers[i]
			(adder as ((Tipper) -> Unit)?)?.invoke(tipper)

			consumers_list_view.adapter.notifyDataSetChanged()
			avoiders_list_view.adapter.notifyDataSetChanged()
		})

		val alert = dialog.create()
		alert.show()
	}

	fun addConsumerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
		addTipperDialog("consumer")
	}

	fun addAvoiderDialog(@Suppress("UNUSED_PARAMETER") v: View) {
		addTipperDialog("avoider")
	}

	fun delete(@Suppress("UNUSED_PARAMETER") v: View) {
		item?.deleteSelf()
		finish(v)
	}

	fun save(@Suppress("UNUSED_PARAMETER") v: View) {
		item?.addToMeal()
		finish(v)
	}
}
