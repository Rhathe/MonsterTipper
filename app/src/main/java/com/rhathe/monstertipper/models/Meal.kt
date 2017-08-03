package com.rhathe.monstertipper.models

import android.databinding.Bindable
import android.util.Log
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal: MoneyBase() {
	val TIPPER_MIN = 0
	val TIPPER_MAX = 20

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
		remaining -= calculateItemsDifferenceTotal(tippersGrouped["dynamicPay"])

		// Split the remaining among the eligible tippers
		calculateSplit(tippersGrouped["dynamicPay"], remaining)

		// Notify data binding change for even split
		registry.notifyChange(this, BR.evenSplit)
	}

	val bill = Bill(onTotalChange = onTotalChange)

	@Bindable
	fun getEvenSplit(): BigDecimal {
		return bill.getTotal() / BigDecimal.ONE.max(BigDecimal(tippers.size))
	}

	fun addTippers() {
		if (tippers.size >= TIPPER_MAX) return
		val tipper = Tipper()
		tippers.add(tipper)
		registry.notifyChange(this, BR.tippers)
	}

	fun removeTippers() {
		try {
			if (tippers.size <= TIPPER_MIN) return
			val index = tippers.size - 1
			tippers.removeAt(index)
			registry.notifyChange(this, BR.tippers)
		} catch(_: Exception) {}
	}

	fun calculateWillPayersTotal(tippers: List<Tipper>?): BigDecimal {
		return calculateTippersValuesTotal(tippers, { it.bill.newValues.total })
	}

	fun calculateItemsDifferenceTotal(tippers: List<Tipper>?): BigDecimal {
		return calculateTippersValuesTotal(tippers, {
			val diff = it.calculateDifferenceFromItems()
			it.addToTotal(diff)
			diff
		})
	}

	fun calculateTippersValuesTotal(tippers: List<Tipper>?, fn: (Tipper) -> BigDecimal): BigDecimal {
		return tippers?.map(fn)?.fold(BigDecimal.ZERO){ x, y -> x + y } ?: BigDecimal.ZERO
	}

	fun calculateSplit(tippers: List<Tipper>?, remaining: BigDecimal) {
		val size = tippers?.size ?: 0
		val split = remaining / BigDecimal(if (size > 0) size else 1)
		var remaining = remaining
		tippers?.forEach {
			val totalAdd = if (remaining > split) split else remaining.max(BigDecimal.ZERO)
			it.addToTotal(totalAdd)
			remaining -= split
		}
	}
}