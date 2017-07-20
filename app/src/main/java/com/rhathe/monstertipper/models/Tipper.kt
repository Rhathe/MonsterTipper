package com.rhathe.monstertipper.models

import java.util.*


class Tipper(name: String = "") {
	var name: String = if (name.isNotBlank()) name else UUID.randomUUID().toString().substring(5)
	var bill: Bill = Bill()

	var hadItems: List<Item> = emptyList()
	var avoidedItems: List<Item> = emptyList()
}