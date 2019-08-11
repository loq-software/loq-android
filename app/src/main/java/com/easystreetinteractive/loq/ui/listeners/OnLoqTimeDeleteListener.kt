package com.easystreetinteractive.loq.ui.listeners

import com.easystreetinteractive.loq.models.BlockedDay

interface OnLoqTimeDeleteListener {

    fun onLoqTimeDeleteClicked(day: BlockedDay)
}