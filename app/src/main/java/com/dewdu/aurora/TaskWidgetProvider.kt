package com.dewdu.aurora

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // Load tasks from SharedPreferences and display them
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val allTasks = sharedPreferences.all

        val taskList = StringBuilder()
        for ((key, value) in allTasks) {
            val taskData = value as String
            val taskInfo = taskData.split(",")
            if (taskInfo.size >= 3) {
                taskList.append("$key (Due: ${taskInfo[0]})\n")
            }
        }

        views.setTextViewText(R.id.widget_task_list, taskList.toString().trim())

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
