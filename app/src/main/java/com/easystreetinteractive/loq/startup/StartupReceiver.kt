package com.easystreetinteractive.loq.startup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.easystreetinteractive.loq.loqer.CheckForLoqService
import com.easystreetinteractive.loq.ui.activities.MainActivity
import java.lang.Exception

class StartupReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action?: return
        if (action == Intent.ACTION_BOOT_COMPLETED){
           /* try {
                val loqServiceIntent = CheckForLoqService.getIntent(context)
                loqServiceIntent?.let {
                    context.startService(it)
                }
            }
            catch (exception: Exception){
                exception.printStackTrace()
            }*/
            val loqIntent = Intent(context, MainActivity::class.java)
            loqIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(loqIntent)
        }

    }
}