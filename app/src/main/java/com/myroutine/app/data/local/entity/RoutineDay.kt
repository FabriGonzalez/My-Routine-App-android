package com.myroutine.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_days")
data class RoutineDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String?,
    val orderIndex: Int
)

