package edu.towson.cosc431.LAZARENKO.todos

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class TodosIntentService : IntentService("TodosIntentService"){

    companion object {
        val NOTIF_ID = 1
        val CHANNEL_ID = "TODOS CHANNEL"
    }

    override fun onHandleIntent(intent: Intent?) {
        Thread.sleep(10*1000) // Sleep for 10 seconds (in a background thread)
        Log.d("Service", "Service done sleeping")

        // The work is done
        val mainActivityIntent = Intent(this, MainActivity::class.java)  // For sending out data...?
        val pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0)

        val result = LocalBroadcastManager
                .getInstance(this)
                .sendBroadcast(mainActivityIntent) // Returns true if someone received the broadcast

        if (result) {
            // The activity received the event. All is good.
        } else {
            // Show notification
            val notification = NotificationCompat.Builder(this, CHANNEL_ID) // ChannelId - notifications can be grouped into channels
                    .setContentTitle("Your Todo images are loaded.")
                    .setContentText("Click here to check them out!")
                    .setSmallIcon(android.R.drawable.ic_input_get)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .build() // Returns the actual notification

            NotificationManagerCompat.from(this).notify(NOTIF_ID, notification)
        }
    }

}