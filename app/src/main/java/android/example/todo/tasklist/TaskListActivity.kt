package android.example.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.example.todo.R
import android.example.todo.addtask.AddTask
import android.example.todo.addtask.TASK_CATEGORY
import android.example.todo.addtask.TASK_DUE_TIME
import android.example.todo.addtask.TASK_TITLE
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView


class TaskListActivity : AppCompatActivity() {

    val LOG_TAG = TaskListActivity::class.qualifiedName

    private val NEW_TASK_RC = 1

    private lateinit var taskViewModel: TaskListViewModel

    private lateinit var mDrawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nvDrawer: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        // Set a Toolbar to replace the actionBar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // This will display an Up icon, to be replaced with hamburger later
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // Find the drawerView
        nvDrawer = findViewById(R.id.nvView)

        // Find the drawer view
        mDrawer = findViewById(R.id.drawer_layout)

        setupDrawerContent(nvDrawer)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        val dataSource = TaskDatabase.getInstance(this).taskDatabaseDao

        val viewModelFactory = TaskListViewModelFactory(dataSource)

        taskViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TaskListViewModel::class.java)

        val taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter

        taskViewModel.tasks?.observe(this, {
            it?.let {
                taskAdapter.submitList(it as MutableList<Task>)
                Log.i(LOG_TAG, "tasks liveData change observed")
            }
        })

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }
    }

    private fun fabOnClick() {
        val intent = Intent(this, AddTask::class.java)
        startActivityForResult(intent, NEW_TASK_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_TASK_RC && resultCode == Activity.RESULT_OK){
            data?.let{ intent ->
                val taskTitle = intent.getStringExtra(TASK_TITLE)
                val taskDueTime = intent.getLongExtra(TASK_DUE_TIME, 0)
                val category = intent.getStringExtra(TASK_CATEGORY)

                taskViewModel.insert(taskTitle, taskDueTime, category)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // The action bar home/up action should open or close the drawer.
        when (item.itemId) {
            android.R.id.home -> {
                mDrawer.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {

        var category: String = when(menuItem.itemId){
            R.id.nav_first_fragment -> "First"
            R.id.nav_second_fragment -> "Second"
            R.id.nav_third_fragment -> "Third"
            else -> "General"
        }

        menuItem.setChecked(true)
        setTitle(menuItem.title)
        mDrawer.closeDrawers()

        // Updates the tasks liveData variable with the lists in the selected
        // category
        taskViewModel.setCategory(category)
    }

//    private fun setupDrawerContent(navigationView: NavigationView){
//        navigationView.setNavigationItemSelectedListener {
//            NavigationView.OnNavigationItemSelectedListener {  }
//        }
//    }
}