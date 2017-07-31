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

	fun nullableChecked(field: String, isNullable: Boolean? = false, placeholderN: BigDecimal?, n: BigDecimal?): Boolean {
		return n != null
	}

	@InverseMethod("nullableChecked")
	fun enableNullable(field: String, isNullable: Boolean? = false, n: BigDecimal?, value: Boolean): BigDecimal? {
		if (isNullable == true) {
			val ret = if (value) BigDecimal.ZERO else null
			fieldMap[field] = ret?.toString() ?: ""
			return ret
		}
		return n
	}

	fun setCurrentFieldOnChange(s: String, change: Boolean) {
		if (change) currentField = s
		else if (currentField == s) currentField = ""
	}

	open fun onToBigDecimal(field: String, value: BigDecimal?) {}

	open fun isFieldEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		return true
	}

	open fun isCheckboxEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		return isFieldEnabled(field, isNullable, value) && isNullable == true
	}

	open fun isEditTextEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		return isFieldEnabled(field, isNullable, value) && (isNullable != true || value != null)
	}

	open fun getLabel(field: String): String {
		return field
	}

	@Ignore
	protected val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}

}