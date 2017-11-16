package com.rhathe.monstertipper.models

import android.databinding.*
import com.rhathe.monstertipper.BR
import java.math.BigDecimal
import android.util.Log


class Bill(
		tax: BigDecimal? = null,
		tip: BigDecimal? = null,
		var onTotalChange: (() -> Unit)? = null,
		dynamicRecalculation: Boolean = false): MoneyBase() {

	private val ONE: BigDecimal = BigDecimal.ONE
	private val HUN: BigDecimal = BigDecimal(100)

	private val fields = mapOf(
			"total" to Field(
				label = "Total ($)",
				register = BR.total,
				calculateFrom = this::calculateFromTotal,
				calculateFrom0 = {calculateFromTotal()},
				isEnabled = { base.compareTo(BigDecimal.ZERO) != 0 },
				notifyList = listOf(BR.total)
			), "base" to Field(
				label = "Base ($)",
				register = BR.base,
				calculateFrom = this::calculateFromBase,
				calculateFrom0 = {calculateFromBase()},
				notifyList = listOf(BR.base, BR.baseWithTax, BR.tipInDollars)
			), "tax" to Field(
				label = "Tax (%)",
				register = BR.tax,
				calculateFrom = this::calculateFromTax,
				calculateFrom0 = {calculateFromTax()},
				notifyList = listOf(BR.tax)
			), "tip" to Field(
				label = "Tip (%)",
				register = BR.tip,
				calculateFrom = this::calculateFromTip,
				calculateFrom0 = {calculateFromTip()},
				notifyList = listOf(BR.tip, BR.tipInDollars)
			), "tipInDollars" to Field(
				label = "Tip In Dollars ($)",
				register = BR.tipInDollars,
				rootField = "tip",
				isEnabled = { base.compareTo(BigDecimal.ZERO) != 0 }
			), "baseWithTax" to Field(
				label = "Base With Tax ($)",
				register = BR.baseWithTax,
				rootField = "base"
			)
	)

	var storedValues = Values(tax=tax?:DEFAULT_TAX, tip=tip?:DEFAULT_TIP)
	var newValues = storedValues.copy()

	private val calculateOthersCallback = object: Observable.OnPropertyChangedCallback() {
		override fun onPropertyChanged(p0: Observable?, p1: Int) {
			var field = fields.filter { (_, v) -> p1 == v.register } .toList().firstOrNull()?.first
			if (field != null) calculateWhenCurrentField(field)
		}
	}

	init {
		if (dynamicRecalculation) enableDynamicRecalculation()
	}

	fun enableDynamicRecalculation() {
		registry.add(calculateOthersCallback)
	}

	fun disableDynamicRecalculation() {
		registry.remove(calculateOthersCallback)
	}

	fun destroy() {
		disableDynamicRecalculation()
	}

	private fun validateNewValues() {
		storedValues = newValues.copy()
	}

	private fun setField(field: String) {
		var fieldObj = fields[field]
	}

	@get:Bindable
	var total: BigDecimal
		get() = newValues.total
		set(total) {
			newValues.total = total.setScale(2, BigDecimal.ROUND_UP)
			onTotalChange?.invoke()
			validateNewValues()
			registry.notifyChange(this, BR.total)
		}

	@get:Bindable
	var base: BigDecimal
		get() = newValues.base
		set(base) {
			newValues.base = base.setScale(2, BigDecimal.ROUND_UP)
			validateNewValues()
			registry.notifyChange(this, BR.base)
			registry.notifyChange(this, BR.tipInDollars)
			registry.notifyChange(this, BR.baseWithTax)
		}

	@get:Bindable
	var tax: BigDecimal
		get() = newValues.tax
		set(tax) {
			newValues.tax = tax.setScale(maxOf(tax.scale(), 3), BigDecimal.ROUND_UP)
			validateNewValues()
			registry.notifyChange(this, BR.tax)
		}

	@get:Bindable
	var baseWithTax: BigDecimal
		get() = ((ONE + tax / HUN) * base).setScale(2, BigDecimal.ROUND_UP)
		set(baseWithTax) {
			// Prevent recursiveness
			if (currentField != "baseWithTax") return

			val newBase = calculateBaseFromBaseWithTax(baseWithTax)
			if (newBase != null) base = newBase
		}

	@get:Bindable
	var tip: BigDecimal
		get() = newValues.tip
		set(tip) {
			newValues.tip = tip.setScale(minOf(tip.scale(), 3), BigDecimal.ROUND_UP)
			validateNewValues()
			registry.notifyChange(this, BR.tip)
			registry.notifyChange(this, BR.tipInDollars)
		}

	@get:Bindable
	var tipInDollars: BigDecimal
		get() = ((tip * base)/ HUN).setScale(2, BigDecimal.ROUND_UP)
		set(tipInDollars) {
			// Prevent recursiveness
			if (currentField != "tipInDollars") return

			val newTip = calculateTipFromTipInDollars(tipInDollars)
			if (newTip != null) tip = newTip
		}

	var tipOnTax: Boolean = false

	private fun _calculateOtherFields(_field: String, n: BigDecimal? = null) {
		fields.keys.forEach({x -> if (x != _field) fieldMap.remove(x)})
		val field = fields[_field]?.rootField ?: _field
		val cf = fields[field]?.calculateFrom

		if (n != null) fields[field]?.calculateFrom?.invoke(n)
		else fields[field]?.calculateFrom0?.invoke()
	}

	fun calculateWhenCurrentField(field: String) {
		if (field == currentField) _calculateOtherFields(field)
	}

	fun calculateOtherFields(field: String, _n: BigDecimal?, lockedFields: List<String>? = null) {
		val n = _n ?: BigDecimal.ZERO
		_calculateOtherFields(field, n)
	}

	fun calculateTipFromTipInDollars(tipInDollars: BigDecimal): BigDecimal? {
		try { return (tipInDollars * HUN) / base }
		catch(e: Exception) { return null }
	}

	fun calculateBaseFromBaseWithTax(baseWithTax: BigDecimal): BigDecimal? {
		try { return baseWithTax / (ONE + tax / HUN) }
		catch(e: Exception) { return null }
	}

	fun calculateTotal(base: BigDecimal = this.base, tax: BigDecimal = this.tax, tip: BigDecimal = this.tip) {
		this.total = (base * taxAndTipFactor(tax, tip))
	}

	fun calculateBase(total: BigDecimal = this.total, tax: BigDecimal = this.tax, tip: BigDecimal = this.tip) {
		try {
			base = (total / taxAndTipFactor(tax, tip))
		} catch(e: Exception) {}
	}

	fun calculateTip(total: BigDecimal = this.total, base: BigDecimal = this.base, tax: BigDecimal = this.tax) {
		try {
			tip = ((HUN * total - base * (HUN + tax)).setScale(2, BigDecimal.ROUND_UP) / base.setScale(2, BigDecimal.ROUND_UP))
		} catch(e: Exception) {}
	}

	fun calculateFromBase(base: BigDecimal = this.base) {
		calculateTotal(base = base)
	}

	fun calculateFromTotal(total: BigDecimal = this.total) {
		if (base.compareTo(BigDecimal.ZERO) == 0) calculateBase()
		else calculateTip(total = total)
	}

	fun calculateFromTax(tax: BigDecimal = this.tax) {
		calculateTotal(tax = tax)
	}

	fun calculateFromTip(tip: BigDecimal = this.tip) {
		calculateTotal(tip = tip)
	}

	fun taxAndTipFactor(tax: BigDecimal = this.tax, tip: BigDecimal = this.tip): BigDecimal {
		if (tipOnTax) return (ONE + tip/HUN) * (ONE + tax/HUN)
		return ONE + (tip + tax)/HUN
	}

	fun resetBaseTotal() {
		newValues.total = BigDecimal.ZERO
		newValues.base = BigDecimal.ZERO
	}

	fun matchTaxAndTip(bill: Bill) {
		this.tax = bill.tax
		this.tip = bill.tip
	}

	override fun isFieldEnabled(field: String, isNullable: Boolean?, value: BigDecimal?): Boolean {
		return fields[field]?.isEnabled?.invoke() ?: true
	}

	override fun getLabel(field: String): String {
		return fields[field]?.label ?: super.getLabel(field)
	}

	data class Values(
		var base: BigDecimal = BigDecimal.ZERO.setScale(2),
		var total: BigDecimal = BigDecimal.ZERO.setScale(2),
		var tax: BigDecimal = DEFAULT_TAX,
		var tip: BigDecimal = DEFAULT_TIP
	)

	data class Field(
		var label: String,
		var register: Int,
		var calculateFrom: ((BigDecimal) -> Unit)? = null,
		var calculateFrom0: (() -> Unit)? = null,
		var rootField: String? = null,
		var isEnabled: (() -> Boolean)? = null,
		var notifyList: List<Int>? = null
	)

	companion object {
		val DEFAULT_TAX: BigDecimal = BigDecimal(8.875).setScale(3)
		val DEFAULT_TIP: BigDecimal = BigDecimal(15).setScale(0)
	}
}