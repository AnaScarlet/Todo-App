package edu.towson.cosc431.LAZARENKO.todos

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.todo_view.view.*
import android.R.attr.x
import android.content.Context
import android.view.Display
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.view.WindowManager



class TodosAdapter(val todosList: List<Todo>, val controller:IController): RecyclerView.Adapter<TodosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosViewHolder {
        // Inflate little view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_view, parent, false)

        view.layoutParams.height = (getScreenWidth(parent) / 7);

        // Create viewHolder for this little view
        val viewHolder = TodosViewHolder(view)

        // Do actions here
        viewHolder.itemView.completedState.setOnClickListener {
            val position = viewHolder.adapterPosition
            controller.toggleCompleted(position)
            notifyItemChanged(position)
        }

        viewHolder.itemView.delete_btn.setOnClickListener {
            val position = viewHolder.adapterPosition
            controller.deleteTodo(position)
            notifyItemRemoved(position)
        }

        return viewHolder

    }

    override fun getItemCount(): Int {
        return todosList.size
    }

    override fun onBindViewHolder(holder: TodosViewHolder, position: Int) {
        val thisTodo = todosList[position]
        Log.d("ADAPTER", thisTodo.toString())

        holder.itemView.title.text = thisTodo.title
        holder.itemView.due_date.text = thisTodo.dueDate
        holder.itemView.completedState.isChecked = thisTodo.isCompleted
        holder.itemView.contents_short.text = thisTodo.contents
    }

    fun getScreenWidth(parent: ViewGroup): Int {

        val wm = parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        return size.y
    }

}

class TodosViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)