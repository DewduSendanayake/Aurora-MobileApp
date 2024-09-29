package com.dewdu.aurora

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.provider.Settings

class AddTaskActivity : AppCompatActivity() {
    private lateinit var taskTitle: EditText
    private lateinit var dueDate: EditText
    private lateinit var priority: EditText
    private lateinit var notes: EditText

    private var isEditing: Boolean = false
    private lateinit var originalTaskTitle: String
    private var reminderTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        // Check if exact alarm permission is granted (for Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        taskTitle = findViewById(R.id.taskTitle)
        dueDate = findViewById(R.id.dueDate)
        priority = findViewById(R.id.priority)
        notes = findViewById(R.id.notes)

        val submitTaskButton: Button = findViewById(R.id.submitTaskButton)
        submitTaskButton.setOnClickListener {
            createTask()
        }

        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            // Return to TaskActivity without saving
            finish()
        }

        // Check if we are editing an existing task
        intent?.let {
            originalTaskTitle = it.getStringExtra("TASK_TITLE") ?: ""
            if (originalTaskTitle.isNotEmpty()) {
                isEditing = true
                taskTitle.setText(originalTaskTitle)
                dueDate.setText(it.getStringExtra("DUE_DATE"))
                priority.setText(it.getStringExtra("PRIORITY"))
                notes.setText(it.getStringExtra("NOTES"))
                reminderTimeInMillis = it.getLongExtra("REMINDER_TIME", 0) // Load reminder time if editing

            }
        }

        // Button to set reminder
        findViewById<Button>(R.id.setReminderButton).setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun createTask() {
        val title = taskTitle.text.toString().trim()
        val date = dueDate.text.toString().trim()
        val priorityValue = priority.text.toString().trim()
        val notesValue = notes.text.toString().trim()

        // Validation for task title
        if (title.isEmpty()) {
            taskTitle.error = "Task title cannot be empty"
            taskTitle.requestFocus()
            return
        }

        // Validation for due date
        if (date.isEmpty()) {
            dueDate.error = "Due date cannot be empty"
            dueDate.requestFocus()
            return
        } else if (!isValidDate(date)) {
            dueDate.error = "Please enter a valid date (YYYY-MM-DD)"
            dueDate.requestFocus()
            return
        }

        // Validation for priority
        if (priorityValue.isEmpty()) {
            priority.error = "Priority cannot be empty"
            priority.requestFocus()
            return
        } else if (!isValidPriority(priorityValue)) {
            priority.error = "Priority must be High, Medium, or Low"
            priority.requestFocus()
            return
        }

        // Validation for notes (optional but can be validated for length)
        if (notesValue.length > 500) {
            notes.error = "Notes cannot exceed 500 characters"
            notes.requestFocus()
            return
        }

        val task = Task(title, date, priorityValue, notesValue)
        if (isEditing) {
            updateTask(originalTaskTitle, task)
            setResult(RESULT_OK, Intent().putExtra("ACTION", "UPDATE"))
        } else {
            // Ensure reminderTimeInMillis is updated before saving
            if (reminderTimeInMillis > 0) {
                setReminderAlarm(reminderTimeInMillis)
            }
            saveTask(task)
            setResult(RESULT_OK, Intent().putExtra("ACTION", "ADD"))
        }

        Toast.makeText(this, if (isEditing) "Task Updated: ${task.title}" else "Task Created: ${task.title}", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Function to validate date format (example: YYYY-MM-DD)
    private fun isValidDate(date: String): Boolean {
        return try {
            val parts = date.split("-")
            if (parts.size != 3) return false
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Month is 0-based in Calendar
            val day = parts[2].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            // Check if the date is valid
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day
        } catch (e: Exception) {
            false
        }
    }

    // Function to validate priority
    private fun isValidPriority(priority: String): Boolean {
        return priority.equals("High", ignoreCase = true) ||
                priority.equals("Medium", ignoreCase = true) ||
                priority.equals("Low", ignoreCase = true)
    }



    private fun updateWidget() {
        val intent = Intent(this, TaskWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, TaskWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    private fun saveTask(task: Task) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(task.title, "${task.dueDate},${task.priority},${task.notes},${if (reminderTimeInMillis > 0) reminderTimeInMillis else 0}")
        editor.apply()

        // Update the widget after saving a task
        updateWidget()

        // Log the saved task
        Log.d("AddTaskActivity", "Saved Task: ${task.title} -> ${task.dueDate}, ${task.priority}, ${task.notes}, Reminder: $reminderTimeInMillis")

        // Set reminder alarm if time is set
        if (reminderTimeInMillis > 0) {
            setReminderAlarm(reminderTimeInMillis)
        }

        setResult(RESULT_OK)
        finish()
    }


    private fun updateTask(oldTitle: String, task: Task) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the old task entry
        editor.remove(oldTitle)

        // Save the updated task with the new title
        editor.putString(task.title, "${task.dueDate},${task.priority},${task.notes},${reminderTimeInMillis}")
        editor.apply()

        // Log the updated task
        Log.d("AddTaskActivity", "Updated Task: ${task.title} -> ${task.dueDate}, ${task.priority}, ${task.notes}")
    }

    private fun setReminderAlarm(reminderTimeInMillis: Long) {
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("task_title", taskTitle.text.toString())
            putExtra("task_reminder", reminderTimeInMillis) // Assuming you want to send the reminder time
        }

        // Use a unique request code if needed, e.g., based on the task title hash code or a counter
        val requestCode = taskTitle.text.toString().hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent)
    }



    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(this,
                    { _, hourOfDay, minute ->
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                        reminderTimeInMillis = calendar.timeInMillis
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }
}
