package edu.towson.cosc431.LAZARENKO.todos

import java.text.SimpleDateFormat
import java.util.*

/*
    Model for storing data
 */
data class Todo(var title:String, var contents:String, var isCompleted:Boolean, var image:String, val dateCreated:Date, val dueDate:String) {
    override fun toString():String {
        val formatter1 = SimpleDateFormat("yyyy-MM-dd-hh.mm.ss")
        val formatter2 = SimpleDateFormat("MM/dd/yyyy")
        val str = ("Todo: Title: " + title + ", Contents: " + contents + ", Is Completed: " + isCompleted.toString()
                + ", Image: " + image + ", Date Created: " + formatter1.format(dateCreated) + ", Due Date: " + dueDate)
        return str
    }
}