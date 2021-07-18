package android.example.todo.notifications

import android.content.Context
import android.example.todo.R
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        //Toast.makeText(applicationContext, "Notification should be triggered now", Toast.LENGTH_SHORT).show()
        Log.i("NotificationWorker", "workerTriggered")

        GlobalScope.launch(Dispatchers.IO) {
            val taskId = inputData.getLong("taskId", 0)

            val dataSource = TaskDatabase.getInstance(applicationContext).taskDatabaseDao
            val task: Task = dataSource.getTask(taskId)!!

            withContext(Dispatchers.Main){
                task.checked = true
            }

            dataSource.update(task)

            withContext(Dispatchers.Main){
                val taskTitle = task.title
                NotificationUtils.triggerNotification(applicationContext, taskTitle, taskId)
            }
        }

        return Result.success()
    }
}