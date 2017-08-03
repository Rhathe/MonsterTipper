package com.rhathe.monstertipper.models

import android.support.v7.widget.RecyclerView
import android.view.View

class Consumable(var name: String = ""): MoneyBase() {
	var bill: Bill = Bill()

	fun calculateTotal(bill: Bill) {
		this.bill.matchTaxAndTip(bill)
		this.bill.calculateFromBase()
	}

	fun removeSelf(consumableItemsList: MutableList<Consumable>, v: View) {
		consumableItemsList.remove(this)
		(v as RecyclerView).adapter.notifyDataSetChanged()
	}
}
