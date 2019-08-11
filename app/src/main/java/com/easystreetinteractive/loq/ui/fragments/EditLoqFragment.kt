package com.easystreetinteractive.loq.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.addFragment
import com.easystreetinteractive.loq.extensions.inflateTo
import com.easystreetinteractive.loq.extensions.safeActivity
import com.easystreetinteractive.loq.extensions.toast
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.ui.adapters.EditLoqAdapter
import com.easystreetinteractive.loq.ui.listeners.OnLoqTimeDeleteListener
import com.easystreetinteractive.loq.ui.viewmodels.EditLoqViewModel
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_edit_loq.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NullPointerException

class EditLoqFragment: Fragment() {

    private lateinit var editLoqAdapter: EditLoqAdapter
    private lateinit var loq: BlockedApplication
    private val viewModel by viewModel<EditLoqViewModel>()
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loq = arguments?.getParcelable(Constants.LOQ)?: throw NullPointerException()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_edit_loq, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editLoqAdapter = EditLoqAdapter().apply {
            updateData(loq.blockBlockedDays)
            deleteListener = deleteTimeListener
        }

        recycler.apply {
            layoutManager = LinearLayoutManager(safeActivity)
            adapter = editLoqAdapter
        }

        addTimeButton.setOnClickListener {
            val fragment = SetAndForgetFragment()
            val bundle = bundleOf(Constants.LOQ to loq)
            fragment.arguments = bundle
            safeActivity.addFragment(fragment = fragment)
        }

        backButton.setOnClickListener {
            safeActivity.onBackPressed()
        }

        viewModel.loqUpdated.observe(this, Observer { event ->
            val loq = event.getContentIfNotHandled()
            loq?.let {
                safeActivity.toast("Loq time deleted")
                mainViewModel.loadLoqs(this)
            }
        })

    }

    private val deleteTimeListener = object : OnLoqTimeDeleteListener {
        override fun onLoqTimeDeleteClicked(day: BlockedDay) {

            if (loq.blockBlockedDays.contains(day)){
                MaterialDialog(safeActivity).show {
                    title(R.string.delete_loq_time)
                    message(R.string.delete_loq_time_message)
                    positiveButton(R.string.yes){
                        loq.blockBlockedDays.remove(day)
                        editLoqAdapter.updateData(loq.blockBlockedDays)
                        if (loq.blockBlockedDays.isEmpty()){
                            viewModel.removeLoq(this@EditLoqFragment, loq)
                        }
                        else {
                            viewModel.updateLoq(this@EditLoqFragment, loq)
                        }
                    }
                    negativeButton(R.string.no)
                }
            }
        }
    }

}