package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.*
import android.util.Log
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Bill: Observable {
	@get:Bindable
	var total: BigDecimal = BigDecimal.ZERO.setScale(2)
		set(total) {
			field = total.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.total)
		}

	var base: Int = 0
	var tip: Number = 0.0
	var tax: Number = 0.0
	var tipOnTax: Boolean = false
	val fieldMap = HashMap<String, String>()

	@Ignore
	private val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}

	fun toDollars(field: String, n: BigDecimal): String {
		return fieldMap[field] ?: n.toString()
	}

	@InverseMethod("toDollars")
	fun toBigDecimal(field: String, s: String): BigDecimal {
		fieldMap[field] = s
		return if (s.isNotBlank()) BigDecimal(s) else BigDecimal.ZERO
	}
}