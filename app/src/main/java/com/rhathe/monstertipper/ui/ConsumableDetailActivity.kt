package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

	fun delete(@Suppress("UNUSED_PARAMETER") v: View) {
		item?.deleteSelf()
		finish(v)
	}
}
