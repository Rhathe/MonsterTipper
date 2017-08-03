package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.models.Tipper
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.ui.TipperDetailActivity


class TipperItemListAdapter(tippers: MutableList<Tipper>) :
		BaseItemListAdapter(tippers as MutableList<Any>, R.layout.tipper_item, BR.tipper, activityClass = TipperDetailActivity::class.java)