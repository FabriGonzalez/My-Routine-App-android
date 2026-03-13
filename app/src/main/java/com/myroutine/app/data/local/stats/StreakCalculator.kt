package com.myroutine.app.data.local.stats

import java.util.Calendar

data class WeekKey(
    val year: Int,
    val week: Int
)

fun getWeekKey(timestamp: Long): WeekKey {

    val cal = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    return WeekKey(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.WEEK_OF_YEAR)
    )
}

fun groupTrainingsByWeek(
    sessions: List<Long>
): Map<WeekKey, Int> {

    return sessions
        .groupBy { getWeekKey(it) }
        .mapValues { it.value.size }
}

fun calculateCurrentStreak(
    sessions: List<Long>,
    routineDayPerWeek: Int
): Int{

    val weeks = groupTrainingsByWeek(sessions)
    val calendar = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, -1)
    }
    var streak = 0

    while(true){

        val key = WeekKey(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.WEEK_OF_YEAR)
        )

        val trainings = weeks[key] ?: 0

        if(trainings >= routineDayPerWeek){
            streak++
        } else {
            break
        }

        calendar.add(Calendar.WEEK_OF_YEAR, - 1)
    }

    return streak
}

fun calculateBestStreak(
    sessions: List<Long>,
    routineDaysPerWeek: Int
): Int {

    val weeks = groupTrainingsByWeek(sessions)

    val lastCompletedWeek = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, -1)
    }

    val lastYear = lastCompletedWeek.get(Calendar.YEAR)
    val lastWeek = lastCompletedWeek.get(Calendar.WEEK_OF_YEAR)

    val filteredWeeks = weeks
        .filterKeys { key ->
            key.year < lastYear ||
                    (key.year == lastYear && key.week <= lastWeek)
        }
        .toSortedMap(compareBy({ it.year }, { it.week }))

    var best = 0
    var current = 0

    filteredWeeks.values.forEach {

        if (it >= routineDaysPerWeek) {
            current++
            best = maxOf(best, current)
        } else {
            current = 0
        }

    }

    return best
}

fun calculateCurrentWeekProgress(
    sessions: List<Long>
): Int {

    val calendar = Calendar.getInstance()

    val week = calendar.get(Calendar.WEEK_OF_YEAR)
    val year = calendar.get(Calendar.YEAR)

    return sessions.count {

        val key = getWeekKey(it)

        key.week == week && key.year == year
    }
}





