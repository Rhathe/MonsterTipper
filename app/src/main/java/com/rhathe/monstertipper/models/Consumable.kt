package com.rhathe.monstertipper.models

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.rhathe.monstertipper.BR

class Consumable(name: String = getName(), val meal: Meal = Meal()): MoneyBase() {
	val bill: Bill = Bill()
	val tippers: HashMap<Tipper, Boolean> = hashMapOf()

	init {
		meal.consumables.add(this)
	}

	@get:Bindable
	var name = name
		set(name) {
			field = name
			registry.notifyChange(this, BR.name)
		}

	@get:Bindable
	var hasAvoiders = false
		get() = avoiders.size > 0

	var consumers: MutableList<Tipper> = mutableListOf()
		get() = tippers.filter { it.value }.keys.toMutableList()

	var avoiders: MutableList<Tipper> = mutableListOf()
		get() = tippers.filter { !it.value }.keys.toMutableList()

	var listOfTippers: MutableList<Tipper> = mutableListOf()
		get() = if (hasAvoiders) avoiders else consumers

	fun calculateTotal(bill: Bill) {
		this.bill.matchTaxAndTip(bill)
		this.bill.calculateFromBase()
	}

	fun removeSelf() {
		if (tippers.size == 0) {
			meal.consumables.remove(this)
		}
	}

	fun removeTipper(tipper: Tipper) {
		tippers.remove(tipper)
	}

	fun isConsumedBy(tipper: Tipper): Boolean {
		return tippers[tipper] == true
	}

	fun isAvoidedBy(tipper: Tipper): Boolean {
		return tippers[tipper] == false
	}

	private fun addTipper(tipper: Tipper, type: Boolean) {
		tippers[tipper] = type
	}

	fun addAsConsumed(tipper: Tipper) {
		addTipper(tipper, true)
	}

	fun addAsAvoided(tipper: Tipper) {
		addTipper(tipper, false)
	}

	companion object {
	    var name_index = 0
		var consumed_index = 0
		var avoided_index = 0

		fun getName(type: String? = null): String {
			val (index, prefix) = when (type) {
				"consumed" -> Pair(this::consumed_index, "Eaten")
				"avoided" -> Pair(this::avoided_index, "Didn't Have")
				else -> Pair(this::name_index, "Food")
			}

			index.set(index.get() + 1)
			return "$prefix ${index.get()}"
		}
	}
}
