package android.example.todo.tasklist

import android.app.Application
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.example.todo.database.TaskDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskListViewModel (
    val dataSource: TaskDatabaseDao) : ViewModel() {

    val tasks = dataSource.getAllTasks()

    fun insert(taskTitle: String?, taskDueTime: Long?){
        if(taskTitle == null || taskDueTime == null){
            return
        }

        viewModelScope.launch {
            val newTask = Task(title = taskTitle, dueTime = taskDueTime)
            dataSource.insert(newTask)
        }
    }
}