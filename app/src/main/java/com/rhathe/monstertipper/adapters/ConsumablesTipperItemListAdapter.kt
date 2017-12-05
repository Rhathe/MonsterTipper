package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.models.Tipper

class ConsumablesTipperItemListAdapter(tippers: MutableList<Tipper>, val consumable: Consumable, private val getter: (() -> MutableList<Tipper>)? = null) :
		BaseItemListAdapter(tippers as MutableList<Any>, R.layout.consumables_tipper_item, BR.tipper) {

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		//holder.setBinding(consumable, BR.consumable)
	}

	override fun getItemList(): MutableList<Any> {
		val list = {
			if (getter != null) getter.invoke()
			else if (consumable.hasAvoiders) consumable.avoiders
			else consumable.consumers
		}.invoke()
		return list as MutableList<Any>
	}

	fun removeFromList(item: Consumable, tipper: Tipper?) {
		this.notifyDataSetChanged()
	}
}