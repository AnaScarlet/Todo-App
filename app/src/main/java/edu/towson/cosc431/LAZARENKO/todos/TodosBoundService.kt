package edu.towson.cosc431.LAZARENKO.todos

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class TodosBoundService : Service() {

    private var working = false
    private lateinit var task: TodoLoaderAsyncTask
    private lateinit var threadpool: ThreadPoolExecutor

    companion object {
        val TAG = "TodosBoundService"
        val CHANNEL_ID = "TodosChannel"
        val NOTIF_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startWorking()
        return START_NOT_STICKY
    }

    fun isWorking(): Boolean {
        return working
    }

    fun startWorking() {
        Log.d(TAG, "Service started working. Creating thread pool...")
        working = true
        // Create a thread pool of 2
        threadpool = Executors.newFixedThreadPool(2) as ThreadPoolExecutor
        task = TodoLoaderAsyncTask(WeakReference(this))
        task.executeOnExecutor(threadpool)
    }

    fun stopWorking() {
        working = false
        // Kill the thread
        task.cancel(true)
        threadpool.shutdown()
        Log.d(TAG, "Done!")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWorking()
        Log.d(TAG, "Destroying ExampleBoundService")
    }

}