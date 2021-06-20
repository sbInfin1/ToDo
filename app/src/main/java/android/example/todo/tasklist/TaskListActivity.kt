package android.example.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.example.todo.R
import android.example.todo.addtask.AddTask
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import android.example.todo.addtask.TASK_TITLE
import android.example.todo.addtask.TASK_DUE_TIME

class TaskListActivity : AppCompatActivity() {

    private val NEW_TASK_RC = 1

    private lateinit var taskViewModel: TaskListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        val dataSource = TaskDatabase.getInstance(this).taskDatabaseDao

        val viewModelFactory = TaskListViewModelFactory(dataSource)

        taskViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TaskListViewModel::class.java)

        val taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter

        taskViewModel.tasks.observe(this, {
            it?.let {
                taskAdapter.submitList(it as MutableList<Task>)
            }
        })

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun fabOnClick() {
        val intent = Intent(this, AddTask::class.java)
        startActivityForResult(intent, NEW_TASK_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_TASK_RC && resultCode == Activity.RESULT_OK){
            data?.let{ intent ->
                val taskTitle = intent.getStringExtra(TASK_TITLE)
                val taskDueTime = intent.getLongExtra(TASK_DUE_TIME, 0)

                taskViewModel.insert(taskTitle, taskDueTime)
            }
        }
    }
}