package com.myroutine.app.di

import android.content.Context
import com.myroutine.app.data.local.AppDatabase
import com.myroutine.app.data.local.dao.ExerciseDao
import com.myroutine.app.data.local.dao.RoutineDayDao
import com.myroutine.app.data.local.dao.TrainingHistoryDao
import com.myroutine.app.data.repositories.ExerciseRepository
import com.myroutine.app.data.repositories.RoutineDayRepository
import com.myroutine.app.data.repositories.SettingsRepository
import com.myroutine.app.data.repositories.TrainingHistoryRepository
import com.myroutine.app.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        AppDatabase.getDatabase(context)

    @Provides
    fun provideTrainingHistoryDao(db: AppDatabase): TrainingHistoryDao =
        db.trainingHistoryDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase) =
        db.exerciseDao()

    @Provides
    fun provideRoutineDayDao(db: AppDatabase): RoutineDayDao =
        db.routineDayDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ) = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: SettingsDataStore
    ) = SettingsRepository(dataStore)

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        dao: TrainingHistoryDao
    ) = TrainingHistoryRepository(dao)

    @Provides
    @Singleton
    fun provideExerciseRepository(
        dao: ExerciseDao
    ): ExerciseRepository = ExerciseRepository(dao)

    @Provides
    @Singleton
    fun provideRoutineDayRepository(
        dao: RoutineDayDao
    ): RoutineDayRepository = RoutineDayRepository(dao)
}