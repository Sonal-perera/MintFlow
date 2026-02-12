package com.mintflow.personal.listener

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleTripleDailyReminders(context: Context) {
        scheduleReminder(context, "Morning Reminder", "Log your morning expenses!", 9)
        scheduleReminder(context, "Afternoon Reminder", "Don’t forget lunch or transport!", 14)
        scheduleReminder(context, "Evening Reminder", "Record your daily spending!", 20)
    }

    private fun scheduleReminder(context: Context, title: String, message: String, hourOfDay: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)

        val delay = target.timeInMillis - now.timeInMillis

        val inputData = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .build()

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_$hourOfDay")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "reminder_$hourOfDay",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelTripleReminders(context: Context) {
        listOf(8, 14, 20).forEach { hour ->
            WorkManager.getInstance(context).cancelUniqueWork("reminder_$hour")
        }
    }
}