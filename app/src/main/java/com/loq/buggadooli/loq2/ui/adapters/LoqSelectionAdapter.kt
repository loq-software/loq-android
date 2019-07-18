package com.loq.buggadooli.loq2.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.databinding.LoqItemBinding
import com.loq.buggadooli.loq2.extensions.inflateWithBinding
import com.loq.buggadooli.loq2.ui.adapters.LoqSelectionAdapter.LoqViewHolder
import com.loq.buggadooli.loq2.ui.listeners.LoqSelectionEditListener

class LoqSelectionAdapter(private val mDataset: List<Loq>) : RecyclerView.Adapter<LoqViewHolder>() {

    var listener: LoqSelectionEditListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LoqViewHolder {
        val viewBinding = viewGroup.inflateWithBinding<LoqItemBinding>(R.layout.loq_item)
        return LoqViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(loqViewHolder: LoqViewHolder, index: Int) {
        val loq = mDataset[index]
        loqViewHolder.bind(loq)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class LoqViewHolder(
            private val binding: LoqItemBinding,
            private val listener: LoqSelectionEditListener?
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(loq: Loq) {
            binding.loq = loq
            binding.btnEdit.setOnClickListener {
                listener?.onLoqSelectionEditListenerClicked(loq, adapterPosition)
            }
            binding.executePendingBindings()
        }

    }

}
