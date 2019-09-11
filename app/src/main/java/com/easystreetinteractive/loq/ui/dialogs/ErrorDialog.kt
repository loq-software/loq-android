package com.easystreetinteractive.loq.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.constants.Constants
import java.lang.NullPointerException

class ErrorDialog: DialogFragment() {


    companion object{

        private val TAG = "ErrorDialog"

        fun show(activity: FragmentActivity, error: Throwable){
            val dialog = ErrorDialog().apply {
                arguments = bundleOf(Constants.ERROR_MESSAGE to error.message)
            }

            dialog.show(activity.supportFragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val context = activity ?: throw IllegalStateException("Not attached")
        val message = arguments?.getString(Constants.ERROR_MESSAGE)?: throw NullPointerException("Error message is null")

        return MaterialDialog(context).show {
            title(R.string.error)
            message(text = message)
            positiveButton(R.string.dismiss)
            onDismiss {
                this.dismiss()
            }
        }
    }
}