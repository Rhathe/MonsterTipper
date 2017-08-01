package com.rhathe.monstertipper.adapters

import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R
import com.rhathe.monstertipper.models.Item


class ConsumableItemListAdapter(consumables: MutableList<Item>) : BaseItemListAdapter(consumables as MutableList<Any>, R.layout.tipper_item, BR.consumable)