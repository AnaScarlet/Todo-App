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

    private lateinit var db:IDatabase
    private val todosList = mutableListOf<Todo>()
    private val todosListFragment: ITodosList = TodosListFragment()

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

        db = TodosDatabase(this)

        populateTodosList()

        todosList.addAll(db.getTodos())

        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, todosListFragment as TodosListFragment)
                .commit()

    }

    private fun drawCompletedFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.colorAccent))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.buttonColor))
        todosListFragment.setListType(TodosListFragment.COMPLETED_TYPE)
        todosListFragment.updateTodosList()
    }

    private fun drawActiveFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.colorAccent))
        all_tab.setTextColor(resources.getColor(R.color.buttonColor))
        todosListFragment.setListType(TodosListFragment.ACTIVE_TYPE)
        todosListFragment.updateTodosList()
    }

    private fun drawAllFragment() {
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.colorAccent))
        todosListFragment.setListType(TodosListFragment.ALL_TYPE)
        todosListFragment.updateTodosList()
    }

    override fun addTodo(todo:Todo) {
        db.addTodo(todo)
        todosList.clear()
        todosList.addAll(db.getTodos())
        todosListFragment.updateTodosList()
    }

    override fun deleteTodo(indx: Int) {
        if (indx <= todosList.lastIndex) {
            db.deleteTodo(todosList[indx])
            todosList.clear()
            todosList.addAll(db.getTodos())
            todosListFragment.updateTodosList()
        }
        else
            throw InvalidParameterException()
    }

    override fun toggleCompleted(indx: Int) {
        if (indx <= todosList.lastIndex) {
            todosList[indx].isCompleted = ! todosList[indx].isCompleted
            db.updateTodo(todosList[indx])
            todosList.clear()
            todosList.addAll(db.getTodos())
            todosListFragment.updateTodosList()
        }
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
                    val formatter = SimpleDateFormat("yyyy-MM-dd-hh.mm.ss")

                    val title:String = data.getStringExtra(NewTodoActivity.TITLE)
                    val contents:String = data.getStringExtra(NewTodoActivity.CONTENTS)
                    val isCompleted:Boolean = data.getBooleanExtra(NewTodoActivity.IS_COMPLETED, false)
                    val image:String = data.getStringExtra(NewTodoActivity.IMAGE)                   // TODO: will need to be changed
                    val dateCreated = formatter.format(Calendar.getInstance().time)
                    val dueDate = data.getStringExtra(NewTodoActivity.DUE_DATE)

                    // TODO: way to pass around isDeleted?
                    val newTodo = Todo(title, contents, isCompleted, false, image, dateCreated, dueDate)
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
        var currentTime = "19-11-2018"
        (1..5).forEach {
            db.addTodo(Todo("Active Todo #"+it, "Do Todo"+it,false, false,
                    "MyTodo", currentTime+" 11:0"+it.toString(), formatter.format(dueDate)))
            //Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
        }
        (1..5).forEach {
            db.addTodo(Todo("Completed Todo #"+it, "Do Todo"+it,true, false,
                    "MyTodo", currentTime+" 11:1"+it.toString(), formatter.format(dueDate)))
            //Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
            currentTime = formatter.format(Calendar.getInstance().time)
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
