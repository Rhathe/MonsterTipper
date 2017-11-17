package com.rhathe.monstertipper

import com.rhathe.monstertipper.models.Bill
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty0


class BillUnitTest {
	fun testSetAndGet(prop: KMutableProperty0<BigDecimal>, expected: List<String>, given: List<Number?>) {
		for ((a, b) in expected.zip(given)) {
			if (b != null) prop.set(BigDecimal(b.toDouble()))
			assertEquals(a, prop.get().toString())
		}
	}

	@Test
	fun total_test() {
		val bill = Bill()
		testSetAndGet(bill::total, listOf("0.00", "5.00", "7.40"), listOf(null, 5, 7.39302))
	}

	@Test
	fun base_test() {
		val bill = Bill()
		testSetAndGet(bill::base, listOf("0.00", "5.00", "7.40"), listOf(null, 5, 7.39302))
	}
}
