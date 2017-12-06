package com.rhathe.monstertipper.adapters

import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.ui.ConsumableDetailActivity
import kotlinx.android.synthetic.main.consumable_item.view.*


class ConsumableItemListAdapter(consumables: () -> MutableList<Consumable>, val tipper: Tipper?) :
		BaseItemListAdapter(
				consumables as () -> MutableList<Any>,
				R.layout.consumable_item,
				BR.consumable,
				activityClass = ConsumableDetailActivity::class.java) {

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		if (tipper != null) holder.setBinding(tipper, BR.tipper)

		val binding = this.binding as ViewDataBinding
		val v = binding.root.c_tipper_items
		val layoutManager = GridLayoutManager(binding.root.context, 5)
		v.layoutManager = layoutManager

		val consumable = items()[position] as Consumable
		v.adapter = ConsumablesTipperItemListAdapter(consumable::listOfTippers::get, consumable)
	}

	fun removeFromList(item: Consumable, tipper: Tipper?) {
		if (tipper == null) return
		item.removeTipper(tipper)
		item.removeSelf()
		this.notifyDataSetChanged()
	}
}
