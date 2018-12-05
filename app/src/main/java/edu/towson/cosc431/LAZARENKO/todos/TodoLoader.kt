package edu.towson.cosc431.LAZARENKO.todos

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.BitmapRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import java.util.*
import kotlin.concurrent.thread

class TodoLoader {

    private var hadError = false
    private lateinit var db: TodosDatabase
    private var imgBitmap: Bitmap? = null

    companion object {
        val URL = "https://my-json-server.typicode.com/rvalis-towson/todos_api/todos"
        val TAG = "TodoLoader"

    }

    data class IntermediateTodo(val id:Int, val title:String, val contents:String,
                                val completed: Boolean, val image_url:String)


    inner class TodosJsonArrayListener : ParsedRequestListener<List<IntermediateTodo>> {

        override fun onResponse(response: List<IntermediateTodo>) {
            db.clearTodosTable()
            for (interTodo in response) {
                val todo = convertIntermediateToTodo(interTodo)
                db.addTodo(todo)
                getImageBitmap(interTodo.image_url, todo)
            }
        }

        private fun convertIntermediateToTodo(intermediateTodo: TodoLoader.IntermediateTodo) : Todo {
            val dateNTime = Calendar.getInstance().time
            val dueDateInstance = MainActivity.dueDateFormatter.format(dateNTime)
            val dateCreatedInstance = MainActivity.dateCreatedFormatter.format(dateNTime)

            return Todo(intermediateTodo.title, intermediateTodo.contents, intermediateTodo.completed,
                    false, null, dateCreatedInstance, dueDateInstance)
        }

        override fun onError(anError: ANError?) {
            Log.d(TAG, "Error with getting network output")
            Log.e(TAG, anError.toString())
            hadError = true
        }

    }

    fun fetchAvatar(url: String) {
        AndroidNetworking.get(url)
                .setBitmapMaxHeight(128)
                .setBitmapMaxWidth(128)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(object: BitmapRequestListener {
                    override fun onResponse(response: Bitmap?) {
                        Log.d(TAG, "Got a response image Bitmap")
                        imgBitmap = response
                    }

                    override fun onError(anError: ANError?) {
                        Log.d(TAG, "Failed to fetch avatar")
                    }

                })
    }

    fun getImageBitmap(url:String, todo:Todo) {
        object : Thread(){
            override fun run() {
                fetchAvatar(url)
                while (imgBitmap == null) {
                    Thread.sleep(50)
                    Log.d(TAG, "Image Bitmap was NULL")
                }
                todo.image = imgBitmap
                db.updateTodo(todo)
            }
        }.start()
        //return imgBitmap!!.copy(imgBitmap!!.config, false)
    }

    /*
     *   First thing to be called.
     *   Returns true if succeeded, false otherwise.
     */
    fun loadTodo(context: Context?) : Boolean {
        if(context == null){
            Log.d(TAG, "Context was null in loadTodo()")
            hadError = true
            return !hadError
        }

        this.db = TodosDatabase(context)

        AndroidNetworking.initialize(context)
        AndroidNetworking.enableLogging()

        fetchTodosFromNetwork()

        return !hadError
    }

    /**
     * Do the network call here
     */
    private fun fetchTodosFromNetwork() {
        Log.d("TodoLoader", "Working on loading the image")

        AndroidNetworking.get(URL)
                .build()
                .getAsObjectList(IntermediateTodo::class.java, TodosJsonArrayListener())

        // Delay download by 10 seconds
        try {
            Thread.sleep(10 * 1000)
        } catch (e: InterruptedException) {
            Log.d(TAG, "Sleeping Thread was interrupted in fetchTodosFromNetork()")
            hadError = true
            e.printStackTrace()
        }
    }

}
