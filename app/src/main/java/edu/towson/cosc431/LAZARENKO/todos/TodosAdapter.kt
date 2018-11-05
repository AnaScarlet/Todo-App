package edu.towson.cosc431.LAZARENKO.todos

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.todo_view.view.*
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.view.WindowManager



class TodosAdapter(val todosList: List<Todo>, val controller:IController): RecyclerView.Adapter<TodosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodosViewHolder {
        // Inflate little view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_view, parent, false)

        view.layoutParams.height = (getScreenWidth(parent) / 7);

        // Create viewHolder for this little view
        val viewHolder = TodosViewHolder(view)
        viewHolder.itemView.isLongClickable = true

        // Do actions here
        viewHolder.itemView.completedState.setOnClickListener {
            completeEventListener(viewHolder)
        }

        viewHolder.itemView.setOnClickListener {
            completeEventListener(viewHolder)
        }

        viewHolder.itemView.delete_btn.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)

            builder.setMessage(R.string.delete_dialog)
                    .setPositiveButton(R.string.delete_btn,
                            DialogInterface.OnClickListener { dialog, id ->
                                // Send the positive button event back to the host activity
                                deleteEventListener(viewHolder)
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->

                            })

            val alertDialog = builder.create()
            alertDialog.show()
        }

        viewHolder.itemView.setOnLongClickListener{
            val builder = AlertDialog.Builder(it.context)

            builder.setMessage(R.string.delete_dialog)
                    .setPositiveButton(R.string.delete_btn,
                            DialogInterface.OnClickListener { dialog, id ->
                                // Send the positive button event back to the host activity
                                deleteEventListener(viewHolder)
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })

            val alertDialog = builder.create()
            alertDialog.show()
            true
        }

        return viewHolder

    }

    override fun getItemCount(): Int {
        return todosList.size
    }

    override fun onBindViewHolder(holder: TodosViewHolder, position: Int) {
        val thisTodo = todosList[position]
        Log.d("ADAPTER", thisTodo.toString())

        if (thisTodo.title.length <= 14)
            holder.itemView.title.text = thisTodo.title
        else
            holder.itemView.title.text = thisTodo.title.substring(0, 11) + "..."
        holder.itemView.due_date.text = thisTodo.dueDate
        holder.itemView.completedState.isChecked = thisTodo.isCompleted
        if (thisTodo.contents.length <= 30)
            holder.itemView.contents_short.text = thisTodo.contents
        else
            holder.itemView.contents_short.text = thisTodo.contents.substring(0, 27) + "..."
    }

    private fun deleteEventListener(viewHolder: TodosViewHolder) {
        val position = viewHolder.adapterPosition
        controller.deleteTodo(position)
        notifyItemRemoved(position)
    }

    private fun completeEventListener(viewHolder: TodosViewHolder) {
        val position = viewHolder.adapterPosition
        controller.toggleCompleted(position)
        notifyItemChanged(position)
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