package com.rhathe.monstertipper

import com.rhathe.monstertipper.models.Bill
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty0


class BillUnitTest {
	fun testSetAndGet(setProp: KMutableProperty0<BigDecimal>, _getProp: KMutableProperty0<BigDecimal>?, expected: List<String>, given: List<Number?>) {
		val getProp = _getProp ?: setProp
		for ((a, b) in expected.zip(given)) {
			if (b != null) setProp.set(BigDecimal(b.toDouble()))
			assertEquals(a, getProp.get().toString())
		}
	}

	@Test
	fun total_test() {
		val bill = Bill()
		testSetAndGet(bill::total, null, listOf("0.00", "5.00", "7.40"), listOf(null, 5, 7.39302))
	}

	@Test
	fun base_test() {
		val bill = Bill()
		testSetAndGet(bill::base, null, listOf("0.00", "5.00", "7.40"), listOf(null, 5, 7.39302))
	}

	@Test
	fun tax_test() {
		val bill = Bill()
		testSetAndGet(bill::tax, null, listOf("8.875", "5", "7.394"), listOf(null, 5, 7.39302))
	}

	@Test
	fun tip_test() {
		val bill = Bill()
		testSetAndGet(bill::tip, null, listOf("15", "5", "7.394"), listOf(null, 5, 7.39302))
	}

	@Test
	fun tipInDollars_test() {
		val bill = Bill()
		bill.base = BigDecimal.TEN
		bill.currentField = "tipInDollars"
		testSetAndGet(bill::tipInDollars, bill::tip, listOf("15", "50.00", "73.931"), listOf(null, 5, 7.39302))
	}

	@Test
	fun baseWithTax_test() {
		val bill = Bill()
		bill.base = BigDecimal.TEN
        bill.currentField = "baseWithTax"
		testSetAndGet(bill::baseWithTax, bill::base, listOf("10.00", "4.60", "6.79"), listOf(null, 5, 7.39302))
	}

	@Test
	fun taxAndTipFactor_test() {
		val bill = Bill()
		assertEquals("1.200", bill.taxAndTipFactor(tax=BigDecimal(10), tip= BigDecimal(10)).toString())
		assertEquals("1.200", bill.taxAndTipFactor(tax=BigDecimal("10"), tip= BigDecimal("10")).toString())
		assertEquals("1.200", bill.taxAndTipFactor(tax=BigDecimal("10.0"), tip= BigDecimal("10.0")).toString())
		assertEquals("1.20000", bill.taxAndTipFactor(tax=BigDecimal("10.00000"), tip= BigDecimal("10.00000")).toString())
	}
}
