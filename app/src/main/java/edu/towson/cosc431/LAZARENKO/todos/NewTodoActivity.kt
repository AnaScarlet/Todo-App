package edu.towson.cosc431.LAZARENKO.todos

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_new_todo.*
import java.util.*

class NewTodoActivity : FragmentActivity() {

    companion object {
        val TITLE = "TITLE"
        val CONTENTS = "CONTENTS"
        val IS_COMPLETED = "IS COMPLETED"
        val IMAGE = "IMAGE"
        val DUE_DATE = "DUE_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)

        save_btn.setOnClickListener{ getFormData() }
    }

    private fun getFormData() {
        val date = due_date_input.text.toString()
        val dateArr = date.split("/")

        val title = title_input.text.toString()
        var dueDate = "1/1/2018"
        if (dateArr.size == 3)
            dueDate = dateArr[0] + "/" + dateArr[1] + "/" + dateArr[2]
        val contents = contents_input.text.toString()
        val isCompleted:Boolean = checkBox.isChecked
        val image = "image here"

        val envelope = Intent()
        envelope.putExtra(TITLE, title)
        envelope.putExtra(DUE_DATE, dueDate)
        envelope.putExtra(CONTENTS, contents)
        envelope.putExtra(IS_COMPLETED, isCompleted)
        envelope.putExtra(IMAGE, image)

        setResult(RESULT_OK, envelope)
        finish()  // kill this activity and resume MainActivity
    }

}
