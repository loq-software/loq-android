package com.easystreetinteractive.loq.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.toast
import com.easystreetinteractive.loq.pin.PinManager
import org.koin.android.ext.android.inject

class PinDialog: DialogFragment() {

    companion object{

        private const val TAG = "PinDialog"

        fun show(activity: FragmentActivity){

            val dialog = PinDialog().apply {
                isCancelable = false
            }

            dialog.show(activity.supportFragmentManager, TAG)

        }
    }

    private val pinManager by inject<PinManager>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialDialog(activity!!).show {
            cancelable(false)
            cancelOnTouchOutside(false)
            noAutoDismiss()
            input(inputType = InputType.TYPE_CLASS_NUMBER){ _, charSequence ->
                val valid = pinManager.validatePin(charSequence.toString())
                if (valid){
                    dismiss()
                    context.toast("Pin Accepted")
                }
                else{
                    context.toast("Pin Invalid")
                }
            }

            title(R.string.pin_dialog_title)
            message(R.string.pin_dialog_message)
            positiveButton(R.string.submit)
            negativeButton(R.string.dismiss){
                activity?.finish()
            }
        }
    }
}

