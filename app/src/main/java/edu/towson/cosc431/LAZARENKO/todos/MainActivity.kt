package edu.towson.cosc431.LAZARENKO.todos

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : FragmentActivity(), IController{

    companion object {
        val TODO_REQUEST_CODE = 50
        val TAG = "MAIN"
    }

    private val todosList = mutableListOf<Todo>()
    private lateinit var todosListFragment: TodosListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        all_tab.setOnClickListener{ drawAllFragment() }
        active_tab.setOnClickListener { drawActiveFragment() }
        completed_tab.setOnClickListener { drawCompletedFragment() }
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.colorAccent))

        button.setOnClickListener{ launchNewTodoActivity() }

        populateTodosList()

        todosListFragment = TodosListFragment()
        todosListFragment.todosList = getTodosList()
        todosListFragment.controller = this
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, todosListFragment)
                .commit()

    }

    private fun drawCompletedFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.colorAccent))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.buttonColor))
        todosListFragment.todosList.clear()
        val newList = getCompletedTodosList()
        newList.forEach {
            todosListFragment.todosList.add(it)
        }
        todosListFragment.notifyDataChanged()
    }

    private fun drawActiveFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.colorAccent))
        all_tab.setTextColor(resources.getColor(R.color.buttonColor))
        todosListFragment.todosList.clear()
        val newList = getActiveTodosList()
        newList.forEach {
            todosListFragment.todosList.add(it)
        }
        todosListFragment.notifyDataChanged()
    }

    private fun drawAllFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.colorAccent))
        todosListFragment.todosList.clear()
        val newList = getTodosList()
        newList.forEach {
            todosListFragment.todosList.add(it)
        }
        todosListFragment.notifyDataChanged()
    }

    override fun addTodo(todo:Todo) {
        todosList.add(todo)
        todosListFragment.todosList.add(todo)
        todosListFragment.notifyItemAdded(todosList.lastIndex)
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

    override fun launchNewTodoActivity() {
        val intent = Intent()
        intent.component = ComponentName(this, NewTodoActivity::class.java)
        this.startActivityForResult(intent, TODO_REQUEST_CODE)
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
                    this.addTodo(newTodo)
                    Log.d(TAG, newTodo.toString())
                    //Log.d(TAG, getTodosList().toString())
                }
                else
                    Log.w(TAG, "Result Intent object was NULL")
            }
            else -> Log.w(TAG, "Result was CANCELLED")
        }
    }

    private fun populateTodosList() {
        val dueDate = Calendar.getInstance(TimeZone.getTimeZone("Australia/ACT")).time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        var currentTime = Calendar.getInstance().time
        (1..5).forEach {
            todosList.add(Todo("Active Todo #"+it, "Do Todo"+it,false, "MyTodo", currentTime, formatter.format(dueDate)))
            Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
            currentTime = Calendar.getInstance().time
        }
        (1..5).forEach {
            todosList.add(Todo("Completed Todo #"+it, "Do Todo"+it,true, "MyTodo", currentTime, formatter.format(dueDate)))
            Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
            currentTime = Calendar.getInstance().time
        }
    }

    override fun getTodosList(): MutableList<Todo> {
        return todosList.toMutableList()     // should be a deep copy
    }

    override fun getActiveTodosList(): MutableList<Todo> {
        val newTodosList = mutableListOf<Todo>()
        for (item in todosList) {
            if ( ! item.isCompleted)
                newTodosList.add(item)
        }
        return newTodosList
    }

    override fun getCompletedTodosList(): MutableList<Todo> {
        val newTodosList = mutableListOf<Todo>()
        for (item in todosList) {
            if (item.isCompleted)
                newTodosList.add(item)
        }
        return newTodosList
    }

}
