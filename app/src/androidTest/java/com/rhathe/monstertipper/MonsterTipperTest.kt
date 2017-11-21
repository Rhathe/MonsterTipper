package com.rhathe.monstertipper

import android.support.test.espresso.*
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log

import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith

import com.rhathe.monstertipper.ui.Main
import junit.framework.AssertionFailedError
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not


@RunWith(AndroidJUnit4::class)
class MonsterTipperTest {
	@Rule
	@JvmField
	val activity = ActivityTestRule<Main>(Main::class.java)

	private fun waitFor(condition: () -> Unit, sleep: Int = 200, checkN: Int = 25) {
		var lastE: AssertionFailedError? = null
		for (i in 1..checkN) {
			try {
				condition()
				return
			} catch(e: AssertionFailedError) {
				lastE = e
			}
		}
		if (lastE != null) throw lastE
	}

	private fun getEditText(id: Int): ViewInteraction {
		return onView(allOf(withId(R.id.field_text_value), withParent(withId(id))))
	}

	private fun matchText(t: String): ViewAssertion {
		return matches(withText(t))
	}

	private fun matchEnabled(b: Boolean = true): ViewAssertion {
		val check = if (b) isEnabled() else not(isEnabled())
		return matches(check)
	}

	private fun getInteractions(): Interactions {
		return Interactions(
			base = getEditText(R.id.bill_base),
			tax = getEditText(R.id.bill_tax),
			baseWithTax = getEditText(R.id.bill_baseWithTax),
			tip = getEditText(R.id.bill_tip),
			tipInDollars = getEditText(R.id.bill_tipInDollars),
			total = getEditText(R.id.bill_total)
		)
	}

	@Test
	fun testInit() {
		val (base, tax, baseWithTax, tip, tipInDollars, total) = getInteractions()
		base.check(matchText("0.00"))
		tax.check(matchText("8.875"))
		baseWithTax.check(matchText("0.00"))
		tip.check(matchText("15"))
		tipInDollars.check(matchText("0.00"))
		total.check(matchText("0.00"))

		tipInDollars.check(matchEnabled(false))
		total.check(matchEnabled(false))
	}

	@Test
	fun testBase() {
		val (base, _, baseWithTax, _, tipInDollars, total) = getInteractions()
		base.perform(typeText("50"))

		base.check(matchText("50"))
		baseWithTax.check(matchText("54.45"))
		tipInDollars.check(matchText("7.50"))
		total.check(matchText("61.95"))

		tipInDollars.check(matchEnabled())
		total.check(matchEnabled())
	}

	@Test
	fun testTax() {
		val (base, tax, baseWithTax, _, _, total) = getInteractions()
		base.perform(typeText("50"))
		tax.perform(typeText("10"))

		base.check(matchText("50.00"))
		tax.check(matchText("10"))
		baseWithTax.check(matchText("55.00"))
		total.check(matchText("62.50"))
	}

	@Test
	fun testBaseWithTax() {
		val (base, tax, baseWithTax, _, tipInDollars, total) = getInteractions()
		baseWithTax.perform(typeText("87.10"))
		waitFor({total.check(matchText("99.12"))})

		base.check(matchText("80.00"))
		tax.check(matchText("8.875"))
		baseWithTax.check(matchText("87.10"))
		tipInDollars.check(matchText("12.00"))
		total.check(matchText("99.12"))
	}

	@Test
	fun testTip() {
		val (base, _, baseWithTax, tip, tipInDollars, total) = getInteractions()
		base.perform(typeText("50"))
		tip.perform(typeText("20"))

		base.check(matchText("50.00"))
		baseWithTax.check(matchText("54.45"))
		tip.check(matchText("20"))
		tipInDollars.check(matchText("10.00"))
		total.check(matchText("64.45"))
	}

	@Test
	fun testTipInDollars() {
		val (base, _, baseWithTax, tip, tipInDollars, total) = getInteractions()
		base.perform(typeText("50"))
		tipInDollars.perform(typeText("10"))
		waitFor({tip.check(matchText("20.00"))})

		base.check(matchText("50.00"))
		baseWithTax.check(matchText("54.45"))
		tip.check(matchText("20.00"))
		tipInDollars.check(matchText("10"))
		total.check(matchText("64.45"))
	}

	@Test
	fun testTotal() {
		val (base, tax, baseWithTax, tip, tipInDollars, total) = getInteractions()
		base.perform(typeText("50"))
		total.perform(typeText("70"))

		base.check(matchText("50.00"))
		tax.check(matchText("8.875"))
		baseWithTax.check(matchText("54.45"))
		tip.check(matchText("31.12"))
		tipInDollars.check(matchText("15.56"))
		total.check(matchText("70"))
	}

	data class Interactions(
		var base:ViewInteraction,
		var tax:ViewInteraction,
		var baseWithTax:ViewInteraction,
		var tip:ViewInteraction,
		var tipInDollars:ViewInteraction,
		var total:ViewInteraction
	)
}
