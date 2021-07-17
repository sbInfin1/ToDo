package android.example.todo.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.example.todo.database.TaskDatabase
import android.example.todo.notifications.NotificationUtils.NOTIFICATION_ID
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SnoozeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.i("SnoozeReceiver", "Outside the global scope")
        Toast.makeText(context, "Notification snoozed", Toast.LENGTH_SHORT).show()

//        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
//        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID, 0)

        NotificationManagerCompat.from(context).cancelAll()

        GlobalScope.launch {
            val dataSource = TaskDatabase.getInstance(context).taskDatabaseDao
            val taskId = intent.getLongExtra("taskId", 0)
            val task = dataSource.getTask(taskId = taskId)!!
            val taskTitle = task.title
            task.dueTime += 2*DateUtils.MINUTE_IN_MILLIS
            val taskDueTime = task.dueTime
            task.checked = false
            dataSource.update(task)

            Log.i("SnoozeReceiver", "taskTitle = $taskTitle")

            NotificationUtils.createNotificationChannel(context)

            val currentTimeInMillis = System.currentTimeMillis()

            val delayedTime = (taskDueTime.minus(currentTimeInMillis))

            val data = Data.Builder()
            data.putLong("taskId", taskId)

            val workManager = WorkManager.getInstance(context)

            val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(delayedTime, TimeUnit.MILLISECONDS)
                .addTag(taskTitle)
                .setInputData(data.build())
                .build()
            workManager.enqueue(request)
        }
    }
}