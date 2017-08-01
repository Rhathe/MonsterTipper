package com.rhathe.monstertipper.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import com.rhathe.monstertipper.models.Tipper


class TipperDetailActivity : AppCompatActivity() {
	var tipper: Tipper? = null
	var tipperIndex: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.tipper_detail)
		tipperIndex = Integer.parseInt(intent.extras.get("tipperIndex").toString())
		tipper = Meal.currentMeal?.tippers?.get(tipperIndex) ?: Tipper()
		binding.setVariable(BR.tipper, tipper)
	}

	fun goToAddConsumedItem() {
		val intent = Intent(this, ItemDetailActivity::class.java)
		intent.putExtra("isConsumed", true)
		intent.putExtra("tipperIndex", tipperIndex)
		startActivity(intent)
	}

	fun goToAddAvoidedItem() {
		val intent = Intent(this, ItemDetailActivity::class.java)
		intent.putExtra("isConsumed", false)
		intent.putExtra("tipperIndex", tipperIndex)
		startActivity(intent)
	}
}
