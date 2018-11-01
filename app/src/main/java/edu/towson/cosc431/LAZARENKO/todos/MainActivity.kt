package edu.towson.cosc431.LAZARENKO.todos

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : FragmentActivity(), IController {

    companion object {
        val TODO_REQUEST_CODE:Int = 50;
        val TAG = "MAIN"
    }

    private val todosList = mutableListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        populateTodosList()

        val todosAdapter = TodosAdapter(todosList, this)
        recyclerView.adapter = todosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener{ launchNewTodoActivity() }
    }

    override fun launchNewTodoActivity() {
        intent = Intent()
        intent.component = ComponentName(this, NewTodoActivity::class.java)
        this.startActivityForResult(intent, TODO_REQUEST_CODE)
    }

    override fun addTodo(todo:Todo) {
        todosList.add(todo)
        recyclerView.adapter.notifyItemInserted(todosList.lastIndex)
    }

    override fun deleteTodo(indx: Int) {
        if (indx <= todosList.lastIndex)
            todosList.removeAt(indx)
        else
            throw InvalidParameterException()
    }

    override fun toggleCompleted(indx: Int) {
        if (indx <= todosList.lastIndex)
            todosList[indx].isCompleted = ! todosList[indx].isCompleted
        else
            throw InvalidParameterException()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            TODO_REQUEST_CODE -> {
                Log.d(TAG, "Result was OK :)")
                if (data != null) {
                    val title:String = data.getStringExtra(NewTodoActivity.TITLE)
                    val contents:String = data.getStringExtra(NewTodoActivity.CONTENTS)
                    val isCompleted:Boolean = data.getBooleanExtra(NewTodoActivity.IS_COMPLETED, false)
                    val image:String = data.getStringExtra(NewTodoActivity.IMAGE)                   // TODO: will need to be changed
                    val dateCreated = Calendar.getInstance().time
                    val dueDate = data.getStringExtra(NewTodoActivity.DUE_DATE)


                    val newTodo = Todo(title, contents, isCompleted, image, dateCreated, dueDate)
                    addTodo(newTodo)
                    Log.d(TAG, newTodo.toString())
                }
                else
                    Log.d(TAG, "Result Intent object was NULL")
            }
            else -> Log.d(TAG, "Result was CANCELLED")
        }
    }

    private fun populateTodosList() {
        val dueDate = Calendar.getInstance(TimeZone.getTimeZone("Australia/ACT")).time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        var currentTime = Calendar.getInstance().time
        (1..10).forEach {
            todosList.add(Todo("Todo"+it, "Do Todo"+it,false, "MyTodo", currentTime, formatter.format(dueDate)))
            Log.d("TODOS_LIST", todosList[it-1].toString())
            Thread.sleep(1000)
            currentTime = Calendar.getInstance().time
        }
    }
}
