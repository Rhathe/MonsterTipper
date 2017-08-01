package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.models.Item

class ItemDetailActivity : AppCompatActivity() {
	var tipper: Tipper? = null
	var tipperIndex: Int = 0
	var item: Item? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.tipper_detail)
		tipperIndex = Integer.parseInt(intent.extras.get("tipperIndex").toString())
		tipper = Meal.currentMeal?.tippers?.get(tipperIndex) ?: Tipper()
		val isConsumed: Boolean = intent.extras.get("isConsumed") == true
		binding.setVariable(BR.item, item)
	}
}
