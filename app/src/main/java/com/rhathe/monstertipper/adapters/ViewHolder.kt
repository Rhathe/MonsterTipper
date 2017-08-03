package com.rhathe.monstertipper.adapters

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView


class ViewHolder(private val adapter: BaseItemListAdapter, private val binding: ViewDataBinding) :
		RecyclerView.ViewHolder(binding.root) {

	fun bind(item: Any, brId: Int) {
		setBinding(item, brId)
		binding.root.setOnClickListener { _ -> adapter.goToItem(item, binding.root.context) }
	}

	fun setBinding(item: Any, brId: Int) {
		binding.setVariable(brId, item)
		binding.executePendingBindings()
	}
}