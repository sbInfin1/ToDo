package android.example.todo.tasklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.todo.R
import android.example.todo.addtask.AddTask
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabaseDao
import android.example.todo.util.DateTimeFormatterUtils
import android.graphics.Paint
import android.service.autofill.Validators.not
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskListViewModel: TaskListViewModel,
                    private val listener: (Task) -> Unit):
    ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTextView: TextView
        val dueTimeTextView: TextView
        val categoryTextView: TextView
        val taskDoneCheckBox: CheckBox

        init {
            titleTextView = view.findViewById(R.id.task_title_textView)
            dueTimeTextView = view.findViewById(R.id.due_time_textView)
            categoryTextView = view.findViewById(R.id.category_textView)
            taskDoneCheckBox = view.findViewById(R.id.todoCheckBox)
        }

        fun bind(task: Task){
            titleTextView.text = task.title
            dueTimeTextView.text = DateTimeFormatterUtils.convertMillisToDateTime(task.dueTime)
            categoryTextView.text = task.category

            if(task.checked){
                titleTextView.paintFlags = titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                taskDoneCheckBox.isChecked = true
            }
            else {
                titleTextView.paintFlags = titleTextView.paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG).inv()
                taskDoneCheckBox.isChecked = false
            }
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

        holder.taskDoneCheckBox.setOnClickListener {
            val updatedTask: Task = task
                if(holder.taskDoneCheckBox.isChecked){
                    holder.titleTextView.paintFlags = holder.titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    updatedTask.checked = true
                    taskListViewModel.update(updatedTask)
                    taskListViewModel.cancelNotification(context, updatedTask.title)
                }
                else {
                    val res = System.currentTimeMillis() > updatedTask.dueTime
//                    val res = taskListViewModel.createNotification(context, updatedTask.taskId)
                    if(res){
                        Toast.makeText(context, "Task due time is behind the current time", Toast.LENGTH_SHORT).show()
                        holder.taskDoneCheckBox.isChecked = true
                    }
                    else{
                        holder.titleTextView.paintFlags = holder.titleTextView.paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG).inv()
                        updatedTask.checked = false
                        taskListViewModel.update(updatedTask)
                    }
                }
        }

        holder.itemView.setOnClickListener {
            listener(task)
        }
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