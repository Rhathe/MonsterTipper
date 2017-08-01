package com.rhathe.monstertipper.adapters

import android.content.Intent
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.rhathe.monstertipper.ui.TipperDetailActivity

class ViewHolder(binding: ViewDataBinding, items: MutableList<Any>) : RecyclerView.ViewHolder(binding.root) {
	private val binding = binding
	val items = items

	fun bind(item: Any, brID: Int, extraFieldName: String? = null) {
		binding.setVariable(brID, item)
		binding.executePendingBindings()

		binding.root.setOnClickListener { _ ->
			val intent = Intent(binding.root.context, TipperDetailActivity::class.java)
			intent.putExtra(extraFieldName ?: "itemIndex", items.indexOf(item))
			binding.root.context.startActivity(intent)
		}
	}
}