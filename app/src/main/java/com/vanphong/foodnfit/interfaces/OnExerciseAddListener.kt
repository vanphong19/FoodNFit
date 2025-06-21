package com.vanphong.foodnfit.interfaces

import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.WorkoutExercises

interface OnExerciseAddListener {
    fun onAddExerciseByMinute(exercise: Exercises, minutes: Int)
    fun onAddExerciseBySetRep(exercise: Exercises, sets: Int, reps: Int)
    fun onRemoveExercise(workoutExercise: WorkoutExercises)
}