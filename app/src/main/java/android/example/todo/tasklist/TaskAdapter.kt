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
                }
                else {
                    holder.titleTextView.paintFlags = holder.titleTextView.paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG).inv()
                    updatedTask.checked = false
                    taskListViewModel.update(updatedTask)
                }
        }

        holder.itemView.setOnClickListener {
            listener(task)
        }

//        holder.itemView.setOnClickListener {
//            val intent = Intent(context, AddTask::class.java)
//            intent.putExtra("taskTitle", task.title)
//            intent.putExtra("taskDueTime", task.dueTime)
//            intent.putExtra("taskCategory", task.category)
//            context.startActivity(intent)
//        }

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