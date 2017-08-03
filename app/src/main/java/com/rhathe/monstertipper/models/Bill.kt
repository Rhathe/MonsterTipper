package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.*
import android.util.Log
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Bill(
		tax: BigDecimal? = null,
		tip: BigDecimal? = null,
		onTotalChange: (() -> Unit)? = null): MoneyBase() {

	var onTotalChange = onTotalChange
	var storedValues = Values(tax=tax?:DEFAULT_TAX, tip=tip?:DEFAULT_TIP)
	var newValues = storedValues.copy()

	fun validateNewValues() {
		storedValues = newValues.copy()
	}

	@Bindable
	fun getTotal(): BigDecimal { return newValues.total }

	fun setTotal(total: BigDecimal) {
		newValues.total = total.setScale(2, BigDecimal.ROUND_UP)

		try {
			onTotalChange?.invoke()
			validateNewValues()
		} catch(e: Exception) {
			newValues = storedValues.copy()
		}
		registry.notifyChange(this, BR.total)
	}

	@Bindable
	fun getBase(): BigDecimal { return newValues.base }

	fun setBase(base: BigDecimal) {
		newValues.base = base.setScale(2, BigDecimal.ROUND_UP)
		validateNewValues()
		registry.notifyChange(this, BR.base)
	}

	@Bindable
	fun getTax(): BigDecimal { return newValues.tax }

	fun setTax(tax: BigDecimal) {
		newValues.tax = tax
		validateNewValues()
		registry.notifyChange(this, BR.tax)
	}

	@Bindable
	fun getTip(): BigDecimal { return newValues.tip }

	fun setTip(tip: BigDecimal) {
		newValues.tip = tip
		validateNewValues()
		registry.notifyChange(this, BR.tip)
	}

	var tipOnTax: Boolean = false

	override fun onToBigDecimal(field: String, value: BigDecimal?) {
		calculateOtherFields(field, value)
	}

	fun calculateOtherFields(field: String, _n: BigDecimal?) {
		val fields = listOf("total", "base", "tax", "tip")
		fields.forEach({x -> if (x != currentField) fieldMap.remove(x)})

		val n = _n ?: BigDecimal.ZERO
		if (currentField == "base") calculateFromBase(n)
		else if (currentField == "total") calculateFromTotal(n)
		else if (currentField == "tip") calculateFromTip(n)
		else if (currentField == "tax") calculateFromTax(n)
	}

	fun calculateTotal(base: BigDecimal = getBase(), tax: BigDecimal = getTax(), tip: BigDecimal = getTip()) {
		setTotal(base * taxAndTipFactor(tax, tip))
	}

	fun calculateBase(total: BigDecimal = getTotal(), tax: BigDecimal = getTax(), tip: BigDecimal = getTip()) {
		setBase(total / taxAndTipFactor(tax, tip))
	}

	fun calculateFromBase(base: BigDecimal = getBase()) {
		calculateTotal(base = base)
	}

	fun calculateFromTotal(total: BigDecimal = getTotal()) {
		calculateBase(total = total)
	}

	fun calculateFromTax(tax: BigDecimal = getTax()) {
		calculateTotal(tax = tax)
	}

	fun calculateFromTip(tip: BigDecimal = getTip()) {
		calculateTotal(tip = tip)
	}

	fun taxAndTipFactor(tax: BigDecimal = getTax(), tip: BigDecimal = getTip()): BigDecimal {
		val one = BigDecimal.ONE
		val hun = BigDecimal(100)
		if (tipOnTax) return (one + tip/hun) * (one + tax/hun)
		return one + (tip + tax)/hun
	}

	fun resetBaseTotal() {
		newValues.total = BigDecimal.ZERO
		newValues.base = BigDecimal.ZERO
	}

	fun matchTaxAndTip(bill: Bill) {
		this.setTax(bill.getTax())
		this.setTip(bill.getTip())
	}

	override fun isFieldEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		when(field) {
			"total" -> {
				if (getBase().setScale(2) == BigDecimal.ZERO.setScale(2)) return false
			}
		}
		return true
	}

	override fun getLabel(field: String): String {
		when(field) {
			"total" -> return "Total ($)"
			"base" -> return "Base ($)"
			"tax" -> return "Tax (%)"
			"tip" -> return "Tip (%)"
		}
		return super.getLabel(field)
	}

	data class Values(
		var base: BigDecimal = BigDecimal.ZERO.setScale(2),
		var total: BigDecimal = BigDecimal.ZERO.setScale(2),
		var tax: BigDecimal = DEFAULT_TAX,
		var tip: BigDecimal = DEFAULT_TIP
	)

	companion object {
		val DEFAULT_TAX: BigDecimal = BigDecimal(8.875).setScale(3)
		val DEFAULT_TIP: BigDecimal = BigDecimal(15).setScale(0)
	}
}