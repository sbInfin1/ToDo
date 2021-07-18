package android.example.todo.tasklist

import android.app.Application
import android.content.Context
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabaseDao
import android.example.todo.notifications.NotificationUtils
import android.example.todo.notifications.NotificationWorker
import android.widget.Toast
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TaskListViewModel (
    private val application: Application,
    private val dataSource: TaskDatabaseDao) : ViewModel() {

    private val LOG_TAG = TaskListViewModel::class.qualifiedName

    private val currentCategory = MutableLiveData<String>("All")
    val tasks: LiveData<List<Task>> = Transformations.switchMap(currentCategory){
        category -> when(category){
            "All" -> dataSource.getAllTasks()
            else -> dataSource.getTasksInCategory(category)
        }
    }

    private val workManager = WorkManager.getInstance(application)

    fun insert(context: Context, taskTitle: String?, taskDueTime: Long?, taskCategory: String?){
        if(taskTitle == null || taskDueTime == null || taskCategory == null){
            return
        }

        viewModelScope.launch {
            val newTask = Task(title = taskTitle, dueTime = taskDueTime, category = taskCategory)
            val taskId = dataSource.insert(newTask)
            createNotification(context, taskId)
        }
    }

    fun delete(position: Int){
        viewModelScope.launch {
            val tasks: List<Task> = tasks.value!!
            dataSource.deleteTask(tasks[position])
        }
    }

    fun update(task: Task){
        viewModelScope.launch {
            dataSource.update(task)
        }
    }

    fun setCategory(category: String){
        currentCategory.value = category
    }

    internal fun createNotification(context: Context, taskId: Long) {

        viewModelScope.launch {
            val task = dataSource.getTask(taskId = taskId)!!
            val taskTitle = task.title
            val taskDueTime = task.dueTime

            NotificationUtils.createNotificationChannel(context)

            val currentTimeInMillis = System.currentTimeMillis()

            val delayedTime = (taskDueTime?.minus(currentTimeInMillis)!!)

            val data = Data.Builder()
            data.putLong("taskId", taskId)

            val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(delayedTime, TimeUnit.MILLISECONDS)
                .addTag(taskTitle!!)
                .setInputData(data.build())
                .build()
            workManager.enqueue(request)
        }
    }

    fun cancelNotification(context: Context, taskTitle: String?){
        workManager.cancelAllWorkByTag(taskTitle!!)
        Toast.makeText(context, "Scheduled Notification cancelled", Toast.LENGTH_SHORT).show()
    }
}