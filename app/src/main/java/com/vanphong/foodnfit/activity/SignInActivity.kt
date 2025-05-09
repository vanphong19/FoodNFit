package com.vanphong.foodnfit.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityChooseFoodBinding
import com.vanphong.foodnfit.viewModel.LoginViewModel
import com.vanphong.foodnfit.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel

        viewModel.navigateMain.observe(this, Observer { shouldNavigate ->
            if(shouldNavigate){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                viewModel.onNavigationComplete()
            }
        })

        viewModel.navigateSignUp.observe(this, Observer { shouldNavigate ->
            if(shouldNavigate){
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                viewModel.onNavigateSignUpComplete()
            }
        })
    }
}