package com.vanphong.foodnfit.googlefit

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness

class GoogleFitClient(private val context: Context) {
    private val REQUEST_OAUTH_REQUEST_CODE = 1001

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build()

    fun requestPermissions() {
        val activity = context as Activity

        GoogleSignIn.requestPermissions(
            activity,
            REQUEST_OAUTH_REQUEST_CODE,
            GoogleSignIn.getAccountForExtension(context, fitnessOptions),
            fitnessOptions
        )
    }

    fun subscribeToStepData() {
        Fitness.getRecordingClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.d("GoogleFit", "Successfully subscribed to step data")
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFit", "Error subscribing to step data", e)
            }
    }
}