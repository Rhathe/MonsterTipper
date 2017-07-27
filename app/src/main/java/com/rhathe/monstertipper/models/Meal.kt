package com.rhathe.monstertipper.models

import android.databinding.Bindable
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal(setAsCurrentMeal: Boolean = false): MoneyBase() {
	var onAddTippers: ((Tipper) -> Unit) = {}
	var onRemoveTippers: ((Tipper, Int) -> Unit) = { _, _ -> }

	@get:Bindable
	val tippers = mutableListOf<Tipper>()

	val TIPPER_MIN = 1
	val TIPPER_MAX = 20

	val onTotalChange: (HashMap<String, BigDecimal>) -> Unit = {
		tippers.forEach { it.bill.total = it.bill.toBigDecimal("total", value="0") ?: BigDecimal.ZERO }
		val tippersGrouped = tippers.groupBy({ it.isSpecial() })

		val remaining = bill.total / BigDecimal(tippers.size)
		tippersGrouped[false]?.forEach({ tipper ->
			tipper.bill.total = tipper.bill.toBigDecimal("total", value=remaining.toString()) ?: BigDecimal.ZERO
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
		onAddTippers(tipper)
	}

	fun removeTippers() {
		try {
			if (tippers.size <= TIPPER_MIN) return
			val index = tippers.size - 1
			val tipper = tippers.removeAt(index)
			registry.notifyChange(this, BR.tippers)
			onRemoveTippers(tipper, index)
		} catch(_: Exception) {}
	}

	companion object {
		var currentMeal: Meal? = null
	}
}