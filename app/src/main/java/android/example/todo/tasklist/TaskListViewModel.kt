package android.example.todo.tasklist

import android.app.Application
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.example.todo.database.TaskDatabaseDao
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TaskListViewModel (
    val dataSource: TaskDatabaseDao) : ViewModel() {

    private val LOG_TAG = TaskListViewModel::class.qualifiedName

    private val currentCategory = MutableLiveData<String>()
    val tasks: LiveData<List<Task>> = Transformations.switchMap(currentCategory){
        categoty -> dataSource.getTasksInCategory(categoty)
    }

    fun insert(taskTitle: String?, taskDueTime: Long?, taskCategory: String?){
        if(taskTitle == null || taskDueTime == null || taskCategory == null){
            return
        }

        viewModelScope.launch {
            val newTask = Task(title = taskTitle, dueTime = taskDueTime, category = taskCategory)
            dataSource.insert(newTask)
        }
    }

    fun setCategory(category: String){
        currentCategory.value = category
    }
}