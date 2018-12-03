package edu.towson.cosc431.LAZARENKO.todos

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import java.util.*

class ImageLoader {

    // used to toggle between error and success
    // DEMO PURPOSES ONLY!!!!
    private val todosList: MutableList<Todo> = mutableListOf()
    private var hadError = false

    companion object {
        val URL = "https://my-json-server.typicode.com/rvalis-towson/todos_api/todos"
        val TAG = "ImageLoader"
    }

    data class IntermediateTodo(val id:Int, val title:String, val contents:String,
                                val completed: Boolean, val image_url:String)


    inner class TodosJsonArrayListener : ParsedRequestListener<List<IntermediateTodo>> {

        override fun onResponse(response: List<IntermediateTodo>) {
            for (interTodo in response) {
                todosList.add(convertIntermediateToTodo(interTodo))
            }
        }

        private fun convertIntermediateToTodo(intermediateTodo: ImageLoader.IntermediateTodo) : Todo {
            val dateNTime = Calendar.getInstance().time
            val dueDateInstance = MainActivity.dueDateFormatter.format(dateNTime)
            val dateCreatedInstance = MainActivity.dateCreatedFormatter.format(dateNTime)
            /*
            Log.d(TAG, "Title: " + intermediateTodo.title + " Contents: " + intermediateTodo.contents
                + " isCompleted: " + intermediateTodo.completed + " image URL: " + intermediateTodo.image_url
                + " date created: " + dateCreatedInstance + " due date: " + dueDateInstance)
            */
            return Todo(intermediateTodo.title, intermediateTodo.contents, intermediateTodo.completed,
                    false, intermediateTodo.image_url, dateCreatedInstance, dueDateInstance)
        }

        override fun onError(anError: ANError?) {
            Log.d(TAG, "Error with getting network output")
            Log.e(TAG, anError.toString())
            hadError = true
        }

    }

    fun loadTodo(context: Context?): List<Todo>? {
        if(context == null){
            Log.d(TAG, "Context was null in loadTodo()")
            hadError = true
            return null
        }

        AndroidNetworking.initialize(context)
        AndroidNetworking.enableLogging()

        fetchTodosFromNetwork()
        return if (hadError) {
            //Log.d(TAG, "Some error was set off while getting todos list from network")
            null
        } else {
            // Returns a lsit of Todos
            todosList.toList()
        }
    }

    /**
     * Do the network call here
     */
    private fun fetchTodosFromNetwork() {
        Log.d("ImageLoader", "Working on loading the image")

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
