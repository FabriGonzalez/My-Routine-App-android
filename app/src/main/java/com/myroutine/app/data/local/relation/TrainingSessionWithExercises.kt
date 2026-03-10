package com.myroutine.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.myroutine.app.data.local.entity.TrainingSession
import com.myroutine.app.data.local.entity.ExerciseHistory

data class TrainingSessionWithExercises(

    @Embedded
    val session: TrainingSession,

    @Relation(
        parentColumn = "idTrainingSession",
        entityColumn = "sessionId"
    )
    val exercises: List<ExerciseHistory>
)