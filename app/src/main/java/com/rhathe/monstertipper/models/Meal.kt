package com.rhathe.monstertipper.models

import android.databinding.Bindable
import android.util.Log
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal(setAsCurrentMeal: Boolean = false): MoneyBase() {
	@get:Bindable
	val tippers = mutableListOf<Tipper>()
	val EMPTY_LIST = listOf<Tipper>()

	val TIPPER_MIN = 1
	val TIPPER_MAX = 20

	val onTotalChange: () -> Unit = {
		tippers.forEach { it.bill.newValues.total = BigDecimal.ZERO }
		val tippersGrouped = tippers.groupBy({ if (it.willPay != null) "staticPay" else "dynamicPay" })
		var remaining = bill.getTotal() - calculateWillPayers(tippersGrouped["staticPay"] ?: EMPTY_LIST)

		calculateSplit(tippersGrouped["dynamicPay"] ?: EMPTY_LIST, remaining)

		tippers.forEach({ tipper ->
			val total = tipper.bill.newValues.total
			tipper.bill.setTotal(tipper.bill.toBigDecimal("total", value=total.toString()) ?: BigDecimal.ZERO)
		})
	}

	val bill = Bill(onTotalChange = onTotalChange)

	init {
		currentMeal = if (setAsCurrentMeal) this else null
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
			val tipper = tippers.removeAt(index)
			registry.notifyChange(this, BR.tippers)
		} catch(_: Exception) {}
	}

	fun calculateWillPayers(tippers: List<Tipper>): BigDecimal {
		tippers.forEach { it.bill.newValues.total = it.willPay ?: BigDecimal.ZERO }
		return tippers.map { it.bill.newValues.total }.fold(BigDecimal.ZERO){ x, y -> x + y }
	}

	fun calculateSplit(tippers: List<Tipper>, remaining: BigDecimal) {
		val split = remaining / BigDecimal(if (tippers.size > 0) tippers.size else 1)
		tippers.forEach { it.bill.newValues.total = split }
	}

	companion object {
		var currentMeal: Meal? = null
	}
}