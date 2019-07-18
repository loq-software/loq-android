package com.loq.buggadooli.loq2.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import kotlinx.android.synthetic.main.list_item.view.*

class AppSelectionViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.list_item_name

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object: ItemDetailsLookup.ItemDetails<Long>() {
                override fun getSelectionKey(): Long? = itemId

                override fun getPosition(): Int = adapterPosition

            }
}