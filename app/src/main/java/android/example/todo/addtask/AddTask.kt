package android.example.todo.addtask

import android.app.Activity
import android.content.Intent
import android.example.todo.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val TASK_TITLE = "task_title"
const val TASK_DUE_TIME = "task_due_time"

class AddTask : AppCompatActivity() {

    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDueTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskTitleEditText = findViewById(R.id.task_title_editText)
        taskDueTimeEditText = findViewById(R.id.task_dueTime_EditText)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addTask()
        }
    }

    private fun addTask() {
        val resultIntent = Intent()

        if(taskTitleEditText.text.isNullOrEmpty() ||
            taskDueTimeEditText.text.isNullOrEmpty()){
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        else{
            val title = taskTitleEditText.text.toString()
            val dueTime = taskDueTimeEditText.text.toString().toLong()
            resultIntent.putExtra(TASK_TITLE, title)
            resultIntent.putExtra(TASK_DUE_TIME, dueTime)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }

}