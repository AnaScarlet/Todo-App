package edu.towson.cosc431.LAZARENKO.todos

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TODO_REQUEST_CODE:Int = 50;
        val TAG = "MAIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{ LaunchNewTodoActivity() }
    }

    private fun LaunchNewTodoActivity() {
        intent = Intent()
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
                    val date = Calendar.getInstance().time

                    val newTodo = Todo(title, contents, isCompleted, image, date)
                    Log.d(TAG, newTodo.toString())
                }
                else
                    Log.d(TAG, "Result Intent object was NULL")
            }
            else -> Log.d(TAG, "Result was CANCELLED")
        }
    }
}
