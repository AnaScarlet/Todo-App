package edu.towson.cosc431.LAZARENKO.todos

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_todos_list.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TodosListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TodosListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TodosListFragment : Fragment(), ITodosList{

    private lateinit var type: String

    companion object {
        val TAG = "List_FRAGMENT"
        val ALL_TYPE = "ALL"
        val COMPLETED_TYPE = "COMPLETED"
        val ACTIVE_TYPE = "ACTIVE"
    }

    private var todosList: MutableList<Todo> = mutableListOf()
    private lateinit var controller: IController
    private lateinit var myView:View

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.controller = context as IController
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        try {
            myView = inflater.inflate(R.layout.fragment_todos_list, container, false)
            Log.d(TAG, "Inflated TodosListFragment")
        } catch (e: InflateException) {
            Log.w(TAG, "Failed to inflate TodosListFragment view")
            return null
        }

        // Controller and todosList should be initialized by the caller by now...
        try {
            val todosAdapter = TodosAdapter(todosList, controller)
            myView.recyclerView.adapter = todosAdapter
            myView.recyclerView.layoutManager = LinearLayoutManager(myView.context)
        } catch (e: UninitializedPropertyAccessException) {
            Log.w(TAG, "Controller or TodosList is missing!")
            e.printStackTrace()
        }

        // Default data to display:
        setListType(ALL_TYPE)
        updateTodosList()

        return myView
    }

    override fun notifyItemAdded(position: Int) {
        myView.recyclerView.adapter.notifyItemInserted(position)
    }

    fun notifyDataChanged() {
        myView.recyclerView.adapter.notifyDataSetChanged()
    }

    override fun updateTodosList() {
        when (type) {
            ALL_TYPE -> {
                todosList.clear()
                todosList.addAll(controller.getTodosList())
            }
            COMPLETED_TYPE -> {
                todosList.clear()
                todosList.addAll(controller.getCompletedTodosList())
            }
            ACTIVE_TYPE -> {
                todosList.clear()
                todosList.addAll(controller.getActiveTodosList())
            }
        }
        notifyDataChanged()
    }

    override fun setListType(type: String) {
        when (type) {
            ALL_TYPE -> {
                this.type = ALL_TYPE
            }
            COMPLETED_TYPE -> {
                this.type = COMPLETED_TYPE
            }
            ACTIVE_TYPE -> {
                this.type = ACTIVE_TYPE
            }
            else -> throw InvalidTypeException()
        }
    }

}

// TODO: Use this in calling code
interface ITodosList {
    fun notifyItemAdded(position: Int)
    //fun notifyDataChanged()
    fun updateTodosList()
    fun setListType(type: String)
}

class InvalidTypeException: Exception() {
    override val message: String?
        get() = "Invalid Todos List type"
}
