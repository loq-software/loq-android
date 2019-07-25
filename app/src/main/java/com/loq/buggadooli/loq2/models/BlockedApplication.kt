package com.loq.buggadooli.loq2.models

data class BlockedApplication(val applicationName: String, val packageName: String, val blockBlockedDays: List<BlockedDay>)