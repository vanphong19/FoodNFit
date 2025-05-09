package com.vanphong.foodnfit.interfaces

import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises

interface OnExerciseAddListener {
    fun onAddExerciseByMinute(exercise: Exercises, minutes: Int)
    fun onAddExerciseBySetRep(exercise: Exercises, sets: Int, reps: Int)
    fun onRemoveExercise(workoutExercise: WorkoutExercises)
}