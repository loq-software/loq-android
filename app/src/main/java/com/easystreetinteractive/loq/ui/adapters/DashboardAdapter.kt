package com.easystreetinteractive.loq.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.databinding.ItemDashboardBinding

import com.easystreetinteractive.loq.extensions.inflateWithBinding
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.ui.adapters.DashboardAdapter.LoqViewHolder
import com.easystreetinteractive.loq.ui.listeners.LoqSelectionEditListener

class DashboardAdapter : RecyclerView.Adapter<LoqViewHolder>() {

    private var loqs: List<BlockedApplication> = emptyList()

    var loqSelectionListener: LoqSelectionEditListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LoqViewHolder {
        val viewBinding = viewGroup.inflateWithBinding<ItemDashboardBinding>(R.layout.item_dashboard)
        return LoqViewHolder(viewBinding, loqSelectionListener)
    }

    override fun onBindViewHolder(loqViewHolder: LoqViewHolder, index: Int) {
        val loq = loqs[index]
        loqViewHolder.bind(loq)
    }

    override fun getItemCount(): Int {
        return loqs.size
    }

    fun updateData(data: List<BlockedApplication>){
        loqs = data
        notifyDataSetChanged()
    }

    class LoqViewHolder constructor(
            private val binding: ItemDashboardBinding,
            private val loqSelectionListener: LoqSelectionEditListener?
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(loq: BlockedApplication) {
            binding.loq = loq

            binding.executePendingBindings()

            binding.btnEdit.setOnClickListener {
                loqSelectionListener?.onLoqSelectionEditListenerClicked(loq, adapterPosition)
            }
        }

    }

}
