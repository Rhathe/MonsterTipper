package com.rhathe.monstertipper.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

open class BaseItemListAdapter(items: MutableList<Any>, layoutId: Int, brId: Int, extraFieldName: String? = null) : RecyclerView.Adapter<ViewHolder>() {
	val items = items
	val layoutId = layoutId
	val brId = brId
	val extraFieldName = extraFieldName

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
		return ViewHolder(binding, items)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = items[position]
		holder.bind(item, brId, extraFieldName)
	}

	override fun getItemCount(): Int {
		return items.size
	}
}