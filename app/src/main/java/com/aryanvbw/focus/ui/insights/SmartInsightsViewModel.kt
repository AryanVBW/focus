package com.aryanvbw.focus.ui.insights

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aryanvbw.focus.data.model.WeeklyStats
import com.aryanvbw.focus.data.model.Goal

class SmartInsightsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _productivityScore = MutableLiveData<Int>()
    val productivityScore: LiveData<Int> = _productivityScore
    
    private val _weeklyStats = MutableLiveData<WeeklyStats>()
    val weeklyStats: LiveData<WeeklyStats> = _weeklyStats
    
    private val _insights = MutableLiveData<List<String>>()
    val insights: LiveData<List<String>> = _insights
    
    private val _habits = MutableLiveData<List<String>>()
    val habits: LiveData<List<String>> = _habits
    
    private val _goals = MutableLiveData<List<Goal>>()
    val goals: LiveData<List<Goal>> = _goals
    
    fun loadInsights() {
        // Calculate productivity score based on various factors
        val score = calculateProductivityScore()
        _productivityScore.value = score
        
        // Load weekly statistics
        val weeklyStats = WeeklyStats(
            focusHours = 28,
            focusMinutes = 45,
            appsBlocked = 156,
            goalsAchieved = 7,
            totalGoals = 10,
            streakDays = 12
        )
        _weeklyStats.value = weeklyStats
        
        // Generate AI-powered insights
        val insightsList = generateInsights(score, weeklyStats)
        _insights.value = insightsList
        
        // Load habit suggestions
        val habitsList = listOf(
            "📱 Check phone less during focus time",
            "⏰ Set specific times for social media",
            "🎯 Complete 3 deep work sessions daily"
        )
        _habits.value = habitsList
        
        // Load current goals
        val goalsList = listOf(
            Goal(1, "Reduce daily screen time to 4 hours", false),
            Goal(2, "Complete 25 focus sessions this week", true),
            Goal(3, "Block social media during work hours", true),
            Goal(4, "Maintain 7-day focus streak", false),
            Goal(5, "Read for 30 minutes daily", true)
        )
        _goals.value = goalsList
    }
    
    private fun calculateProductivityScore(): Int {
        // This would typically use real data from the database
        // For now, return a sample score based on simulated metrics
        
        val focusTimeScore = 85 // Based on focus time goals
        val blockingEffectivenessScore = 78 // Based on successful blocks
        val consistencyScore = 92 // Based on daily usage patterns
        val goalCompletionScore = 70 // Based on completed goals
        
        // Weighted average
        val totalScore = (focusTimeScore * 0.3 + 
                         blockingEffectivenessScore * 0.25 + 
                         consistencyScore * 0.25 + 
                         goalCompletionScore * 0.2).toInt()
        
        return totalScore.coerceIn(0, 100)
    }
    
    private fun generateInsights(score: Int, stats: WeeklyStats): List<String> {
        val insights = mutableListOf<String>()
        
        // Score-based insights
        when {
            score >= 80 -> {
                insights.add("🚀 You're in the top 20% of Focus users! Your consistency is paying off.")
            }
            score >= 60 -> {
                insights.add("📈 Good progress! Try extending your focus sessions by 15 minutes.")
            }
            else -> {
                insights.add("💡 Start with shorter focus sessions and gradually increase duration.")
            }
        }
        
        // Stats-based insights
        if (stats.streakDays >= 7) {
            insights.add("🔥 Amazing ${stats.streakDays}-day streak! Consistency is key to building lasting habits.")
        }
        
        if (stats.appsBlocked > 100) {
            insights.add("🛡️ You've blocked ${stats.appsBlocked} distracting apps this week. Your focus is getting stronger!")
        }
        
        // Time-based insights
        val totalMinutes = stats.focusHours * 60 + stats.focusMinutes
        if (totalMinutes > 1200) { // More than 20 hours
            insights.add("⭐ Exceptional focus time this week! You're building excellent digital wellness habits.")
        }
        
        return insights.take(3) // Return up to 3 insights
    }
}
