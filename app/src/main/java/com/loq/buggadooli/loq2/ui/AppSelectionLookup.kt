package com.loq.buggadooli.loq2.ui

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.loq.buggadooli.loq2.ui.adapters.AppSelectionViewHolder

class AppSelectionLookup(private val rv: RecyclerView)
    : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent)
            : ItemDetails<Long>? {

        val view = rv.findChildViewUnder(event.x, event.y)
        if(view != null) {
            return (rv.getChildViewHolder(view) as AppSelectionViewHolder)
                    .getItemDetails()
        }
        return null

    }
}