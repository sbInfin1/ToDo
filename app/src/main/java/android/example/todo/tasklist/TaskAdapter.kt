package android.example.todo.tasklist

import android.content.Context
import android.example.todo.R
import android.example.todo.database.Task
import android.example.todo.util.DateTimeFormatterUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter:
    ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTextView: TextView
        val dueTimeTextView: TextView
        val categoryTextView: TextView

        init {
            titleTextView = view.findViewById(R.id.task_title_textView)
            dueTimeTextView = view.findViewById(R.id.due_time_textView)
            categoryTextView = view.findViewById(R.id.category_textView)
        }

        fun bind(task: Task){
            titleTextView.text = task.title
            dueTimeTextView.text = DateTimeFormatterUtils.convertMillisToDateTime(task.dueTime)
            categoryTextView.text = task.category
        }
    }

    private lateinit var context: Context

    // Create new views (invoked by layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)

        context = parent.context

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by Layout Manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get element from the dataset at this position and replace the
        // contents of the view with that element
        val task = getItem(position)
        holder.bind(task)

        // click listener for long-click on any item, for deleting
//        holder.itemView.setOnLongClickListener {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Delete")
//            builder.setMessage("Are you Sure To Delete?")
//
//            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//                Toast.makeText(context,
//                    android.R.string.yes, Toast.LENGTH_SHORT).show()
//            }
//
//            builder.setNegativeButton(android.R.string.no) { dialog, which ->
//                Toast.makeText(context,
//                    android.R.string.no, Toast.LENGTH_SHORT).show()
//            }
//
////            builder.setNeutralButton("Maybe") { dialog, which ->
////                Toast.makeText(context,
////                    "Maybe", Toast.LENGTH_SHORT).show()
////            }
//            builder.show()
//            false
//        }
    }
}

object TaskDiffCallback : DiffUtil.ItemCallback<Task>(){

    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.taskId == newItem.taskId
    }
}