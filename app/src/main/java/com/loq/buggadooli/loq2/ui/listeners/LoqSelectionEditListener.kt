package com.loq.buggadooli.loq2.ui.listeners

import com.loq.buggadooli.loq2.models.BlockedApplication

interface LoqSelectionEditListener{

    fun onLoqSelectionEditListenerClicked(loq: BlockedApplication, index: Int)
}