package com.myroutine.app.di

import android.content.Context
import com.myroutine.app.data.local.AppDatabase
import com.myroutine.app.data.local.dao.ExerciseDao
import com.myroutine.app.data.local.dao.ExerciseHistoryDao
import com.myroutine.app.data.local.dao.RoutineDayDao
import com.myroutine.app.data.local.dao.TrainingSessionDao
import com.myroutine.app.data.repositories.ExerciseHistoryRepository
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
    fun provideTrainingSessionDao(db: AppDatabase): TrainingSessionDao =
        db.trainingSessionDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao =
        db.exerciseDao()

    @Provides
    fun provideRoutineDayDao(db: AppDatabase): RoutineDayDao =
        db.routineDayDao()

    @Provides
    fun provideExerciseHistoryDao(db: AppDatabase): ExerciseHistoryDao =
        db.exerciseHistoryDao()


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
    fun provideTrainingHistoryRepository(
        dao: TrainingSessionDao
    ) = TrainingHistoryRepository(dao)

    @Provides
    @Singleton
    fun provideExerciseRepository(
        dao: ExerciseDao
    ) = ExerciseRepository(dao)

    @Provides
    @Singleton
    fun provideRoutineDayRepository(
        dao: RoutineDayDao
    ) = RoutineDayRepository(dao)

    @Provides
    @Singleton
    fun provideExerciseHistoryRepository(
        dao: ExerciseHistoryDao
    ) = ExerciseHistoryRepository(dao)
}