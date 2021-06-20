package android.example.todo.tasklist

import android.app.Application
import android.example.todo.database.TaskDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskListViewModelFactory(
    private val dataSource: TaskDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}