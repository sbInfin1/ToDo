package android.example.todo.addtask

import android.app.Activity
import android.content.Intent
import android.example.todo.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

const val TASK_TITLE = "task_title"
const val TASK_DUE_TIME = "task_due_time"
const val TASK_CATEGORY = "task_category"

class AddTask : AppCompatActivity() {

    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDueTimeEditText: EditText
    private lateinit var categoryEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskTitleEditText = findViewById(R.id.task_title_editText)
        taskDueTimeEditText = findViewById(R.id.task_dueTime_editText)
        categoryEditText = findViewById(R.id.task_category_editText)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addTask()
        }
    }

    private fun addTask() {
        val resultIntent = Intent()

        if(taskTitleEditText.text.isNullOrEmpty() ||
            taskDueTimeEditText.text.isNullOrEmpty() ||
            categoryEditText.text.isNullOrEmpty()){
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        else{
            val title = taskTitleEditText.text.toString()
            val dueTime = taskDueTimeEditText.text.toString().toLong()
            val category = categoryEditText.text.toString()
            resultIntent.putExtra(TASK_TITLE, title)
            resultIntent.putExtra(TASK_DUE_TIME, dueTime)
            resultIntent.putExtra(TASK_CATEGORY, category)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }

}