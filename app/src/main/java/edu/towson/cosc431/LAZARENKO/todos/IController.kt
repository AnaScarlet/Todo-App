package edu.towson.cosc431.LAZARENKO.todos

interface IController{
    fun launchNewTodoActivity()
    fun addTodo(todo:Todo)
    fun deleteTodo(indx:Int)
    fun toggleCompleted(indx:Int)
    fun getTodosList(): MutableList<Todo>
    fun getActiveTodosList(): MutableList<Todo>
    fun getCompletedTodosList(): MutableList<Todo>
}