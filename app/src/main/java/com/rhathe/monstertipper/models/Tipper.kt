package com.rhathe.monstertipper.models


class Tipper {
	var name: String = "Random Tipper"
	var bill: Bill = Bill()

	var hadItems: List<Item> = emptyList()
	var avoidedItems: List<Item> = emptyList()
}