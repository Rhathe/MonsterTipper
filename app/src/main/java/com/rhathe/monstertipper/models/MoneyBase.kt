package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.InverseMethod
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.util.Log
import java.math.BigDecimal


open class MoneyBase: Observable {
	val fieldMap = HashMap<String, String>()
	var currentField: String = ""

	fun toDollars(field: String, isNullable: Boolean? = false, n: BigDecimal?): String {
		return fieldMap[field] ?: n?.toString() ?: ""
	}

	@InverseMethod("toDollars")
	fun toBigDecimal(field: String, _isNullable: Boolean? = false, value: String): BigDecimal? {
		fieldMap[field] = value

		val isNullable = _isNullable ?: false
		var ret = if (isNullable) null else BigDecimal.ZERO
		try {
			ret = BigDecimal(value)
		} catch(e: Exception) {
		}

		onToBigDecimal(field, ret)
		return ret
	}

	fun setCurrentFieldOnChange(s: String, change: Boolean) {
		if (change) currentField = s
		else if (currentField == s) currentField = ""
	}

	open fun onToBigDecimal(field: String, value: BigDecimal?) {}

	@Ignore
	protected val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}

}