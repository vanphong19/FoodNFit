package com.vanphong.foodnfit.interfaces

import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.WorkoutExercises

interface OnExerciseListener {
    fun onExerciseAdded(exercise: Exercises)

    fun onExerciseRemoved(workoutExercise: WorkoutExercises)
}