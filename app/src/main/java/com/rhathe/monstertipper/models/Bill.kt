package com.rhathe.monstertipper.models

import android.databinding.*
import com.rhathe.monstertipper.BR
import java.math.BigDecimal


class Bill(
		tax: BigDecimal? = null,
		tip: BigDecimal? = null,
		var onTotalChange: (() -> Unit)? = null,
		dynamicRecalculation: Boolean = false): MoneyBase() {

	private val ONE: BigDecimal = BigDecimal.ONE
	private val HUN: BigDecimal = BigDecimal(100)
	private val listOfFields = listOf("total", "base", "tax", "tip", "tipInDollars", "baseWithTax")

	var storedValues = Values(tax=tax?:DEFAULT_TAX, tip=tip?:DEFAULT_TIP)
	var newValues = storedValues.copy()

	private val calculateOthersCallback = object: Observable.OnPropertyChangedCallback() {
		override fun onPropertyChanged(p0: Observable?, p1: Int) {
			when (p1) {
				BR.total -> calculateWhenCurrentField("total", getTotal())
				BR.base -> calculateWhenCurrentField("base", getBase())
				BR.baseWithTax -> calculateWhenCurrentField("baseWithTax", getBase())
				BR.tax -> calculateWhenCurrentField("tax", getTax())
				BR.tip -> calculateWhenCurrentField("tip", getTip())
				BR.tipInDollars -> calculateWhenCurrentField("tipInDollars", getTip())
			}
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

	fun validateNewValues() {
		storedValues = newValues.copy()
	}

	@Bindable
	fun getTotal(): BigDecimal { return newValues.total }

	fun setTotal(total: BigDecimal) {
		newValues.total = total.setScale(2, BigDecimal.ROUND_UP)
		onTotalChange?.invoke()
		validateNewValues()
		registry.notifyChange(this, BR.total)
	}

	@Bindable
	fun getBase(): BigDecimal { return newValues.base }

	fun setBase(base: BigDecimal) {
		newValues.base = base.setScale(2, BigDecimal.ROUND_UP)
		validateNewValues()
		registry.notifyChange(this, BR.base)
		registry.notifyChange(this, BR.tipInDollars)
		registry.notifyChange(this, BR.baseWithTax)
	}

	@Bindable
	fun getTax(): BigDecimal { return newValues.tax }

	fun setTax(tax: BigDecimal) {
		newValues.tax = tax
		validateNewValues()
		registry.notifyChange(this, BR.tax)
	}

	@Bindable
	fun getBaseWithTax(): BigDecimal { return ((ONE + getTax() / HUN) * getBase()).setScale(2, BigDecimal.ROUND_UP) }

	@Bindable
	fun setBaseWithTax(baseWithTax: BigDecimal) {
		// Prevent recursiveness
		if (currentField != "baseWithTax") return

		val newBase = calculateBaseFromBaseWithTax(baseWithTax)
		if (newBase != null) setBase(newBase)
	}

	@Bindable
	fun getTip(): BigDecimal { return newValues.tip }

	fun setTip(tip: BigDecimal) {
		newValues.tip = tip.setScale(minOf(tip.scale(), 3), BigDecimal.ROUND_UP)
		validateNewValues()
		registry.notifyChange(this, BR.tip)
		registry.notifyChange(this, BR.tipInDollars)
	}

	@Bindable
	fun getTipInDollars(): BigDecimal { return ((getTip() * getBase())/ HUN).setScale(2, BigDecimal.ROUND_UP) }

	@Bindable
	fun setTipInDollars(tipInDollars: BigDecimal) {
		// Prevent recursiveness
		if (currentField != "tipInDollars") return

		val newTip = calculateTipFromTipInDollars(tipInDollars)
		if (newTip != null) setTip(newTip)
	}

	var tipOnTax: Boolean = false

	fun calculateWhenCurrentField(field: String, value: BigDecimal?) {
		if (field == currentField) calculateOtherFields(field, value)
	}

	fun calculateOtherFields(field: String, _n: BigDecimal?) {
		listOfFields.forEach({x -> if (x != field) fieldMap.remove(x)})
		val n = _n ?: BigDecimal.ZERO
		if (field == "base" || field == "baseWithTax") calculateFromBase(n)
		else if (field == "total") calculateFromTotal(n)
		else if (field == "tip" || field == "tipInDollars") calculateFromTip(n)
		else if (field == "tax") calculateFromTax(n)
	}

	fun calculateTipFromTipInDollars(tipInDollars: BigDecimal): BigDecimal? {
		try { return (tipInDollars * HUN) / getBase() }
		catch(e: Exception) { return null }
	}

	fun calculateBaseFromBaseWithTax(baseWithTax: BigDecimal): BigDecimal? {
		try { return baseWithTax / (ONE + getTax() / HUN) }
		catch(e: Exception) { return null }
	}

	fun calculateTotal(base: BigDecimal = getBase(), tax: BigDecimal = getTax(), tip: BigDecimal = getTip()) {
		setTotal(base * taxAndTipFactor(tax, tip))
	}

	fun calculateBase(total: BigDecimal = getTotal(), tax: BigDecimal = getTax(), tip: BigDecimal = getTip()) {
		try {
			setBase(total / taxAndTipFactor(tax, tip))
		} catch(e: Exception) {}
	}

	fun calculateTip(total: BigDecimal = getTotal(), base: BigDecimal = getBase(), tax: BigDecimal = getTax()) {
		try {
			setTip((HUN * total - base * (HUN + tax)).setScale(2, BigDecimal.ROUND_UP) / base.setScale(2, BigDecimal.ROUND_UP))
		} catch(e: Exception) {}
	}

	fun calculateFromBase(base: BigDecimal = getBase()) {
		calculateTotal(base = base)
	}

	fun calculateFromTotal(total: BigDecimal = getTotal()) {
		if (getBase().compareTo(BigDecimal.ZERO) == 0) calculateBase()
		else calculateTip(total = total)
	}

	fun calculateFromTax(tax: BigDecimal = getTax()) {
		calculateTotal(tax = tax)
	}

	fun calculateFromTip(tip: BigDecimal = getTip()) {
		calculateTotal(tip = tip)
	}

	fun taxAndTipFactor(tax: BigDecimal = getTax(), tip: BigDecimal = getTip()): BigDecimal {
		if (tipOnTax) return (ONE + tip/HUN) * (ONE + tax/HUN)
		return ONE + (tip + tax)/HUN
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
				if (getBase().compareTo(BigDecimal.ZERO) == 0) return false
			} "tipInDollars" -> {
				if (getBase().compareTo(BigDecimal.ZERO) == 0) return false
			}
		}
		return true
	}

	override fun getLabel(field: String): String {
		when(field) {
			"total" -> return "Total ($)"
			"base" -> return "Base ($)"
			"baseWithTax" -> return "Base With Tax ($)"
			"tax" -> return "Tax (%)"
			"tip" -> return "Tip (%)"
			"tipInDollars" -> return "Tip In Dollars ($)"
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