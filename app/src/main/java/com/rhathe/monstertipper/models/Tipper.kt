package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import java.util.*
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import java.math.BigDecimal


class Tipper(name: String = ""): Observable {
	var name: String = if (name.isNotBlank()) name else UUID.randomUUID().toString().substring(0, 5)
	var bill: Bill = Bill()
	var onlyHad: Boolean = false
	var willPay: BigDecimal? = null
	var maxPay: BigDecimal? = null
	var minPay: BigDecimal? = null

	var hadItems: List<Item> = emptyList()
	var avoidedItems: List<Item> = emptyList()

	fun isSpecial(): Boolean {
		return maxPay != null
			|| minPay != null
			|| willPay != null
			|| onlyHad
			|| hadItems.isNotEmpty()
			|| avoidedItems.isNotEmpty()
	}

	@Ignore
	private val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}
}