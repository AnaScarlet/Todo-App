package edu.towson.cosc431.LAZARENKO.todos

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import java.text.SimpleDateFormat

object todosContract {
    object todosTable {
        const val TABLE_NAME = "TodosTable"
    //    const val ID_COLUMN = "ID"
        const val TITLE_COLUMN = "Title"
        const val CONTENTS_COLUMN = "Contents"
        const val IS_COMPLETED_COLUMN = "IsCompleted"
        const val IS_DELETED_COLUMN = "IsDeleted"
        const val IMAGE_COLUMN = "Image"
        const val DATE_CREATED_COLUMN = "DateCreated"
        const val DUE_DATE_COLUMN = "DueDate"
    }
}

private const val CREATE_DATABASE = "CREATE TABLE ${todosContract.todosTable.TABLE_NAME} (" +
       // "${todosContract.todosTable.ID_COLUMN} INTEGER PRIMARY KEY, " +
        "${todosContract.todosTable.TITLE_COLUMN} TEXT, " +
        "${todosContract.todosTable.CONTENTS_COLUMN} TEXT, " +
        "${todosContract.todosTable.IS_COMPLETED_COLUMN} INTEGER DEFAULT 0, " +
        "${todosContract.todosTable.IS_DELETED_COLUMN} INTEGER DEFAULT 0, " +
        "${todosContract.todosTable.IMAGE_COLUMN} TEXT, " +
        "${todosContract.todosTable.DATE_CREATED_COLUMN} TEXT PRIMARY KEY, " +
        "${todosContract.todosTable.DUE_DATE_COLUMN} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${todosContract.todosTable.TABLE_NAME}"


class TodosDatabase(context: Context) : IDatabase {

    private val db: SQLiteDatabase = TodosDatabaseHelper(context).writableDatabase
    private val gson = Gson()

    override fun addTodo(todo: Todo) {
        Log.d("DATABASE: Add", todo.toString())
        val cvalues = toContentValues(todo)

        val err = db.insert(todosContract.todosTable.TABLE_NAME, null, cvalues)
        if (err == (-1).toLong())
            Log.w("DATABASE", "Failed to insert ${todo} into database")
    }

    override fun deleteTodo(todo: Todo) {
        todo.isDeleted = true
        val cvalues = toContentValues(todo)

        db.update(
                todosContract.todosTable.TABLE_NAME,
                cvalues,
                "${todosContract.todosTable.DATE_CREATED_COLUMN} = ?",
                arrayOf(todo.dateCreated)
        )
    }

    override fun clearTodosTable() {
        db.delete(todosContract.todosTable.TABLE_NAME, null, null)
    }

    override fun updateTodo(todo: Todo) {
        Log.d("DATABASE: Update", todo.toString())
        val cvalues = toContentValues(todo)

        db.update(
                todosContract.todosTable.TABLE_NAME,
                cvalues,
                "${todosContract.todosTable.DATE_CREATED_COLUMN} = ?",
                arrayOf(todo.dateCreated)
        )
    }

    override fun getTodo(dateCreated: String): Todo {
        val cursor = db.rawQuery(
                "SELECT * " +
                        "FROM ${todosContract.todosTable.TABLE_NAME}" +
                        "WHERE ${todosContract.todosTable.DATE_CREATED_COLUMN} = ?",
                arrayOf(dateCreated)
        )

        lateinit var todo: Todo

        while(cursor.moveToNext()) {
           // val id = cursor.getInt(cursor.getColumnIndex(todosContract.todosTable.ID_COLUMN))
            val title = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.TITLE_COLUMN))
            val contents = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.CONTENTS_COLUMN))
            val isCompleted = cursor.getInt(cursor.getColumnIndex(todosContract.todosTable.IS_COMPLETED_COLUMN))
            val isDeleted = cursor.getInt(cursor.getColumnIndex(todosContract.todosTable.IS_DELETED_COLUMN))
            val jsonImage = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.IMAGE_COLUMN))
            val image = gson.fromJson(jsonImage, Bitmap::class.java)

            val dateCreated = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.DATE_CREATED_COLUMN))
            val dueDate = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.DUE_DATE_COLUMN))

            todo = Todo(title, contents, isCompleted = isCompleted==1, isDeleted = isDeleted==1,
                    image = image, dateCreated = dateCreated, dueDate = dueDate)
        }

        cursor.close()
        return todo
    }

    override fun getTodos(): List<Todo> {
        val cursor = db
                .rawQuery(
                        "SELECT * FROM ${todosContract.todosTable.TABLE_NAME} "
                                // where not deleted
                                + "WHERE ${todosContract.todosTable.IS_DELETED_COLUMN}=0"
                        , null
                )
        val result = mutableListOf<Todo>()
        lateinit var todo: Todo

        // Will move through every tuple returned by the query
        while(cursor.moveToNext()) {
            val title: String = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.TITLE_COLUMN))
            val contents = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.CONTENTS_COLUMN))
            val isCompleted = cursor.getInt(cursor.getColumnIndex(todosContract.todosTable.IS_COMPLETED_COLUMN))
            val isDeleted = cursor.getInt(cursor.getColumnIndex(todosContract.todosTable.IS_DELETED_COLUMN))
            val jsonImage = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.IMAGE_COLUMN))
            val image = gson.fromJson(jsonImage, Bitmap::class.java)

            val dateCreated = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.DATE_CREATED_COLUMN))
            val dueDate = cursor.getString(cursor.getColumnIndex(todosContract.todosTable.DUE_DATE_COLUMN))

            todo = Todo(title, contents, isCompleted = isCompleted==1, isDeleted = isDeleted==1,
                    image = image, dateCreated = dateCreated, dueDate = dueDate)
            result.add(todo)
        }

        cursor.close()

        return result

    }

    private fun toContentValues(todo: Todo): ContentValues {
        val cvalues = ContentValues()

        val jsonImage = gson.toJson(todo.image, Bitmap::class.java)

        cvalues.put(todosContract.todosTable.TITLE_COLUMN, todo.title)
        cvalues.put(todosContract.todosTable.CONTENTS_COLUMN, todo.contents)
        cvalues.put(todosContract.todosTable.IS_COMPLETED_COLUMN, todo.isCompleted)
        cvalues.put(todosContract.todosTable.IS_DELETED_COLUMN, todo.isDeleted)
        cvalues.put(todosContract.todosTable.IMAGE_COLUMN, jsonImage)
        cvalues.put(todosContract.todosTable.DATE_CREATED_COLUMN, todo.dateCreated)
        cvalues.put(todosContract.todosTable.DUE_DATE_COLUMN, todo.dueDate)

        return cvalues
    }

}

class TodosDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        db?.execSQL(CREATE_DATABASE)
    }

    companion object {
        val DATABASE_NAME = "todos.db"
        val DATABASE_VERSION = 6
    }

}
