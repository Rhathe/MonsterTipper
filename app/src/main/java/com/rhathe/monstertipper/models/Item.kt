package com.rhathe.monstertipper.models

class Item(var name: String = ""): MoneyBase() {
	var bill: Bill = Bill()

	fun calculateTotal(bill: Bill) {
		this.bill.matchTaxAndTip(bill)
		this.bill.calculateFromBase()
	}
}
