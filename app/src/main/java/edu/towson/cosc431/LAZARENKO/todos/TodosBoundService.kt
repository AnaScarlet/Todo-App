package edu.towson.cosc431.LAZARENKO.todos

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class TodosBoundService : Service() {

    private var working = false
    private val tasks: MutableList<ImageLoaderAsyncTask> = mutableListOf()
    private var todos: List<Todo> = listOf()

    companion object {
        val TAG = "TodosBoundService"
    }

    inner class TodosServiceBinder() : Binder() {
        fun getService() : TodosBoundService {
            return this@TodosBoundService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return TodosServiceBinder()
    }

    // TODO: personalize for the Todos app
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Received mesage!", Toast.LENGTH_SHORT).show()
        startWorking()
        return START_NOT_STICKY
    }

    fun isWorking(): Boolean {
        return working
    }

    fun fetchData() : List<Todo>{
        return todos.toList()
    }

    fun putData(todos: List<Todo>) {
        this.todos = todos
    }

    fun startWorking() {
        Log.d(TAG, "Bound Service started working. Creating thread pool...")
        working = true
        // Create a thread pool of 2
        val threadpool = Executors.newFixedThreadPool(2) as ThreadPoolExecutor
        val task = ImageLoaderAsyncTask(WeakReference(this))
        task.executeOnExecutor(threadpool)
        tasks.add(task)
    }

    fun stopWorking() {
        working = false
        // Kill the threads
        val iterator = tasks.iterator()
        iterator.forEach {
            it.cancel(true)
        }

        Log.d(TAG, "Done!")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWorking()
        Log.d(TAG, "Destroying ExampleBoundService")
    }

}