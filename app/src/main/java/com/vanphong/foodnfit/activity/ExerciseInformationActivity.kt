package com.vanphong.foodnfit.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.databinding.ActivityExerciseInformationBinding
import com.vanphong.foodnfit.viewModel.ExerciseInfoViewModel

class ExerciseInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityExerciseInformationBinding
    private val viewModel: ExerciseInfoViewModel by viewModels()
    companion object {
        const val EXTRA_EXERCISE_ID = "exercise_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.infoViewModel = viewModel

        val exerciseId = intent.getIntExtra(EXTRA_EXERCISE_ID, -1)
        Log.d("ExerciseInfoActivity", "Received exerciseId = $exerciseId")

        if (exerciseId != -1) {
            viewModel.loadExerciseInfo(exerciseId)
        } else {
            Toast.makeText(this, "Không tìm thấy ID bài tập", Toast.LENGTH_LONG).show()
            finish()
        }
        observe()
        lifecycle.addObserver(binding.youtubePlayerView)
    }

    private fun observe(){
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.videoUrl.observe(this) { url ->
            url?.let { fullUrl ->
                binding.youtubePlayerView.addYouTubePlayerListener(object :
                    AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(fullUrl, 0f)
                    }
                })
            }
        }
    }
}