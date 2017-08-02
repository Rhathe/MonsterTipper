package com.rhathe.monstertipper.adapters

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView


class ViewHolder(private val adapter: BaseItemListAdapter, private val binding: ViewDataBinding) :
		RecyclerView.ViewHolder(binding.root) {

	fun bind(item: Any, brID: Int) {
		binding.setVariable(brID, item)
		binding.executePendingBindings()
		binding.root.setOnClickListener { _ -> adapter.goToItem(item, binding.root.context) }
	}
}