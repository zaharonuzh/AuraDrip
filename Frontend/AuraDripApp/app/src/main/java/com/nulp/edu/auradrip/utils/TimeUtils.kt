package com.nulp.edu.auradrip.utils

import android.content.Context
import com.nulp.edu.auradrip.R
import java.time.Instant
import java.time.Duration

fun String?.toTimeAgo(context: Context): String {
    if (this == null) return context.getString(R.string.time_ago_waiting)
    
    return try {
        val instant = Instant.parse(this)
        val now = Instant.now()
        val duration = Duration.between(instant, now)
        
        val minutes = duration.toMinutes().toInt()
        val hours = duration.toHours().toInt()
        val days = duration.toDays().toInt()
        
        when {
            minutes < 1 -> context.getString(R.string.time_ago_just_now)
            minutes < 60 -> context.resources.getQuantityString(R.plurals.time_ago_minutes, minutes, minutes)
            hours < 24 -> context.resources.getQuantityString(R.plurals.time_ago_hours, hours, hours)
            else -> context.resources.getQuantityString(R.plurals.time_ago_days, days, days)
        }
    } catch (e: Exception) {
        context.getString(R.string.time_ago_unknown)
    }
}
