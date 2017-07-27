package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.Bindable
import java.util.*
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Tipper(name: String = ""): MoneyBase() {
	var name: String = if (name.isNotBlank()) name else UUID.randomUUID().toString().substring(0, 5)
	var bill: Bill = Bill()
	var onlyHad: Boolean = false

	@get:Bindable
	var willPay: BigDecimal? = null
		set(willPay) {
			field = willPay?.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.willPay)
		}

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
}