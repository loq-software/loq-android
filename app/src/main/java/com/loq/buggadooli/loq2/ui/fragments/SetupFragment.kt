package com.loq.buggadooli.loq2.ui.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import kotlinx.android.synthetic.main.fragment_setup.*

class SetupFragment : Fragment() {

    private var chooseApps = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_setup, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(safeActivity,
                R.array.popular_apps, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        appSelectGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioBtnPopular) {
                chooseApps = false
                spinner.visibility = View.VISIBLE
            } else if (checkedId == R.id.radioBtnCustom) {
                chooseApps = true
                spinner.visibility = View.INVISIBLE
            }
        }

        btnNext!!.setOnClickListener {
            val fragment = LockFragment().apply {
                arguments?.putBoolean("chooseApps", chooseApps)
            }
            safeActivity.addFragment(fragment = fragment)
        }
    }

}
