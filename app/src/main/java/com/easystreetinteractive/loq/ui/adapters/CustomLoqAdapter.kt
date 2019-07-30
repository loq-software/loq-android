package com.easystreetinteractive.loq.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.databinding.ItemCustomLoqBinding
import com.easystreetinteractive.loq.extensions.inflateWithBinding
import com.easystreetinteractive.loq.models.CustomLoqItem

class CustomLoqAdapter(): RecyclerView.Adapter<CustomLoqAdapter.CustomLoqViewHolder>() {

    var applicationItems: List<CustomLoqItem> = emptyList()

    var checkboxListener: CustomOnCheckChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomLoqViewHolder {
        val viewBinding = parent.inflateWithBinding<ItemCustomLoqBinding>(R.layout.item_custom_loq)
        return CustomLoqViewHolder(viewBinding, checkboxListener)
    }

    override fun getItemCount(): Int {
        return applicationItems.size
    }

    override fun onBindViewHolder(holder: CustomLoqViewHolder, position: Int) {
        val item = applicationItems[position]
        holder.bind(item)
    }

    fun updateData(data: List<CustomLoqItem>){
        applicationItems = data
        notifyDataSetChanged()
    }

    fun getApplicationForPosition(position: Int): CustomLoqItem {
        return applicationItems[position]
    }


    class CustomLoqViewHolder(
            private val binding: ItemCustomLoqBinding,
            private val listener: CustomOnCheckChangedListener?): RecyclerView.ViewHolder(binding.root){

        fun bind(item: CustomLoqItem){
            binding.item = item
            binding.executePendingBindings()

            binding.selected.setOnCheckedChangeListener { _, checked ->
                listener?.onCheckBoxChanged(checked, adapterPosition)
            }
        }
    }

    interface CustomOnCheckChangedListener{

        fun onCheckBoxChanged(checked: Boolean, adapterPosition: Int)
    }
}