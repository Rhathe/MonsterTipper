package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry

class Meal: Observable {
	val bill = Bill()
	val tippers: List<Tipper> = emptyList()

	@Ignore
	private val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}
}