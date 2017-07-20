package com.rhathe.monstertipper.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Meal
import com.rhathe.monstertipper.models.Tipper

import kotlinx.android.synthetic.main.tipper_detail.*

class TipperDetailActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val binding: ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.tipper_detail)
		val tipperIndex = Integer.parseInt(intent.extras.get("tipperIndex").toString())
		val tipper = Meal.currentMeal?.tippers?.get(tipperIndex) ?: Tipper()
		binding.setVariable(BR.tipper, tipper)
	}

}
