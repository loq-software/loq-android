package com.loq.buggadooli.loq2.ui.adapters

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.selection.SelectionTracker
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.utils.Utils

class AppSelectionAdapter(private val listItems:List<ApplicationInfo>,
                          private val context: Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<AppSelectionViewHolder>() {

    private var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AppSelectionViewHolder =
            AppSelectionViewHolder(
                    LayoutInflater.from(context)
                            .inflate(R.layout.list_item, parent, false)
            )

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(p0: AppSelectionViewHolder, p1: Int) {
        p0.name.text = listItems[p1].loadLabel(context.packageManager).toString();
        val parent = p0.name.parent as LinearLayout
        if(tracker!!.isSelected(p1.toLong())) {
            Utils.INSTANCE.addAppInfo(listItems[p1]);
            parent.background = ColorDrawable(
                    Color.parseColor("#80deea")
            )
        } else {
            parent.background = ColorDrawable(Color.WHITE)
        }
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }
}