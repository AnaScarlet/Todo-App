package edu.towson.cosc431.LAZARENKO.todos

import android.graphics.Bitmap

/*
    Model for storing data
 */
data class Todo(var title:String, var contents:String, var isCompleted:Boolean, var isDeleted:Boolean, var image:Bitmap?, val dateCreated:String, val dueDate:String) {
    override fun toString():String {
        val str = ("Todo: Title: " + title + ", Contents: " + contents + ", Is Completed: " + isCompleted.toString()
                + ", Is Dalated: " + isDeleted.toString() + ", Image: " + image
                + ", Date Created: " + dateCreated + ", Due Date: " + dueDate)
        return str
    }
}