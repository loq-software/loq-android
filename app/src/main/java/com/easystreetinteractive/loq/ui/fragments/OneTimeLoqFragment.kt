package com.easystreetinteractive.loq.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.afollestad.materialdialogs.list.listItems
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.TimeType
import com.easystreetinteractive.loq.databinding.FragmentOneTimeLoqBinding
import com.easystreetinteractive.loq.extensions.getAppName

import com.easystreetinteractive.loq.extensions.inflateWithBinding
import com.easystreetinteractive.loq.extensions.safeActivity
import com.easystreetinteractive.loq.extensions.observe
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import com.easystreetinteractive.loq.ui.viewmodels.OneTimeLoqViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_one_time_loq.*

class OneTimeLoqFragment : Fragment() {

    private val viewModel by sharedViewModel<OneTimeLoqViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var binding: FragmentOneTimeLoqBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View{
        binding = inflater.inflateWithBinding(R.layout.fragment_one_time_loq, container)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        applicationView.setOnClickListener {
            startApplicationPicker()
        }

        dateView.setOnClickListener {
            startDatePicker()
        }

        btnStartTime.setOnClickListener { startTimePicker(TimeType.FOR_START) }
        btnEndTime.setOnClickListener { startTimePicker(TimeType.FOR_END) }

        btnBack.setOnClickListener { safeActivity.onBackPressed() }
        btnFinished.setOnClickListener { viewModel.onSubmitButtonClicked(safeActivity) }

        viewModel.loadApplications(safeActivity)
        viewModel.loadDefaultDate()

        viewModel.addedBlockedApplication.observe(this){ event ->
            event.getContentIfNotHandled()?: return@observe
            mainViewModel.loadLoqs(safeActivity)
            safeActivity.onBackPressed()
        }
    }

    private fun startTimePicker(type: TimeType) {
        MaterialDialog(safeActivity).show {
            timePicker(show24HoursView = false) { _, datetime ->
                viewModel.setTime(datetime, type)
            }
        }
    }

    private fun startApplicationPicker(){
        MaterialDialog(safeActivity).show {
            val list = viewModel.applications.value?.map { it.getAppName(safeActivity.packageManager) }?: emptyList()
            listItems(items = list) { _, index, _ ->
                viewModel.setSelectedApplication(index)
            }
        }
    }

    private fun startDatePicker(){
        MaterialDialog(safeActivity).show {
            datePicker(currentDate = viewModel.dateForCalender) { _, date ->
                viewModel.setDate(date)
            }
        }

    }
}
