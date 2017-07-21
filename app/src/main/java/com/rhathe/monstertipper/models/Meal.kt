package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.Bindable
import android.databinding.InverseMethod
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.util.Log
import android.view.View
import com.rhathe.monstertipper.BR
import java.math.BigDecimal

class Meal(setAsCurrentMeal: Boolean = false): Observable {
	var onAddTippers: ((Tipper) -> Unit) = {}
	var onRemoveTippers: ((Tipper, Int) -> Unit) = { _, _ -> }

	@get:Bindable
	val tippers = mutableListOf<Tipper>()

	val TIPPER_MIN = 1
	val TIPPER_MAX = 20

	val onTotalChange: (HashMap<String, BigDecimal>) -> Unit = {
		map: HashMap<String, BigDecimal> ->

		tippers.forEach { it.bill.total = it.bill.toBigDecimal("total", "0") }
		val tippersGrouped = tippers.groupBy({ it.isSpecial() })

		val remaining = bill.total / BigDecimal(tippers.size)
		tippersGrouped[false]?.forEach({ tipper ->
			tipper.bill.total = tipper.bill.toBigDecimal("total", remaining.toString())
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

	@Ignore
	private val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}

	companion object {
		var currentMeal: Meal? = null
	}
}