package com.vanphong.foodnfit.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityExerciseInformationBinding
import com.vanphong.foodnfit.viewModel.ExerciseInfoViewModel

class ExerciseInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityExerciseInformationBinding
    private val viewModel: ExerciseInfoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.infoViewModel = viewModel

        val id = 5
        viewModel.loadExerciseInfo(id)
        observe()
        lifecycle.addObserver(binding.youtubePlayerView)
    }

    private fun observe(){
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}