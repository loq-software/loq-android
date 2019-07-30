package com.easystreetinteractive.loq.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.easystreetinteractive.loq.R

import com.easystreetinteractive.loq.extensions.inflateTo
import com.easystreetinteractive.loq.extensions.popAllInBackStack
import com.easystreetinteractive.loq.extensions.replaceFragment
import com.easystreetinteractive.loq.extensions.safeActivity
import kotlinx.android.synthetic.main.fragment_child_lock.*

class ChildLockFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_child_lock, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEnter.setOnClickListener { saveChildLock() }
    }

    private fun saveChildLock() {
        val pinCode = txtPinCode!!.text.toString()
        val pinCode2 = txtPinCode2!!.text.toString()
        if (pinCode == pinCode2) {
            if (!pinCode.isEmpty()) {
               // Utils.INSTANCE.saveChildLoqPin(safeActivity, pinCode)//todo: 7/25/19 handle this
            }
            safeActivity.popAllInBackStack()
            safeActivity.replaceFragment(fragment = CongratsFragment())
        } else {
            Toast.makeText(safeActivity,
                    "Pin codes do not match!",
                    Toast.LENGTH_LONG).show()
        }
    }
}
