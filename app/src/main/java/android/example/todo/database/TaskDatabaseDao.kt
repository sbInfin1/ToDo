package android.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDatabaseDao {

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    /**
     * Retrieves the Task with given taskId
     */
    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    suspend fun getTask(taskId: Long): Task?

    /**
     * Retrieves all tasks in the database, ordered by the priority
     * and further ordered by the dueTime
     */
    @Query("SELECT * FROM task_table ORDER BY column_priority DESC, column_due_time")
    fun getAllTasks(): LiveData<List<Task>>

    /**
     * Retrieves all tasks that are in the given Category
     */
    @Query("SELECT * FROM task_table WHERE column_category = :category ORDER BY column_priority DESC, column_due_time")
    fun getTasksInCategory(category: String): LiveData<List<Task>>

    /**
     * Deletes all tuples from the task_table
     */
    @Query("DELETE FROM task_table")
    suspend fun clear()

    /**
     * Deletes the given task from the database
     */
    @Delete
    suspend fun deleteTask(taskEntry: Task)
}