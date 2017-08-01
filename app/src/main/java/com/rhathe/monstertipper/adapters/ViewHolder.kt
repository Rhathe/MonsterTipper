package com.rhathe.monstertipper.adapters

import android.content.Intent
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.rhathe.monstertipper.services.CurrentService


class ViewHolder(adapter: BaseItemListAdapter, binding: ViewDataBinding, items: MutableList<Any>) :
		RecyclerView.ViewHolder(binding.root) {

	private val adapter = adapter
	private val binding = binding
	val items = items

	fun bind(item: Any, brID: Int) {
		binding.setVariable(brID, item)
		binding.executePendingBindings()
		binding.root.setOnClickListener { _ -> adapter.goToItem(item, binding.root.context) }
	}
}