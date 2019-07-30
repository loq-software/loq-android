package com.easystreetinteractive.loq.ui.listeners

import com.easystreetinteractive.loq.models.BlockedApplication

interface LoqSelectionEditListener{

    fun onLoqSelectionEditListenerClicked(loq: BlockedApplication, index: Int)
}