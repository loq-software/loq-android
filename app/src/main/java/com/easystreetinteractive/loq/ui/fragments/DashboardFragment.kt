package com.easystreetinteractive.loq.ui.fragments

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.*

import com.easystreetinteractive.loq.loqer.CheckForLoqService
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.ui.activities.MainActivity
import com.easystreetinteractive.loq.ui.adapters.LoqSelectionAdapter
import com.easystreetinteractive.loq.ui.listeners.LoqSelectionEditListener
import com.easystreetinteractive.loq.ui.viewmodels.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var loqAdapter: LoqSelectionAdapter
    private var initialized = false
    private var lockPin: String = ""
    private var mLockService: CheckForLoqService? = null
    private var mServiceIntent: Intent? = null

    private val dashboardViewModel by sharedViewModel<DashboardViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_dashboard, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLockBlock()
        startLockService()
        btnAddToLoq.setOnClickListener { safeActivity.addFragment(fragment = EasyLoqFragment()) }
        btnQuickAdd.setOnClickListener { safeActivity.addFragment(fragment = QuickLoqFragment()) }

        dashboardViewModel.loqsRecieved.observe(this, Observer { event ->
            progressLayout.hide()
            val items = event.getContentIfNotHandled()
            items?.let {
                if (it.isNotEmpty()) {
                    layoutManager = LinearLayoutManager(safeActivity)
                    listCurrentLoqs.layoutManager = layoutManager
                    loqAdapter = LoqSelectionAdapter(it)
                    loqAdapter.loqSelectionListener = editLockListener
                    listCurrentLoqs.adapter = loqAdapter
                }
                initialized = true
            }

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLayout.show()
        dashboardViewModel.getLoqs(view)
    }

    private fun initLockBlock() {
       // lockPin = Utils.INSTANCE.getChildLoqPin(safeActivity)//todo: 7/25/19 Handle this
        if (!lockPin!!.isEmpty()) {
            childLock!!.visibility = View.VISIBLE
        }
        btnUnlock!!.setOnClickListener {
            if (txtPin!!.text.toString() == lockPin) {
                childLock!!.visibility = View.GONE
            }
        }
    }

    private fun startLockService() {
      /*  mLockService = CheckForLoqService()
        if (isMyServiceRunning(mLockService!!.javaClass)) {
            safeActivity.stopService(Intent(safeActivity, mLockService!!.javaClass))
            safeActivity.startService(Intent(safeActivity, mLockService!!.javaClass))
        } else {
            mServiceIntent = Intent(safeActivity, mLockService!!.javaClass)
            val pintent = PendingIntent.getService(safeActivity, 0, mServiceIntent!!, 0)
            val alarm = safeActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, 1000, pintent)
        }*/
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = safeActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    private val editLockListener: LoqSelectionEditListener = object: LoqSelectionEditListener {

        override fun onLoqSelectionEditListenerClicked(loq: BlockedApplication, index: Int) {
           // Utils.INSTANCE.editLoq = loq todo: 7/25/19 Handle this
            /*val fragment = EditLoqFragment()
                    .apply {
                        val bundle = bundleOf(Constants.LOQ_INDEX to index)
                        arguments = bundle
                    }
            safeActivity.addFragment(fragment = fragment)*/
        }
    }
}
