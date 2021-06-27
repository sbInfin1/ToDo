package android.example.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,

    @ColumnInfo(name = "column_title")
    var title: String = "No title available",

    @ColumnInfo(name = "column_description")
    var description: String = "No description available",

    @ColumnInfo(name = "column_due_time")
    var dueTime: Long = 0L,

    @ColumnInfo(name = "column_remindMe_time")
    var remindMeTime: Long = 0L,

    @ColumnInfo(name = "column_priority")
    var priotity: Int = 0,

    @ColumnInfo(name = "column_category")
    var category: String = "General",

    @ColumnInfo(name = "column_checked")
    var checked: Boolean = false
)
