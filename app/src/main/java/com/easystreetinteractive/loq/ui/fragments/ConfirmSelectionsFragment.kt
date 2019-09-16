package com.easystreetinteractive.loq.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.*
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.ui.adapters.DashboardAdapter
import com.easystreetinteractive.loq.ui.viewmodels.ConfirmSelectionsViewModel
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_confirm_selections.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NullPointerException

class ConfirmSelectionsFragment: Fragment() {

    private val viewModel by sharedViewModel<ConfirmSelectionsViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var currentLoqs: List<BlockedApplication>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentLoqs = arguments?.getParcelableArrayList(Constants.CURRENT_LOQS)?: throw NullPointerException("Current loqs are null")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_confirm_selections)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dashBoardAdapter = DashboardAdapter()
                .apply {
                    updateData(currentLoqs)
                }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(safeActivity)
            adapter = dashBoardAdapter
        }

        backButton.setOnClickListener { safeActivity.onBackPressed() }

        finishedButton.setOnClickListener {
            viewModel.finishButtonClicked(
                    safeActivity,
                    currentLoqs,
                    confirmCheckbox.isChecked,
                    pinCheckbox.isChecked,
                    pinView.text.toString(),
                    confirmPinView.text.toString()
            )
        }

        viewModel.onLockedApplicationsSaved.observe(this){ event ->
            val applications = event.getContentIfNotHandled()
            applications?.let {
                safeActivity.toast("Applications saved")
                mainViewModel.loadLoqs(this)
                safeActivity.popAllInBackStack()
                safeActivity.replaceFragment(fragment = DashboardFragment())
            }
        }
    }

    companion object{

        fun newInstance(
                currentLoqs: List<BlockedApplication>
        ): ConfirmSelectionsFragment{
            return ConfirmSelectionsFragment()
                    .apply {
                        arguments = bundleOf(
                                Constants.CURRENT_LOQS to currentLoqs
                        )
                    }
        }
    }
}