package com.rhathe.monstertipper.models

import android.databinding.Bindable
import android.util.Log
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.services.NameService
import java.math.BigDecimal


class Tipper(name: String = ""): MoneyBase() {
	var name: String = if (name.isNotBlank()) name else NameService.instance?.getName() ?: ""
	var bill: Bill = Bill()
	var onlyConsumed: Boolean = false

	@get:Bindable
	var willPay: BigDecimal? = null
		set(willPay) {
			field = willPay?.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.willPay)
		}

	var maxPay: BigDecimal? = null
	var minPay: BigDecimal? = null

	var consumedItems: List<Item> = emptyList()
	var avoidedItems: List<Item> = emptyList()

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

	fun isOnlyConsumedEnabled(willPay: BigDecimal?): Boolean {
		return willPay == null
	}

	fun addConsumedItem() {

	}
}