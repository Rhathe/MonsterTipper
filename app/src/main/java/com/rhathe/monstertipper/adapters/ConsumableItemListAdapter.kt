package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.ui.ConsumableDetailActivity


class ConsumableItemListAdapter(consumables: MutableList<Consumable>, val tipper: Tipper?, private val getter: () -> MutableList<Consumable>) :
		BaseItemListAdapter(consumables as MutableList<Any>, R.layout.consumable_item, BR.consumable, activityClass = ConsumableDetailActivity::class.java) {

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		if (tipper != null) holder.setBinding(tipper, BR.tipper)
	}

	override fun getItemList(): MutableList<Any> {
		return getter() as MutableList<Any>
	}

	fun removeFromList(item: Consumable, tipper: Tipper?) {
		if (tipper == null) return
		item.removeTipper(tipper)
		item.removeSelf()
		this.notifyDataSetChanged()
	}
}
