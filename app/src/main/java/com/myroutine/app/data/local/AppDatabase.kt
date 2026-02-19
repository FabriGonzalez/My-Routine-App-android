package com.myroutine.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myroutine.app.data.local.dao.ExerciseDao
import com.myroutine.app.data.local.dao.RoutineDayDao
import com.myroutine.app.data.local.dao.TrainingHistoryDao
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.local.entity.RoutineDay
import com.myroutine.app.data.local.entity.TrainingHistory

@Database(
    entities = [RoutineDay::class, Exercise::class, TrainingHistory::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun routineDayDao(): RoutineDayDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingHistoryDao(): TrainingHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myroutine_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

