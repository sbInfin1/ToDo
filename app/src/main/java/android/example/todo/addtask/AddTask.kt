package android.example.todo.addtask

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.todo.R
import android.example.todo.tasklist.TaskListActivity
import android.example.todo.util.DateTimeFormatterUtils
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlin.properties.Delegates

const val TASK_TITLE = "task_title"
const val TASK_DUE_TIME = "task_due_time"
const val TASK_CATEGORY = "task_category"

class AddTask : AppCompatActivity() {

    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDueTimeEditText: EditText
    private lateinit var categoryTextView: AutoCompleteTextView
    private lateinit var dueTimeTextView: AutoCompleteTextView
    private lateinit var dueTimeTextInputLayout: TextInputLayout

    private lateinit var dateString: String
    private lateinit var dateTimeString: String
    private var dueTimeMilliseconds by Delegates.notNull<Long>()

    private var categories: ArrayList<String> =  ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskTitleEditText = findViewById(R.id.task_title_editText)
        //taskDueTimeEditText = findViewById(R.id.task_dueTime_editText)
        categoryTextView = findViewById(R.id.task_category_textView)
        dueTimeTextView = findViewById(R.id.due_time_textView)
        dueTimeTextInputLayout = findViewById(R.id.task_dueTime_text_input)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addTask()
        }

        populateTheCategoriesAutoCompleteTextView()

        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(this,
            R.layout.dropdown_menu_time_list_item, items)
        dueTimeTextView.setAdapter(adapter)
        //(textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        dueTimeTextInputLayout.setStartIconOnClickListener {

            var date: Long
            var newHour: Int
            var newMinute: Int

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()

            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select due time")
                    .build()

            datePicker.show(supportFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener {
                date = datePicker.selection!!
                dateString = datePicker.headerText
                Toast.makeText(this, "Date: $dateString", Toast.LENGTH_SHORT).show()
                timePicker.show(supportFragmentManager, "Picker")
            }

            timePicker.addOnPositiveButtonClickListener {
                newHour = timePicker.hour
                newMinute = timePicker.minute
                Toast.makeText(this, "Time: ${newHour}:${newMinute}", Toast.LENGTH_SHORT).show()

                dateTimeString = "$dateString $newHour:$newMinute"
                dueTimeMilliseconds = DateTimeFormatterUtils.convertDateTimeToMillis(dateTimeString)
                dateTimeString = DateTimeFormatterUtils.convertMillisToDateTime(dueTimeMilliseconds)
                dueTimeTextView.setText(dateTimeString)
            }
            //Toast.makeText(this, "Clock should appear here", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTask() {
        val resultIntent = Intent()

        if(taskTitleEditText.text.isNullOrEmpty() ||
            //taskDueTimeEditText.text.isNullOrEmpty() ||
            categoryTextView.text.isNullOrEmpty()){
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        else{
            val title = taskTitleEditText.text.toString()
            //val dueTime = DateTimeFormatterUtils.convertDateTimeToMillis(dateTimeString)
            val category = categoryTextView.text.toString()
            resultIntent.putExtra(TASK_TITLE, title)
            resultIntent.putExtra(TASK_DUE_TIME, dueTimeMilliseconds)
            resultIntent.putExtra(TASK_CATEGORY, category)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }

    fun populateTheCategoriesAutoCompleteTextView(){
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val categorySet = sharedPref.getStringSet("categories", emptySet())

        if (categorySet != null) {
            for (str in categorySet) categories.add(str)
        }

        val adapter = ArrayAdapter(this,
            R.layout.dropdown_menu_time_list_item, categories)
        categoryTextView.setAdapter(adapter)
    }

}