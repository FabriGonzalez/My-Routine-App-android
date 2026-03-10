package com.myroutine.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myroutine.app.data.local.converters.Converters
import com.myroutine.app.data.local.dao.ExerciseDao
import com.myroutine.app.data.local.dao.ExerciseHistoryDao
import com.myroutine.app.data.local.dao.RoutineDayDao
import com.myroutine.app.data.local.dao.TrainingSessionDao
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.local.entity.ExerciseHistory
import com.myroutine.app.data.local.entity.RoutineDay
import com.myroutine.app.data.local.entity.TrainingSession

@Database(
    entities = [RoutineDay::class, Exercise::class, TrainingSession::class, ExerciseHistory::class],
    version = 4,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun routineDayDao(): RoutineDayDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun exerciseHistoryDao(): ExerciseHistoryDao

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

