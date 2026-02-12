package com.mintflow.personal.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User

class NotifyManager {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CategoryBudget Alerts"
            val descriptionText = "Alerts when you exceed or approach your budget"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("BUDGET_ALERTS", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun checkAndNotifyCategoryBudgetStatus(
        context: Context,
        user: User,
        newTransaction: Transaction
    ) {
        val budgetList = PrefsHelper.loadCategoryBudgets(context, user.email)

        for (budget in budgetList) {
            if (budget.category == newTransaction.category) {
                val percent =
                    ((budget.spent / budget.budget) * 100).coerceAtMost(100.0).toInt()
                val remaining = budget.budget - budget.spent

                val message = when {
                    remaining < 0 -> "You've exceeded your ${budget.category} budget!"
                    remaining.toInt() == 0 -> "You've at your ${budget.category} budget limit!"
                    percent in 81..99 -> "You've near your ${budget.category} budget!"
                    else -> null
                }

                message?.let {
                    sendBudgetNotification(context, budget.category, it)
                }

            }

        }
    }

    private fun sendBudgetNotification(context: Context, category: String, message: String) {
        val builder = NotificationCompat.Builder(context, "BUDGET_ALERTS")
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("CategoryBudget Alert: $category")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = category.hashCode()
        notificationManager.notify(notificationId, builder.build())
    }

    fun notifyMonthlyBudgetStatus(context: Context, message: String, user: User) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        val allBudgets = PrefsHelper.loadBudgets(context, user.email)
        val allTransactions = PrefsHelper.loadTransactions(context, user.email)
        var totalSpent = 0.0

        for (transaction in allTransactions) {
            val calendar = Calendar.getInstance()
            calendar.time = transaction.date
            val transactionYear = calendar.get(Calendar.YEAR)
            val transactionMonth = calendar.get(Calendar.MONTH)

            if (transaction.type == "Expense" && transactionMonth == currentMonth && transactionYear == currentYear) {
                val amount = transaction.price
                totalSpent += amount
            }
        }

        val monthlyBudget = allBudgets
            .firstOrNull { it.month == currentMonth && it.year == currentYear }
            ?.budget ?: 0.0

        val remaining = monthlyBudget - totalSpent

        if (remaining < 0) {
            val builder = NotificationCompat.Builder(context, "BUDGET_ALERTS")
                .setSmallIcon(R.drawable.calender)
                .setContentTitle("Monthly Budget Exceeded")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(100, builder.build())
        }
    }

}