package com.dewdu.aurora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)

        // Load tasks when the activity is created
        loadTasks()

        // Navigation buttons setup
        findViewById<ImageButton>(R.id.navPomodoro).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Optional: close current activity
        }

        findViewById<ImageButton>(R.id.navTasks).setOnClickListener {
            // No action needed as we're already in TaskActivity
        }

        findViewById<ImageButton>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1) // Start for result
        }
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val allTasks = sharedPreferences.all

        Log.d("TaskActivity", "All Tasks: $allTasks") // Log all tasks

        val taskContainer: LinearLayout = findViewById(R.id.taskContainer)
        taskContainer.removeAllViews()

        for ((key, value) in allTasks) {
            val taskData = value as? String ?: continue
            val taskInfo = taskData.split(",")

            if (taskInfo.size >= 4) {
                val taskTitle = key
                val taskDueDate = taskInfo[0]
                val taskPriority = taskInfo[1]
                val taskNotes = taskInfo[2]
                val reminderTime = taskInfo[3].toLongOrNull() ?: 0L

                // Log the task data
                Log.d("TaskActivity", "Loaded Task: $taskTitle -> $taskDueDate, $taskPriority, $taskNotes, Reminder: $reminderTime")

                // Ensure the task view is created
                val taskView = createTaskView(taskTitle, taskDueDate, taskPriority, taskNotes, reminderTime)
                taskContainer.addView(taskView)
            } else {
                Log.e("TaskActivity", "Task data not in expected format: $taskData")
            }
        }

    }


    private fun createTaskView(title: String, dueDate: String, priority: String, notes: String, reminderTime: Long?): LinearLayout {
        val taskLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL // Main layout horizontal
            gravity = android.view.Gravity.CENTER_VERTICAL // Center align vertically
            setPadding(20, 10, 16, 10) // Padding for the entire task layout
            (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 22) // Space between task sets
        }

        // Create a vertical layout for the task details
        val detailsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL // Details layout vertical
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) // Fill remaining space
            setPadding(16, 0, 0, 0) // Move displaying data a little to the right
        }

        // Title
        val titleTextView = TextView(this).apply {
            text = title
            textSize = 18f // Adjust the size as needed
            setTypeface(null, android.graphics.Typeface.BOLD) // Make it bold
            setPadding(10, 10, 10, 0) // Padding for the title
        }

        // Due Date
        val dueDateTextView = TextView(this).apply {
            text = "• Due Date: $dueDate" // Bullet point
            setPadding(10, 5, 10, 0) // Padding for spacing
        }

        // Priority
        val priorityTextView = TextView(this).apply {
            text = "• Priority: $priority" // Bullet point
            setPadding(10, 5, 10, 0) // Padding for spacing
        }

        // Notes
        val notesTextView = TextView(this).apply {
            text = "• Notes: $notes" // Bullet point
            setPadding(10, 5, 10, 0) // Padding for spacing
        }

        // Adding all TextViews to the details layout
        detailsLayout.addView(titleTextView)
        detailsLayout.addView(dueDateTextView)
        detailsLayout.addView(priorityTextView)
        detailsLayout.addView(notesTextView)

        // Create a layout for the buttons
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL // Buttons layout vertical
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 0, 10, 0) // Move buttons a little to the left
        }

        val editButton = Button(this).apply {
            text = "Edit"
            setBackgroundColor(resources.getColor(android.R.color.white)) // Change button color
            setTextColor(android.graphics.Color.RED) // Change text color for contrast
            setOnClickListener {
                val intent = Intent(this@TaskActivity, AddTaskActivity::class.java).apply {
                    putExtra("TASK_TITLE", title)
                    putExtra("DUE_DATE", dueDate)
                    putExtra("PRIORITY", priority)
                    putExtra("NOTES", notes)
                }
                startActivityForResult(intent, 1) // Start for result
            }
        }

        val deleteButton = Button(this).apply {
            text = "Delete"
            setBackgroundColor(resources.getColor(android.R.color.holo_red_dark)) // Change button color
            setTextColor(android.graphics.Color.WHITE) // Change text color for contrast
            setOnClickListener {
                deleteTask(title) // Call delete function
                loadTasks() // Refresh task list
            }
        }

        // Adding buttons to the button layout
        buttonLayout.addView(editButton)
        buttonLayout.addView(deleteButton)

        // Adding both layouts to the main task layout
        taskLayout.addView(detailsLayout) // Add the details first
        taskLayout.addView(buttonLayout) // Add the button layout next

//        // Add a horizontal line separator
//        val separator = View(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1 // Height of the line
//            )
//            setBackgroundColor(android.graphics.Color.LTGRAY) // Color of the line
//        }
//
//        taskLayout.addView(separator) // Add the separator to the task layout

        return taskLayout
    }


    private fun deleteTask(title: String) {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(title)
        editor.apply()
        Toast.makeText(this, "Task Deleted: $title", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loadTasks() // Refresh task list when a task is added or updated
        }
    }
}
