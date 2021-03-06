package com.rhathe.monstertipper.adapters

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.models.Consumable
import com.rhathe.monstertipper.services.CurrentService

open class BaseItemListAdapter(
		val items: () -> MutableList<Any>,
		val layoutId: Int,
		val brId: Int,
		val activityClass: Class<*>? = null) : RecyclerView.Adapter<ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
		return ViewHolder(this, binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = items()[position]
		bindItemToHolder(holder, item)
		holder.setBinding(this, BR.adapter)
	}

	override fun getItemCount(): Int {
		return items().size
	}

	open fun bindItemToHolder(holder: ViewHolder, item: Any) {
		holder.bind(item, brId)
	}

	fun goToItem(item: Any, ctx: Context) {
		if (activityClass == null) return
		val intent = Intent(ctx, activityClass)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		CurrentService.setAsCurrent(item)
		ctx.startActivity(intent)
	}

	open fun removeFromList(item: Any) {
		items().remove(item)
		this.notifyDataSetChanged()
	}
}
