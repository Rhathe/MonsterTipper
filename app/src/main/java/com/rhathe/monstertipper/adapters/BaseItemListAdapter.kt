package com.rhathe.monstertipper.adapters

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rhathe.monstertipper.services.CurrentService

open class BaseItemListAdapter(items: MutableList<Any>, layoutId: Int, brId: Int, activityClass: Class<*>? = null) :
		RecyclerView.Adapter<ViewHolder>() {

	val items = items
	val layoutId = layoutId
	val brId = brId
	val activityClass = activityClass

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
		return ViewHolder(this, binding, items)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = items[position]
		holder.bind(item, brId)
	}

	override fun getItemCount(): Int {
		return items.size
	}

	fun goToItem(item: Any, ctx: Context) {
		val intent = Intent(ctx, activityClass)
		CurrentService.setAsCurrent(item)
		ctx.startActivity(intent)
	}
}