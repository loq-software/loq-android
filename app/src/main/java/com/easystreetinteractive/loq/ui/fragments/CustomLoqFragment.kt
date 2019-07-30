package com.easystreetinteractive.loq.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.*
import com.easystreetinteractive.loq.ui.adapters.CustomLoqAdapter
import com.easystreetinteractive.loq.ui.viewmodels.CustomLoqViewModel
import kotlinx.android.synthetic.main.fragment_custom_loq.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CustomLoqFragment: Fragment() {

    private lateinit var loqAdapter: CustomLoqAdapter
    private val viewModel by sharedViewModel<CustomLoqViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_custom_loq, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loqAdapter = CustomLoqAdapter()
        loqAdapter.checkboxListener = checkboxListener
        recycler.apply {
            layoutManager = LinearLayoutManager(safeActivity)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = 16
                }
            })
            adapter = loqAdapter
        }
        recycler.addOnItemClick { position, view ->
            val item = loqAdapter.getApplicationForPosition(position)
            val selected = item.isSelected
            item.isSelected = !selected
            loqAdapter.notifyItemChanged(position)
        }

        nextButton.setOnClickListener {
            val selectedItems = loqAdapter.applicationItems.getSelectedApplicationInformationList()
            if (selectedItems.isNotEmpty()) {
                val bundle = bundleOf(Constants.APP_NAME to selectedItems)
                val fragment = SetAndForgetFragment().apply {
                    arguments = bundle
                }
                safeActivity.addFragment(fragment = fragment)
                return@setOnClickListener
            }
            safeActivity.toast("Please select applications to block")
        }

        backButton.setOnClickListener { safeActivity.onBackPressed() }

        viewModel.onApplicationsLoaded.observe(this, Observer { event ->
            progressLayout.hide()
            val data = event.getContentIfNotHandled()
            data?.let {
                val customLoqItems = it.toCustomLoqItems()
                loqAdapter.updateData(customLoqItems)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLayout.show()
        viewModel.loadApplications(view)
    }

    private val checkboxListener: CustomLoqAdapter.CustomOnCheckChangedListener = object : CustomLoqAdapter.CustomOnCheckChangedListener {

        override fun onCheckBoxChanged(checked: Boolean, adapterPosition: Int) {
            val item = loqAdapter.getApplicationForPosition(adapterPosition)
            item.isSelected = checked
            recycler.post {
                loqAdapter.notifyItemChanged(adapterPosition)
            }
        }
    }
}