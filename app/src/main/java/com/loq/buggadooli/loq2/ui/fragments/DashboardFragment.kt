package com.loq.buggadooli.loq2.ui.fragments

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.loq.buggadooli.loq2.loqer.LockService
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.utils.Utils
import com.loq.buggadooli.loq2.ui.adapters.LoqSelectionAdapter
import com.loq.buggadooli.loq2.ui.listeners.LoqSelectionEditListener
import kotlinx.android.synthetic.main.fragment_dashboard.*

import java.util.Calendar

class DashboardFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var loqAdapter: LoqSelectionAdapter
    private var initialized = false
    private var lockPin: String? = null
    private var mLockService: LockService? = null
    private var mServiceIntent: Intent? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_dashboard, container)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLockBlock()
        initLoqList()
        startLockService()
        btnAddToLoq.setOnClickListener { safeActivity.addFragment(fragment = SetupFragment()) }
        btnQuickAdd.setOnClickListener { safeActivity.addFragment(fragment = QuickLoqFragment()) }
    }

    private fun initLockBlock() {
        lockPin = Utils.INSTANCE.getChildLoqPin(safeActivity)
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
        mLockService = LockService()
        if (isMyServiceRunning(mLockService!!.javaClass)) {
            safeActivity.stopService(Intent(safeActivity, mLockService!!.javaClass))
            safeActivity.startService(Intent(safeActivity, mLockService!!.javaClass))
        } else {
            mServiceIntent = Intent(safeActivity, mLockService!!.javaClass)
            val pintent = PendingIntent.getService(safeActivity, 0, mServiceIntent!!, 0)
            val alarm = safeActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, 1000, pintent)
        }
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

    private fun initLoqList() {
        val currentLoqs = Utils.INSTANCE.readLoqsFromFile(safeActivity)
        if (!currentLoqs.isEmpty()) {
            layoutManager = LinearLayoutManager(safeActivity)
            listCurrentLoqs.layoutManager = layoutManager
            loqAdapter = LoqSelectionAdapter(currentLoqs)
            loqAdapter.loqSelectionListener = editLockListener
            listCurrentLoqs.adapter = loqAdapter
        }
        initialized = true
    }

    private val editLockListener: LoqSelectionEditListener = object: LoqSelectionEditListener {

        override fun onLoqSelectionEditListenerClicked(loq: Loq, index: Int) {
            Utils.INSTANCE.editLoq = loq
            val fragment = EditLoqFragment()
                    .apply {
                        arguments?.putInt(Constants.LOQ_INDEX,index)
                    }
            safeActivity.addFragment(fragment = fragment)
        }
    }
}
