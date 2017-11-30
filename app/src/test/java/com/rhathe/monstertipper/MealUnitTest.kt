package com.rhathe.monstertipper

import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.models.Meal
import com.rhathe.monstertipper.models.Tipper
import org.junit.Test

import org.junit.Assert.*


class MealUnitTest {
    val meal = Meal()
    val tippers = mutableListOf<Tipper>()

    init {
        tippers.add(Tipper("t0"))
        tippers.add(Tipper("t1"))
        tippers.add(Tipper("t2"))
        tippers.add(Tipper("t3"))
        tippers.add(Tipper("t4"))
        tippers.add(Tipper("t5"))
        tippers.add(Tipper("t6"))
    }

    @Test
    fun getTippersOnConsumable_consumed_test() {
        val consumable = Consumable()
        consumable.addAsConsumed(tippers[3])
        consumable.addAsConsumed(tippers[6])

        val newTippers = meal.getTippersOnConsumable(consumable, tippers).toSet()
        assertEquals(newTippers, setOf(tippers[3], tippers[6]))
    }

    @Test
    fun getTippersOnConsumable_avoided_test() {
        val consumable = Consumable()
        consumable.addAsAvoided(tippers[3])
        consumable.addAsAvoided(tippers[6])

        val newTippers = meal.getTippersOnConsumable(consumable, tippers).toSet()
        assertEquals(newTippers, setOf(tippers[0], tippers[1], tippers[2], tippers[4], tippers[5]))
    }

    @Test
    fun getTippersOnConsumable_consumed_and_avoided_test() {
        val consumable = Consumable()
        consumable.addAsAvoided(tippers[4])
        consumable.addAsAvoided(tippers[6])
        consumable.addAsConsumed(tippers[1])
        consumable.addAsConsumed(tippers[5])
        consumable.addAsConsumed(tippers[6])
        consumable.addAsAvoided(tippers[1])

        val newTippers = meal.getTippersOnConsumable(consumable, tippers).toSet()
        assertEquals(newTippers, setOf(tippers[5], tippers[6]))
    }

    @Test
    fun getTippersOnConsumable_empty_test() {
        val consumable = Consumable()
        val newTippers = meal.getTippersOnConsumable(consumable, tippers).toSet()
        assertEquals(newTippers, tippers.toSet())
    }
}