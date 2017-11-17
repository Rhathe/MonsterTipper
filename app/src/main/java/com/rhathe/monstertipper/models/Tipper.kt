package com.rhathe.monstertipper.models

import android.databinding.Bindable
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Tipper(name: String = "", meal: Meal = Meal()): MoneyBase() {
	var bill: Bill = Bill()
	var onlyConsumed: Boolean = false

	@get:Bindable
	var name: String = if (name.isNotBlank()) name else getName()
		set(name) {
			field = name
			registry.notifyChange(this, BR.name)
		}

	@get:Bindable
	var willPay: BigDecimal? = null
		set(willPay) {
			field = willPay?.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.willPay)
			registry.notifyChange(this, BR.consumedItemsEnabled)
			registry.notifyChange(this, BR.avoidedItemsEnabled)
		}

	var maxPay: BigDecimal? = null
	var minPay: BigDecimal? = null

	var consumedItems: MutableList<Consumable> = mutableListOf()
	var avoidedItems: MutableList<Consumable> = mutableListOf()

	fun isSpecial(): Boolean {
		return maxPay != null
			|| minPay != null
			|| willPay != null
			|| onlyConsumed
			|| consumedItems.isNotEmpty()
			|| avoidedItems.isNotEmpty()
	}

	override fun isFieldEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		when(field) {
			"willPay" -> {
				if (onlyConsumed) return false
			}
		}
		return super.isFieldEnabled(field, isNullable, value)
	}

	override fun getLabel(field: String): String {
		if (field == "willPay") return "Will Only Pay"
		return super.getLabel(field)
	}

	@Bindable
	fun getConsumedItemsEnabled(): Boolean {
		return willPay == null
	}

	@Bindable
	fun getAvoidedItemsEnabled(): Boolean {
		return getConsumedItemsEnabled() && !onlyConsumed
	}

	fun addConsumedItem(): Consumable {
		val item = Consumable("Eaten " + (consumedItems.size + 1))
		consumedItems.add(item)
		return item
	}

	fun addAvoidedItem(): Consumable {
		val item = Consumable("Didn't Have " + (avoidedItems.size + 1))
		avoidedItems.add(item)
		return item
	}

	fun matchMealTaxAndTip(meal: Meal) {
		bill.matchTaxAndTip(meal.bill)
		consumedItems.forEach { it.calculateTotal(bill) }
		avoidedItems.forEach { it.calculateTotal(bill) }
	}

	fun calculateTotalIfWillPay(): BigDecimal? {
		if (willPay != null) return calculateTotal(willPay as BigDecimal)
		return willPay
	}

	fun calculateTotalIfOnlyPayForConsumed(): BigDecimal? {
		if (onlyConsumed) return calculateTotal(calculateTotalFromItemList(consumedItems))
		return null
	}

	fun calculateTotal(total: BigDecimal): BigDecimal {
		bill.calculateOtherFields("total", total)
		bill.total = (total)
		return total
	}

	fun addToTotal(total: BigDecimal): BigDecimal {
		return calculateTotal(total + bill.total)
	}

	fun calculateDifferenceFromExtras(): BigDecimal {
		return calculateTotalFromItemList(consumedItems)
	}

	fun calculateDifferenceFromAvoided(): BigDecimal {
		return calculateTotalFromItemList(avoidedItems)
	}

	fun calculateTotalFromItemList(items: MutableList<Consumable>): BigDecimal {
		return items.map { it.bill.newValues.total }.fold(BigDecimal.ZERO){ x, y -> x + y }
	}

	companion object {
		var tipper_name_index = 0

		fun getName(): String {
			tipper_name_index += 1
			return "M-Tipper " + tipper_name_index
		}

		fun resetNames() {
			tipper_name_index = 0
		}
	}
}
