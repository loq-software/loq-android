package com.easystreetinteractive.loq.startup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.easystreetinteractive.loq.ui.activities.MainActivity

class StartupReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action?: return
        if (action == Intent.ACTION_BOOT_COMPLETED){
            val loqIntent = Intent(context, MainActivity::class.java)
            loqIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(loqIntent)
        }

    }
}