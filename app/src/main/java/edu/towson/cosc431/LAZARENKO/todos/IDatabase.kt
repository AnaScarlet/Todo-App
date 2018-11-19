package edu.towson.cosc431.LAZARENKO.todos

interface IDatabase {
    fun addTodo(todo:Todo)
    fun deleteTodo(todo: Todo)
    fun updateTodo(todo: Todo)
    fun getTodo(id:String): Todo
    fun getTodos(): List<Todo>  // gets all Todos
}