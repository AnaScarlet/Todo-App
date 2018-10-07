package edu.towson.cosc431.LAZARENKO.todos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_new_todo.*

class NewTodoActivity : AppCompatActivity() {

    companion object {
        val TITLE = "TITLE"
        val CONTENTS = "CONTENTS"
        val IS_COMPLETED = "IS COMPLETED"
        val IMAGE = "IMAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)

        save_btn.setOnClickListener{ getFormData() }
    }

    private fun getFormData() {
        val title:String = title_input.text.toString()
        val contents = contents_input.text.toString()
        val isCompleted = checkBox.isChecked
        val image = "image here"

        val envelope = Intent()
        envelope.putExtra(TITLE, title)
        envelope.putExtra(CONTENTS, contents)
        envelope.putExtra(IS_COMPLETED, isCompleted)
        envelope.putExtra(IMAGE, image)

        setResult(RESULT_OK, envelope)
        finish()  // kill this activity and resume MainActivity
    }
}
