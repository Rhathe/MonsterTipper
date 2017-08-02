package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.models.ConsumableItem
import com.rhathe.monstertipper.services.CurrentService

class ItemDetailActivity : AppCompatActivity() {
	var tipper: Tipper? = null
	var item: ConsumableItem? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail)
		tipper = CurrentService.getCurrent(Tipper::class.java) as Tipper? ?: Tipper()
		item = CurrentService.getCurrent(ConsumableItem::class.java) as ConsumableItem? ?: ConsumableItem()
		binding.setVariable(BR.item, item)
	}

	fun finish(@Suppress("UNUSED_PARAMETER") v: View) {
		finish()
	}
}
