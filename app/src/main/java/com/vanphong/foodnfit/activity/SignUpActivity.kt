package com.vanphong.foodnfit.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivitySignUpBinding
import com.vanphong.foodnfit.viewModel.SignUpViewModel

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.signUpViewModel = viewModel

        binding.btnSignUp.setOnClickListener {
            showLoadingDialog(getString(R.string.sending_verification))
            viewModel.registerUser()
        }

        viewModel.navigateToOtp.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                dismissLoadingDialog()
                val intent = Intent(this, VerifyActivity::class.java)
                intent.putExtra("email", viewModel.email.value)
                startActivity(intent)
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                dismissLoadingDialog()
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var loadingDialog: AlertDialog? = null

    private fun showLoadingDialog(message: String) {
        if (loadingDialog?.isShowing == true) return

        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_loading)
        builder.setCancelable(false)

        loadingDialog = builder.create()
        loadingDialog?.show()

        // Nếu muốn hiển thị nội dung text:
        loadingDialog?.findViewById<TextView>(R.id.loading_message)?.text = message
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}