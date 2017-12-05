package com.rhathe.monstertipper.models

import android.databinding.Bindable
import com.rhathe.monstertipper.BR
import java.math.BigDecimal
import java.util.*


class Tipper(name: String = "", val meal: Meal = Meal()): MoneyBase() {
	var bill: Bill = Bill()
	var onlyConsumed: Boolean = false
	var color = getColor()

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
		get() = meal.consumables.filter { it.isConsumedBy(this) } .toMutableList()

	var avoidedItems: MutableList<Consumable> = mutableListOf()
		get() = meal.consumables.filter { it.isAvoidedBy(this) } .toMutableList()

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
		val item = Consumable(Consumable.getName("consumed"), meal = meal)
		item.addAsConsumed(this)
		return item
	}

	fun addAvoidedItem(): Consumable {
		val item = Consumable(Consumable.getName("avoided"), meal = meal)
		item.addAsAvoided(this)
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
		bill.total = total
		return total
	}

	fun addToTotal(total: BigDecimal): BigDecimal {
		return calculateTotal(total + bill.total)
	}

	fun calculateTotalFromItemList(items: MutableList<Consumable>): BigDecimal {
		return items.map { it.bill.newValues.total }.fold(BigDecimal.ZERO){ x, y -> x + y }
	}

	companion object {
		var tipper_name_index = 0
		val random = Random()

		fun getName(): String {
			tipper_name_index += 1
			return "M-Tipper " + tipper_name_index
		}

		fun getColor(): Int {
			return 0xff000000.toInt() + 256 * 256 * getRandomHue() + 256 * getRandomHue() + getRandomHue()
		}

		fun getRandomHue(): Int {
			return (random.nextInt(256)/20) * 20
		}

		fun resetNames() {
			tipper_name_index = 0
		}
	}
}
