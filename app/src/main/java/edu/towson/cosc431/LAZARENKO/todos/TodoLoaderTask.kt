package edu.towson.cosc431.LAZARENKO.todos

import android.app.PendingIntent
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.lang.ref.WeakReference

class ImageRes(var index: Int, var resId: Int)

class TodoLoaderAsyncTask(var act: WeakReference<TodosBoundService>) : AsyncTask<ImageRes, Int, Boolean>() {

    override fun doInBackground(vararg imgRes: ImageRes): Boolean {
        Log.d("AsyncTask", "Loading images from background thread...")
        val loader = TodoLoader()
        return loader.loadTodo(act.get())
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(succeeded: Boolean) {
        super.onPostExecute(succeeded)
//        if (todosList != null)
//            act.get()?.putData(todosList)
        if (succeeded)
            notifyUser("Your Todos are loaded.")
        else
            notifyUser("An Error occurred while loading your Todos :( ")
    }

    fun notifyUser(msg: String) {
        val context = act.get()

        val broadcastReceiverIntent = Intent()
        broadcastReceiverIntent.setAction(Intent.ACTION_RUN)
        broadcastReceiverIntent.addCategory(Intent.CATEGORY_DEFAULT)

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)

        if (context != null) {
            val result = LocalBroadcastManager
                    .getInstance(context)
                    .sendBroadcast(broadcastReceiverIntent) // Returns true if someone received the broadcast
            Log.d("TodoLoaderTask", result.toString())
            if (!result) {
                // Show notification
                val notification = NotificationCompat.Builder(context, TodosBoundService.CHANNEL_ID) // ChannelId - notifications can be grouped into channels
                        .setContentTitle(msg)
                        .setContentText("Click here to check them out!")
                        .setSmallIcon(android.R.drawable.ic_input_get)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .build() // Returns the actual notification

                NotificationManagerCompat.from(context).notify(TodosBoundService.NOTIF_ID, notification)
            }
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        notifyUser("Todo images download was cancelled")
        Log.d(MainActivity.TAG, "Download Cancelled")
    }

}