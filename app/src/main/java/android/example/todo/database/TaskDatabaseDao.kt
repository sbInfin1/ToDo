package android.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDatabaseDao {

    @Insert
    suspend fun insert(task: Task)

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
     * Deletes all tuples from the task_table
     */
    @Query("DELETE FROM task_table")
    suspend fun clear()
}