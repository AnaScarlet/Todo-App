package edu.towson.cosc431.LAZARENKO.todos

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
class TodosListFragment : Fragment(){

    companion object {
        val TAG = "List_FRAGMENT"
    }

    lateinit var todosList: MutableList<Todo>
    lateinit var controller: IController
    private lateinit var myView:View

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

        return myView
    }

    fun notifyItemAdded(position: Int) {
        myView.recyclerView.adapter.notifyItemInserted(position)
    }

    fun notifyDataChanged() {
        myView.recyclerView.adapter.notifyDataSetChanged()
    }

}
