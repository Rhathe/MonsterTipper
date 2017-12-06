package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.ui.TipperDetailActivity


class TipperItemListAdapter(tippers: () -> MutableList<Tipper>, val consumable: Consumable? = null) :
		BaseItemListAdapter(tippers as () -> MutableList<Any>, R.layout.tipper_item, BR.tipper, activityClass = TipperDetailActivity::class.java) {

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		holder.setBinding(true, BR.showRemove)
	}

	override fun removeFromList(item: Any) {
		consumable?.removeTipper(item as Tipper)
		super.removeFromList(item)
	}
}
