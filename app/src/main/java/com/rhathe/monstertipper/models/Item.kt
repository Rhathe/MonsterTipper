package com.rhathe.monstertipper.models

class Item: MoneyBase() {
	var name: String = ""
	var bill: Bill = Bill()
}
