package com.rhathe.monstertipper.models

import android.databinding.Bindable
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal(tax: BigDecimal? = null, tip: BigDecimal? = null): MoneyBase() {
	val TIPPER_MIN = 0
	val TIPPER_MAX = 100

	@get:Bindable
	val tippers = mutableListOf<Tipper>()

	val onTotalChange: () -> Unit = {
		// Initialize each tipper to zero
		tippers.forEach {
			it.bill.resetBaseTotal()
			it.matchMealTaxAndTip(this)
		}

		// Group by staticPayers and dynamicPayers
		val tippersGrouped = tippers.groupBy {
			val nullCheck = it.calculateTotalIfWillPay() ?: it.calculateTotalIfOnlyPayForConsumed()
			if (nullCheck == null) "dynamicPay" else "staticPay"
		}

		// Calculate remaining from tip and items values
		var remaining = bill.getTotal() - calculateWillPayersTotal(tippersGrouped["staticPay"])
		remaining -= calculateDifferenceFromExtrasTotal(tippersGrouped["dynamicPay"])

		// Split the remaining among the eligible tippers
		calculateSplit(tippersGrouped["dynamicPay"], remaining)

		// Redistribute if tippers avoided items
		redistributeOnAvoidedItems(tippersGrouped["dynamicPay"])

		// Notify data binding change for even split
		registry.notifyChange(this, BR.evenSplit)
	}

	val bill = Bill(tax = tax, tip = tip, onTotalChange = onTotalChange)

	@Bindable
	fun getEvenSplit(): BigDecimal {
		return bill.getTotal() / BigDecimal.ONE.max(BigDecimal(tippers.size))
	}

	fun notifyTippersChanged() {
		registry.notifyChange(this, BR.tippers)
	}

	fun addTippers() {
		if (tippers.size >= TIPPER_MAX) return
		val tipper = Tipper()
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

	fun calculateDifferenceFromExtrasTotal(tippers: List<Tipper>?): BigDecimal {
		return calculateTippersValuesTotal(tippers, {
			val diff = it.calculateDifferenceFromExtras()
			it.addToTotal(diff)
			diff
		})
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

	fun redistributeOnAvoidedItems(tippers: List<Tipper>?) {
		if (tippers?.size?.compareTo(1) == 1) {
			tippers?.forEach {
				val tipper = it
				val avoided = it.calculateDifferenceFromAvoided()
				if (avoided.compareTo(BigDecimal.ZERO) == 1) {
					val split = avoided / BigDecimal(tippers.size).setScale(2)
					tipper.addToTotal(-split)
					val newTippers = tippers.filter { it != tipper }
					distributeToTippers(newTippers, split)
				}
			}
		}
	}
}