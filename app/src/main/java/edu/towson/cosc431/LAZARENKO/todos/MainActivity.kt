package edu.towson.cosc431.LAZARENKO.todos

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
        val dateCreatedFormatter = SimpleDateFormat("yyyy-MM-dd-hh.mm.ss")
        val dueDateFormatter = SimpleDateFormat("MM/dd/yyyy")
    }

    val serviceConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service == null)
                Log.d(TAG, "IBinder object was null")
            val binder = service as TodosBoundService.TodosServiceBinder?
            boundService = binder?.getService()

        }

        override fun onServiceDisconnected(comp: ComponentName?) {
            boundService = null
        }
    }

    var boundService: TodosBoundService? = null
    var serviceIsBound = false
    private lateinit var db:IDatabase
    private val todosList = mutableListOf<Todo>()
    private val todosListFragment: ITodosList = TodosListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        all_tab.setOnClickListener{ drawAllFragment() }
        active_tab.setOnClickListener { drawActiveFragment() }
        completed_tab.setOnClickListener { drawCompletedFragment() }
        completed_tab.setTextColor(resources.getColor(R.color.buttonColor))
        active_tab.setTextColor(resources.getColor(R.color.buttonColor))
        all_tab.setTextColor(resources.getColor(R.color.colorAccent))

        button.setOnClickListener{ launchNewTodoActivity() }

        db = TodosDatabase(this)

        //populateTodosList()
        fetchTodosInBackgroundService()

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

    private fun addTodosList(todos: MutableList<Todo>) {
        for (todo in todos) {
            db.addTodo(todo)
        }
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
                    val title:String = data.getStringExtra(NewTodoActivity.TITLE)
                    val contents:String = data.getStringExtra(NewTodoActivity.CONTENTS)
                    val isCompleted:Boolean = data.getBooleanExtra(NewTodoActivity.IS_COMPLETED, false)
                    val image:String = data.getStringExtra(NewTodoActivity.IMAGE)                   // TODO: will need to be changed
                    val dateCreated = dateCreatedFormatter.format(Calendar.getInstance().time)
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
        var currentTime = "19-11-2018"
        (1..5).forEach {
            db.addTodo(Todo("Active Todo #"+it, "Do Todo"+it,false, false,
                    "MyTodo", currentTime+" 11:0"+it.toString(), dueDateFormatter.format(dueDate)))
            //Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
        }
        (1..5).forEach {
            db.addTodo(Todo("Completed Todo #"+it, "Do Todo"+it,true, false,
                    "MyTodo", currentTime+" 11:1"+it.toString(), dueDateFormatter.format(dueDate)))
            //Log.d(TAG+":TODOS_LIST", todosList[it-1].toString())
            currentTime = dateCreatedFormatter.format(Calendar.getInstance().time)
        }
    }

    private fun fetchTodosInBackgroundService() {
        Log.d(TAG, serviceIsBound.toString())
        Log.d(TAG, boundService!!.isWorking().toString())
        // will set boundService if succeeded
        if (serviceIsBound) {
            if (boundService == null)
                throw NullBoundServiceException() // shouldn't happen....
            else {
                if (!boundService!!.isWorking()) {
                    val newTodosList = boundService!!.fetchData().toMutableList()
                    for (todo in newTodosList)
                        Log.d(TAG, todo.toString())
                    addTodosList(newTodosList)
                }
                else
                    Log.w(TAG, "Bound service was still working when data was requested!")
            }
        }

        Log.i("MAIN", "Background Bound Service started")
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Make sure Android version is compatible
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(TodosIntentService.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(IntentService.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
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

    override fun onResume() {
        serviceIsBound = bindService(Intent(this, TodosBoundService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
        startService(Intent(this, TodosBoundService::class.java))
        super.onResume()
    }

    override fun onPause() {
        unbindService(serviceConnection)
        serviceIsBound = false
        super.onPause()
    }

}

class NullBoundServiceException : Exception("Bound Service was null on access")
