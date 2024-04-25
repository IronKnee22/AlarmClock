package com.example.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("Alarm", "Alarm se spustil!")

        val gentle = intent?.getBooleanExtra("gentle", false)


        val upozorneniIntent = Intent(context, VypnutiBudiku::class.java)
        upozorneniIntent.putExtra("gentle", gentle)
        upozorneniIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(upozorneniIntent)

    }
}
