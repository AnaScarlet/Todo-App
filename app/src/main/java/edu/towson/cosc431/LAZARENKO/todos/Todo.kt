package edu.towson.cosc431.LAZARENKO.todos

import java.text.SimpleDateFormat
import java.util.*

/*
    Model for storing data
 */
data class Todo(var title:String, var contents:String, var isCompleted:Boolean, var image:String, val dateCreated:Date) {
    override fun toString():String {
        val formatter = SimpleDateFormat("yyyy-MM-dd-hh.mm.ss")
        val str = ("Todo: Title: " + title + ", Contents: " + contents + ", Is Completed: " + isCompleted.toString()
                + ", Image: " + image + ", Date Created: " + formatter.format(dateCreated))
        return str
    }
}