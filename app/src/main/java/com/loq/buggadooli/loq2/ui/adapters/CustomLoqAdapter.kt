package com.loq.buggadooli.loq2.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.databinding.ItemCustomLoqBinding
import com.loq.buggadooli.loq2.extensions.inflateWithBinding
import com.loq.buggadooli.loq2.models.CustomLoqItem

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