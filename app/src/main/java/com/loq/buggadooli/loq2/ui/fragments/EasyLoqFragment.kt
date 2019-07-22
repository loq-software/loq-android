package com.loq.buggadooli.loq2.ui.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.ui.viewmodels.EasyLoqViewModel
import kotlinx.android.synthetic.main.fragment_setup.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EasyLoqFragment : Fragment() {

    private var choosenAppName = ""
    private var checkboxSelected = false
    private val viewModel by viewModel<EasyLoqViewModel>()

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
                spinner.visibility = View.VISIBLE
            } else if (checkedId == R.id.radioBtnCustom) {
                spinner.visibility = View.INVISIBLE
            }
            checkboxSelected = true
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                choosenAppName = parent.getItemAtPosition(pos) as String
            }

        }


        btnNext!!.setOnClickListener {

            when(appSelectGroup.checkedRadioButtonId){
                R.id.radioBtnPopular -> {
                    val appName = choosenAppName
                    if (appName.isNotBlank() && ! appName.contentEquals("See apps")){
                        viewModel.checkIfUserHasAppOnDevice(appName, this)
                    }
                    else{
                        Toast.makeText(safeActivity, "Please select an app from the list", Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.radioBtnCustom -> {
                    //safeActivity.addFragment(fragment = SetAndForgetFragment())// todo: Start CustomLoqFragment
                    Toast.makeText(safeActivity, "Coming soon", Toast.LENGTH_LONG).show()
                }

                else -> {
                    Toast.makeText(safeActivity, "Please make a selection", Toast.LENGTH_LONG).show()

                }

            }
        }

        viewModel.onUserHasApplication.observe(this, Observer { event ->
            val hasApplication = event.getContentIfNotHandled()
            hasApplication?.let {
                val name = choosenAppName
                if (it){
                    val fragment = SetAndForgetFragment()
                    val bundle = bundleOf(Constants.APP_NAME to name)
                    fragment.arguments = bundle
                    safeActivity.addFragment(fragment = fragment)
                }
                else{
                    Toast.makeText(safeActivity, "You don't have $name installed", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

}
