package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.*
import android.util.Log
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Bill(
			onTotalChange: ((HashMap<String, BigDecimal>) -> Unit)? = null
		): MoneyBase() {

	var onTotalChange = onTotalChange

	@get:Bindable
	var total: BigDecimal = BigDecimal.ZERO.setScale(2)
		set(total) {
			field = total.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.total)
			onTotalChange?.invoke(getChangedValues())
		}

	@get:Bindable
	var base: BigDecimal = BigDecimal.ZERO.setScale(2)
		set(base) {
			field = base.setScale(2, BigDecimal.ROUND_UP)
			registry.notifyChange(this, BR.base)
		}

	var tip: BigDecimal = BigDecimal(15).setScale(0)
	var tax: BigDecimal = BigDecimal(8.875).setScale(3)
	var tipOnTax: Boolean = false

	override fun onToBigDecimal(field: String, _n: BigDecimal?) {
		val fields = listOf("total", "base", "tax", "tip")
		fields.forEach({x -> if (x != currentField) fieldMap.remove(x)})

		val n = _n ?: BigDecimal.ZERO
		if (currentField == "base") calculateFromBase(n)
		else if (currentField == "total") calculateFromTotal(n)
		else if (currentField == "tip") calculateFromTip(n)
		else if (currentField == "tax") calculateFromTax(n)

	}

	fun getChangedValues(): HashMap<String, BigDecimal> {
		val map = hashMapOf<String, BigDecimal>()
		map["total"] = total
		map["base"] = base
		map["tip"] = tip
		map["tax"] = tax
		return map
	}

	fun calculateTotal(base: BigDecimal = this.base, tax: BigDecimal = this.tax, tip: BigDecimal = this.tip) {
		total = base * taxAndTipFactor(tax, tip)
	}

	fun calculateBase(total: BigDecimal = this.total, tax: BigDecimal = this.tax, tip: BigDecimal = this.tip) {
		base = total / taxAndTipFactor(tax, tip)
	}

	fun calculateFromBase(base: BigDecimal) {
		calculateTotal(base = base)
	}

	fun calculateFromTotal(total: BigDecimal) {
		calculateBase(total = total)
	}

	fun calculateFromTax(tax: BigDecimal) {
		calculateTotal(tax = tax)
	}

	fun calculateFromTip(tip: BigDecimal) {
		calculateTotal(tip = tip)
	}

	fun taxAndTipFactor(tax: BigDecimal = this.tax, tip: BigDecimal = this.tip): BigDecimal {
		val one = BigDecimal.ONE
		val hun = BigDecimal(100)
		if (tipOnTax) return (one + tip/hun) * (one + tax/hun)
		return one + (tip + tax)/hun
	}
}