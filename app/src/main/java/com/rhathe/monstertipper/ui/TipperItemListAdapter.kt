package com.rhathe.monstertipper.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.rhathe.monstertipper.models.Tipper
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rhathe.monstertipper.BR
import com.rhathe.monstertipper.R


class TipperItemListAdapter(tippers: MutableList<Tipper>) : RecyclerView.Adapter<TipperItemListAdapter.ViewHolder>() {
	val tippers = tippers

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.tipper_item, parent, false)
		return ViewHolder(binding, tippers)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val tipper = tippers[position]
		holder.bind(tipper)
	}

	override fun getItemCount(): Int {
		return tippers.size
	}

	class ViewHolder(binding: ViewDataBinding, tippers: MutableList<Tipper>) : RecyclerView.ViewHolder(binding.root) {
		private val binding = binding
		val tippers = tippers

		fun bind(tipper: Tipper) {
			binding.setVariable(BR.tipper, tipper)
			binding.executePendingBindings()

			binding.root.setOnClickListener { _ ->
				val intent = Intent(binding.root.context, TipperDetailActivity::class.java)
				val tipperIndex = tippers.indexOf(tipper)
				intent.putExtra("tipperIndex", tipperIndex)
				binding.root.context.startActivity(intent)
			}
		}
	}
}