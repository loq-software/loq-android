package com.easystreetinteractive.loq.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.databinding.ItemEditLoqBinding
import com.easystreetinteractive.loq.extensions.inflateWithBinding
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.ui.listeners.OnLoqTimeDeleteListener

class EditLoqAdapter: RecyclerView.Adapter<EditLoqAdapter.EditLoqViewHolder>() {

    private var days: List<BlockedDay> = emptyList()
    var deleteListener: OnLoqTimeDeleteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditLoqViewHolder {
        val viewBinding = parent.inflateWithBinding<ItemEditLoqBinding>(R.layout.item_edit_loq)
        return EditLoqViewHolder(viewBinding, deleteListener)
    }

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(holder: EditLoqViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day)
    }

    fun updateData(data: List<BlockedDay>){
        days = data
        notifyDataSetChanged()
    }

    class EditLoqViewHolder(private val binding: ItemEditLoqBinding, private val listener: OnLoqTimeDeleteListener?) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: BlockedDay) {
            binding.day = day
            binding.executePendingBindings()

            binding.delete.setOnClickListener {
                listener?.onLoqTimeDeleteClicked(day)
            }
        }
    }

}