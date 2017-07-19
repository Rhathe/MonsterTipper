package com.rhathe.monstertipper.models

import android.arch.persistence.room.Ignore
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import com.rhathe.monstertipper.BR

class Meal: Observable {
	val bill = Bill()
	var onAddTippers: ((Tipper) -> Unit) = {}
	var onRemoveTippers: ((Tipper, Int) -> Unit) = { _, _ -> }

	@get:Bindable
	val tippers = mutableListOf<Tipper>()

	fun addTippers() {
		val tipper = Tipper()
		tippers.add(tipper)
		registry.notifyChange(this, BR.tippers)
		onAddTippers(tipper)
	}

	fun removeTippers() {
		try {
			val index = tippers.size - 1
			val tipper = tippers.removeAt(index)
			registry.notifyChange(this, BR.tippers)
			onRemoveTippers(tipper, index)
		} catch(_: Exception) {}
	}

	@Ignore
	private val registry = PropertyChangeRegistry()

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		registry.remove(callback)
	}
}