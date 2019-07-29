package com.loq.buggadooli.loq2.ui.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer

import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.ui.activities.MainActivity
import com.loq.buggadooli.loq2.ui.viewmodels.EasyLoqViewModel
import kotlinx.android.synthetic.main.fragment_easy_loq.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.loq.buggadooli.loq2.R


class EasyLoqFragment : Fragment() {

    private val viewModel by viewModel<EasyLoqViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_easy_loq, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(safeActivity,
                R.array.popular_apps, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        appSelectGroup.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.radioBtnPopular) {
                spinner.visibility = View.VISIBLE
            }
            else {
                spinner.visibility = View.INVISIBLE
            }
        }

        btnNext.setOnClickListener {

            when(appSelectGroup.checkedRadioButtonId){
                R.id.radioBtnPopular -> {
                  viewModel.buttonNextForPopularClicked(view)
                }

                R.id.radioBtnCustom -> {
                    safeActivity.addFragment(fragment = CustomLoqFragment())
                }

                else -> {
                    Toast.makeText(safeActivity, "Please make a selection", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.installedPopularApplications.observe(this, Observer { event ->
            val popularApplications = event.getContentIfNotHandled()
            popularApplications?.let {
                val fragment = SetAndForgetFragment()
                val bundle = bundleOf(Constants.APP_NAME to it)
                fragment.arguments = bundle
                safeActivity.addFragment(fragment = fragment)
            }
        })

        val hasStatsPermission = (safeActivity as MainActivity).permissionsManager.hasUsageStatsPermission()
        if (!hasStatsPermission){
            val builder = AlertDialog.Builder(safeActivity)
            builder.setMessage("Make sure Usage Access is granted to Loq for the app to work!")
                    .setCancelable(false)
                    .setPositiveButton("OK") { p0, p1 ->
                        val intent = Intent(ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent)
                    }
            val alert = builder.create()
            alert.show()
        }
    }

}
