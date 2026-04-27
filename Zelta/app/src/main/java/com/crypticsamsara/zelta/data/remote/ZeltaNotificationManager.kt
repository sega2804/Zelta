package com.crypticsamsara.zelta.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import com.crypticsamsara.zelta.domain.model.Goal
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZeltaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_BUDGET = "zelta_budget"
        const val CHANNEL_GOALS = "zelta_goals"
        const val CHANNEL_GENERAL = "zelta_general"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE)
    as NotificationManager

    init {
        createChannels()
    }

    // Create channels
    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when you're near or over a budget limit"
            }

            val goalsChannel = NotificationChannel(
                CHANNEL_GOALS,
                "Goal Milestones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Celebrate goal milestones and completions"
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General Zelta notifications"
            }

            notificationManager.createNotificationChannels(
                listOf(budgetChannel, goalsChannel, generalChannel)
            )
        }
    }

    // Budget warning
    fun showBudgetWarning(budget: Budget, categoryName: String) {
        val (title, message) = when (budget.budgetState) {
            BudgetState.WARNING  -> Pair(
                "Budget Alert ⚠️",
                "You've used ${"%.0f".format(budget.usagePercent * 100)}% of your $categoryName budget"
            )
            BudgetState.EXCEEDED -> Pair(
                "Over Budget! 🚨",
                "You've exceeded your $categoryName budget by $${"%.2f".format(-budget.remainingAmount)}"
            )
            else -> return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_BUDGET)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(budget.id.hashCode(), notification)
    }

    // Goal milestone
    fun showGoalMilestone(goal: Goal, percent: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_GOALS)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle("Goal Milestone! ${goal.icon}")
            .setContentText(
                "You're $percent% of the way to \"${goal.name}\" — keep it up!"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(goal.id.hashCode(), notification)
    }

    // Goal complete
    fun showGoalComplete(goal: Goal) {
        val notification = NotificationCompat.Builder(context, CHANNEL_GOALS)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle("Goal Complete! 🎉")
            .setContentText("You hit your target for \"${goal.name}\" — amazing work!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(goal.id.hashCode() + 1, notification)
    }

    // Daily reminder
    fun showDailyReminder() {
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Track today's spending 💸")
            .setContentText("Don't forget to log your expenses in Zelta")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(999, notification)
    }
}
