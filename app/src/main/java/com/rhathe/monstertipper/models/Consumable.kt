package com.rhathe.monstertipper.models

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.rhathe.monstertipper.BR

class Consumable(name: String = ""): MoneyBase() {
	var bill: Bill = Bill()

	@get:Bindable
	var name = name
		set(name) {
			field = name
			registry.notifyChange(this, BR.name)
		}

	fun calculateTotal(bill: Bill) {
		this.bill.matchTaxAndTip(bill)
		this.bill.calculateFromBase()
	}

	fun removeSelf(consumableItemsList: MutableList<Consumable>, v: View) {
		consumableItemsList.remove(this)
		(v as RecyclerView).adapter.notifyDataSetChanged()
	}
}
