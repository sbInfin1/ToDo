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

//class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
//
//    override fun doWork(): Result {
//        val appContext = applicationContext
//
//        makeStatusNotification("Blurring image", appContext)
//
//        return try {
//            val picture = BitmapFactory.decodeResource(
//                appContext.resources,
//                R.drawable.test)
//
//            val output = blurBitmap(picture, appContext)
//
//            // Write bitmap to a temp file
//            val outputUri = writeBitmapToFile(appContext, output)
//
//            makeStatusNotification("Output is $outputUri", appContext)
//
//            Result.success()
//        } catch (throwable: Throwable) {
//            Timber.e(throwable, "Error applying blur")
//            Result.failure()
//        }
//    }
//}