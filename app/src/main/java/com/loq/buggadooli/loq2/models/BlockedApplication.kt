package com.loq.buggadooli.loq2.models

data class BlockedApplication(var id: String = "", val userId: String, val applicationName: String, val packageName: String, val blockBlockedDays: List<BlockedDay>)
