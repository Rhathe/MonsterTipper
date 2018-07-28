package com.rhathe.monstertipper.models

import android.databinding.Bindable
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal(tax: BigDecimal? = null, tip: BigDecimal? = null): MoneyBase() {
	val TIPPER_MIN = 0
	val TIPPER_MAX = 100

	@get:Bindable
	val tippers = mutableListOf<Tipper>()

	val consumables = mutableListOf<Consumable>()

	val onTotalChange: () -> Unit = {
		// Initialize each tipper to zero
		tippers.forEach { it.bill.resetBaseTotal() }

		// Calculate distribution of items
		var itemPay = redistributeOnItems(tippers, consumables)

		// Group by willPay, onlyConsumed, and remaining
		val tippersGrouped = tippers.groupBy {
			when {
				it.willPay != null -> "willPay"
				it.onlyConsumed -> "onlyConsumed"
				else -> "remaining"
			}
		}

		// Remove willPayers itemPay, recalculate willPayers totals
		itemPay -= calculateWillPayersTotal(tippersGrouped["willPay"])
		tippersGrouped["willPay"]?.forEach { it.calculateTotalIfWillPay() }

		// subtract from total the itemPAy and the new willPayers total
		var remaining = bill.total - itemPay - calculateWillPayersTotal(tippersGrouped["willPay"])

		remaining = remaining.max(BigDecimal.ZERO)

		// Split the remaining among the eligible tippers
		calculateSplit(tippersGrouped["remaining"], remaining)

		// Calculate tippers' other fields from total
		tippers.forEach {
			it.bill.base = BigDecimal.ZERO
			it.matchMealTaxAndTip(this)
			it.bill.calculateOtherFields("total", it.bill.total, listOf("tip"))
		}

		// Notify data binding change for even split
		registry.notifyChange(this, BR.evenSplit)
	}

	val bill = Bill(tax = tax, tip = tip, onTotalChange = onTotalChange, dynamicRecalculation = true)

	@Bindable
	fun getEvenSplit(): BigDecimal {
		return bill.total / BigDecimal.ONE.max(BigDecimal(tippers.size))
	}

	fun notifyTippersChanged() {
		registry.notifyChange(this, BR.tippers)
	}

	fun addTippers() {
		if (tippers.size >= TIPPER_MAX) return
		val tipper = Tipper(meal=this)
		tippers.add(tipper)
		notifyTippersChanged()
	}

	fun removeTippers() {
		try {
			if (tippers.size <= TIPPER_MIN) return
			val index = tippers.size - 1
			tippers.removeAt(index)
			notifyTippersChanged()
		} catch(_: Exception) {}
	}

	fun calculateWillPayersTotal(tippers: List<Tipper>?): BigDecimal {
		return calculateTippersValuesTotal(tippers, { it.bill.newValues.total })
	}

	fun calculateTippersValuesTotal(tippers: List<Tipper>?, fn: (Tipper) -> BigDecimal): BigDecimal {
		return tippers?.map(fn)?.fold(BigDecimal.ZERO){ x, y -> x + y } ?: BigDecimal.ZERO
	}

	fun distributeToTippers(tippers: List<Tipper>?, total: BigDecimal) {
		val size = tippers?.size ?: 0
		val split = total / BigDecimal(if (size > 0) size else 1)
		var remaining = total
		tippers?.forEach {
			val totalAdd = if (remaining > split) split else remaining.max(BigDecimal.ZERO)
			it.addToTotal(totalAdd)
			remaining -= split
		}
	}

	fun calculateSplit(tippers: List<Tipper>?, remaining: BigDecimal) {
		distributeToTippers(tippers, remaining)
	}

	fun redistributeOnItems(tippers: List<Tipper>, consumables: List<Consumable>): BigDecimal {
		var total = BigDecimal.ZERO
		consumables.forEach {
			if (it.tippers.isEmpty()) return@forEach
			val newTippers = getTippersOnConsumable(it, tippers)

			val ctotal = it.bill.newValues.total
			total += ctotal
			distributeToTippers(newTippers, ctotal)
		}
		return total
	}

	fun getTippersOnConsumable(consumable: Consumable, tippers: List<Tipper>): List<Tipper> {
		// First see if we have a list of tippers that had these consumables
		var newTippers = consumable.tippers.filterValues { it }.keys.toList()

		// Otherwise filter out avoided tippers
		if (newTippers.isEmpty())
			newTippers = tippers.filter { consumable.tippers.getOrElse(it, {true}) != false}
		return newTippers.filter { tippers.contains(it) }
	}

	fun destroy() {
		bill.destroy()
	}
}
