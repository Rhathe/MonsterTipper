package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.ui.ItemDetailActivity


class ConsumableItemListAdapter(consumables: MutableList<Consumable>) :
		BaseItemListAdapter(consumables as MutableList<Any>, R.layout.consumable_item, BR.consumable, ItemDetailActivity::class.java)