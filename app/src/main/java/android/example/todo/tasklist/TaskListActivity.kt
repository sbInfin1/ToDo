package android.example.todo.tasklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.todo.R
import android.example.todo.addtask.AddTask
import android.example.todo.addtask.TASK_CATEGORY
import android.example.todo.addtask.TASK_DUE_TIME
import android.example.todo.addtask.TASK_TITLE
import android.example.todo.addtask.TASK_ID
import android.example.todo.database.Task
import android.example.todo.database.TaskDatabase
import android.example.todo.database.TaskDatabaseDao
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView


class TaskListActivity : AppCompatActivity() {

    val LOG_TAG = TaskListActivity::class.qualifiedName

    private val NEW_TASK_RC = 1

    private lateinit var mDataSource: TaskDatabaseDao
    private lateinit var mTaskViewModel: TaskListViewModel
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mDrawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nvDrawer: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var navDrawerAdapter: ArrayAdapter<String>

    private var categories: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        // Set a Toolbar to replace the actionBar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // This will display an Up icon, to be replaced with hamburger later
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Find the drawerView
        nvDrawer = findViewById(R.id.nvView)

        // Find the drawer view
        mDrawer = findViewById(R.id.drawer_layout)

        //setupDrawerContent(nvDrawer)

        drawerToggle = ActionBarDrawerToggle(
            this, mDrawer, toolbar,
            R.string.drawer_open, R.string.drawer_closed
        )

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle)


        mRecyclerView = findViewById(R.id.recycler_view)

        mDataSource = TaskDatabase.getInstance(this).taskDatabaseDao

        val viewModelFactory = TaskListViewModelFactory(application, mDataSource)

        mTaskViewModel = ViewModelProvider(this, viewModelFactory)
            .get(TaskListViewModel::class.java)

        initializeTaskAdapter()

        mRecyclerView.adapter = mTaskAdapter

        mTaskViewModel.tasks?.observe(this, {
            it?.let {
                mTaskAdapter.submitList(it as MutableList<Task>)
                Log.i(LOG_TAG, "tasks liveData change observed")
            }
        })

        // Populate the navigation drawer with menus
        attachAdapterForNavDrawerListView()

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }

        setOnTouchItemHelper()

        setOnClickListenerForManageCategories()
    }

    private fun attachAdapterForNavDrawerListView() {

        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val categorySet = sharedPref.getStringSet("categories", emptySet())

        if (categorySet != null) {
            for (str in categorySet) categories.add(str)
        }

        navDrawerAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, categories)
        val listView: ListView = findViewById<View>(R.id.list_slidermenu) as ListView
        listView.setAdapter(navDrawerAdapter)

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->

            mTaskViewModel.setCategory(listView.getItemAtPosition(position).toString())
            mDrawer.closeDrawers()
        }

        val showAllTasksButton = findViewById<Button>(R.id.show_all_tasks_button)
        showAllTasksButton.setOnClickListener {
            listView.clearChoices()
            listView.requestLayout()
            mTaskViewModel.setCategory("All")
            mDrawer.closeDrawers()
        }
    }

    private fun initializeTaskAdapter() {

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val taskId = intent?.getLongExtra(TASK_ID, 0)
                    val taskTitle = intent?.getStringExtra(TASK_TITLE)
                    val taskDueTime = intent?.getLongExtra(TASK_DUE_TIME, 0)
                    val category = intent?.getStringExtra(TASK_CATEGORY)

                    val newTask = Task(
                        taskId = taskId!!,
                        title = taskTitle!!,
                        dueTime = taskDueTime!!,
                        category = category!!
                    )

                    mTaskViewModel.update(newTask)
                    mTaskViewModel.createNotification(this, taskId)
                }
            }

        mTaskAdapter = TaskAdapter(mTaskViewModel) { task ->
            val intent = Intent(this, AddTask::class.java)
            intent.putExtra(TASK_ID, task.taskId)
            intent.putExtra(TASK_TITLE, task.title)
            intent.putExtra(TASK_DUE_TIME, task.dueTime)
            intent.putExtra(TASK_CATEGORY, task.category)
            resultLauncher.launch(intent)
        }
    }

    private fun setOnTouchItemHelper() {
        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Called when a user swipes left or right on a ViewHolder
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                val position = viewHolder.adapterPosition

                val builder = MaterialAlertDialogBuilder(this@TaskListActivity)
                builder.setTitle("Delete")
                builder.setMessage("Are you sure to Delete the task?")

                builder.setPositiveButton("Yes") { dialog, which ->
                    mTaskViewModel.delete(position)
                    Toast.makeText(applicationContext, "Task deleted", Toast.LENGTH_SHORT).show()
                }

                builder.setNegativeButton("No") { dialog, which ->
                    Toast.makeText(applicationContext, "Task not deleted", Toast.LENGTH_SHORT)
                        .show()
                }
                builder.show()

                mTaskAdapter.notifyItemChanged(position);
            }
        }).attachToRecyclerView(mRecyclerView)
    }

    private fun setOnClickListenerForManageCategories() {
        val manageCategoriesButton = findViewById<Button>(R.id.button_manage_categories)

        manageCategoriesButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this@TaskListActivity)
            builder.setTitle("New Category")
            builder.setMessage("Enter the label of the new category")

            val input = EditText(this@TaskListActivity)
            builder.setView(input)

            builder.setPositiveButton("Add") { dialog, which ->
                categories.add(input.text.toString())
                navDrawerAdapter.notifyDataSetChanged()

                val categoriesSet: Set<String> = HashSet<String>(categories)

                val sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putStringSet("categories", categoriesSet)
                    apply()
                }
                Toast.makeText(applicationContext, "New category added", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(applicationContext, "Category was not added", Toast.LENGTH_SHORT)
                    .show()
            }
            builder.show()
        }
    }

    private fun fabOnClick() {
        val intent = Intent(this, AddTask::class.java)
        startActivityForResult(intent, NEW_TASK_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_TASK_RC && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                val taskTitle = intent.getStringExtra(TASK_TITLE)
                val taskDueTime = intent.getLongExtra(TASK_DUE_TIME, 0)
                val category = intent.getStringExtra(TASK_CATEGORY)

                mTaskViewModel.insert(this, taskTitle, taskDueTime, category)
//                mTaskViewModel.createNotification(this, taskId)
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

        var category: String = when (menuItem.itemId) {
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
        mTaskViewModel.setCategory(category)
    }
}