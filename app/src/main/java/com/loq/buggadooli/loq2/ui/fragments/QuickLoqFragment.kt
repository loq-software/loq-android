package com.loq.buggadooli.loq2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.inflateTo

class QuickLoqFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.activity_quick_loq, container)
}
