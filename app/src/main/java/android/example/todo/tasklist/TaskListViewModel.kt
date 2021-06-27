package android.example.todo.tasklist

import android.app.Application
import android.content.Context
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabaseDao
import android.example.todo.notifications.NotificationUtils
import android.example.todo.notifications.NotificationWorker
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TaskListViewModel (
    val application: Application,
    val dataSource: TaskDatabaseDao) : ViewModel() {

    private val LOG_TAG = TaskListViewModel::class.qualifiedName

    private val currentCategory = MutableLiveData<String>("All")
    val tasks: LiveData<List<Task>> = Transformations.switchMap(currentCategory){
        category -> when(category){
            "All" -> dataSource.getAllTasks()
            else -> dataSource.getTasksInCategory(category)
        }
    }

    private val workManager = WorkManager.getInstance(application)

    private val _categories = MutableLiveData<ArrayList<String>>()
    val categories: LiveData<ArrayList<String>>
        get() = _categories

    init {
        addCategory("First")
        addCategory("Second")
        addCategory("Third")
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

    internal fun createNotification(context: Context, taskDueTime: Long?, taskTitle: String?) {

//        val delayTimeInSeconds: Long = 15
//        val delayedTime = TimeUnit.SECONDS.toMillis(delayTimeInSeconds)

        NotificationUtils.createNotificationChannel(context)

        val currentTimeInMillis = System.currentTimeMillis()
        val delayedTime = (taskDueTime?.minus(currentTimeInMillis)!!)

        val data = Data.Builder()
        data.putString("taskTitle", taskTitle)

        val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delayedTime, TimeUnit.MILLISECONDS)
            .addTag("TAG")
            .setInputData(data.build())
            .build()
        workManager.enqueue(request)
    }

    fun addCategory(newCategory: String){
        _categories.value?.add(newCategory)
    }

}