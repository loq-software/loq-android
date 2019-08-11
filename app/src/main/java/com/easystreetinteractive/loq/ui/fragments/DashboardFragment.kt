package com.easystreetinteractive.loq.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.*

import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.ui.activities.MainActivity
import com.easystreetinteractive.loq.ui.adapters.LoqSelectionAdapter
import com.easystreetinteractive.loq.ui.listeners.LoqSelectionEditListener
import com.easystreetinteractive.loq.ui.viewmodels.DashboardViewModel
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var loqAdapter: LoqSelectionAdapter
    private var initialized = false
    private var lockPin: String = ""

    private val dashboardViewModel by sharedViewModel<DashboardViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_dashboard, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLockBlock()
        btnAddToLoq.setOnClickListener { safeActivity.addFragment(fragment = EasyLoqFragment()) }
        btnQuickAdd.setOnClickListener { safeActivity.addFragment(fragment = QuickLoqFragment()) }

        layoutManager = LinearLayoutManager(safeActivity)
        recyclerView.layoutManager = layoutManager
        loqAdapter = LoqSelectionAdapter()

        mainViewModel.onLoqsLoaded.observe(this, Observer { event ->
            progressLayout.hide()
            val items = event.peekContent()
            if (items.isNotEmpty()) {
                loqAdapter.updateData(items)
                loqAdapter.loqSelectionListener = editLockListener
                recyclerView.adapter = loqAdapter
            }
            initialized = true

        })

        dashboardViewModel.showToast.observe(this, Observer { event ->
            val message = event.getContentIfNotHandled()
            message?.let {
                safeActivity.toast(it)
            }
        })

        val hasStatsPermission = (safeActivity as MainActivity).permissionsManager.hasUsageStatsPermission()
        if (!hasStatsPermission){
            val builder = AlertDialog.Builder(safeActivity)
            builder.setMessage("Make sure Usage Access is granted to Loq for the app to work!")
                    .setCancelable(false)
                    .setPositiveButton("OK") { p0, p1 ->
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent)
                    }
            val alert = builder.create()
            alert.show()
        }

        progressLayout.show()
    }


    private fun initLockBlock() {
       // lockPin = Utils.INSTANCE.getChildLoqPin(safeActivity)//todo: 7/25/19 Handle this
        if (lockPin.isNotEmpty()) {
            childLock!!.visibility = View.VISIBLE
        }
        btnUnlock.setOnClickListener {
            if (txtPin!!.text.toString() == lockPin) {
                childLock!!.visibility = View.GONE
            }
        }
    }

    private val editLockListener: LoqSelectionEditListener = object: LoqSelectionEditListener {

        override fun onLoqSelectionEditListenerClicked(loq: BlockedApplication, index: Int) {
            val fragment = EditLoqFragment()
                    .apply {
                        val bundle = bundleOf(Constants.LOQ to loq)
                        arguments = bundle
                    }
            safeActivity.addFragment(fragment = fragment)
        }
    }
}
