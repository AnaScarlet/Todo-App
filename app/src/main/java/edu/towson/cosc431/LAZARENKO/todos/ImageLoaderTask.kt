package edu.towson.cosc431.LAZARENKO.todos

import android.app.PendingIntent
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.lang.ref.WeakReference

class ImageResAndIndex(var index: Int, var resId: Int)

class ImageLoaderAsyncTask(var act: WeakReference<TodosBoundService>) : AsyncTask<ImageResAndIndex, Int, List<Todo>?>() {

    override fun doInBackground(vararg resId: ImageResAndIndex?): List<Todo>? {
        Log.d("AsyncTask", "Loading images from background thread...")
        val loader = ImageLoader()
        return loader.loadTodo(act.get())
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(todosList: List<Todo>?) {
        super.onPostExecute(todosList)
        if (todosList != null) {
            act.get()?.putData(todosList)
            notifyUser("Your Todos are loaded.")
        }
        else
            notifyUser("Empty Todo loaded :( ")
    }

    fun notifyUser(msg: String) {
        val context = act.get()

        val mainActivityIntent = Intent(context, MainActivity::class.java)  // For sending out data...?
        val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)

        if (context != null) {
            val result = LocalBroadcastManager
                    .getInstance(context)
                    .sendBroadcast(mainActivityIntent) // Returns true if someone received the broadcast

            if (!result) {
                // Show notification
                val notification = NotificationCompat.Builder(context, TodosIntentService.CHANNEL_ID) // ChannelId - notifications can be grouped into channels
                        .setContentTitle(msg)
                        .setContentText("Click here to check them out!")
                        .setSmallIcon(android.R.drawable.ic_input_get)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .build() // Returns the actual notification

                NotificationManagerCompat.from(context).notify(TodosIntentService.NOTIF_ID, notification)
            }
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        notifyUser("Todo images download was cancelled")
        Log.d(MainActivity.TAG, "Download Cancelled")
    }

}