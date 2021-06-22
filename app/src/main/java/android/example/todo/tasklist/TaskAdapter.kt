package android.example.todo.tasklist

import android.example.todo.R
import android.example.todo.database.Task
import android.example.todo.util.DateTimeFormatterUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    // Create new views (invoked by layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by Layout Manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get element from the dataset at this position and replace the
        // contents of the view with that element
//        holder.titleTextView.text = tasks[position].title
//        holder.dueTimeTextView.text = tasks[position].dueTime.toString()

        val task = getItem(position)
        holder.bind(task)
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