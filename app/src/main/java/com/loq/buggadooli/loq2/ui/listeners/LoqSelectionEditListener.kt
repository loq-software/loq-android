package com.loq.buggadooli.loq2.ui.listeners

import com.loq.buggadooli.loq2.models.Loq

interface LoqSelectionEditListener{

    fun onLoqSelectionEditListenerClicked(loq: Loq, index: Int)
}