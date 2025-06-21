package com.vanphong.foodnfit.activity

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.transition.Visibility
import coil.load
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityFeedbackBinding
import com.vanphong.foodnfit.model.FeedbackRequest
import com.vanphong.foodnfit.util.ImageUtils
import com.vanphong.foodnfit.viewModel.FeedbackViewModel

class FeedbackActivity : BaseActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private val viewModel: FeedbackViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imgFi.load(it)
            binding.imgFi.visibility = View.VISIBLE
            binding.btnDeImgFi.visibility = View.VISIBLE
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.feedbackViewModel = viewModel

        binding.btnAttach.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnDeImgFi.setOnClickListener {
            selectedImageUri = null
            binding.imgFi.setImageDrawable(null) // Xoá ảnh
            binding.imgFi.visibility = View.GONE
            binding.btnDeImgFi.visibility = View.GONE
        }

        binding.btnSendInquiry.setOnClickListener {
            val message = binding.editTextInquiry.text.toString().trim()
            val selectedPurposeId = binding.radioGroupType.checkedRadioButtonId
            val selectedPurpose = if (selectedPurposeId != -1) {
                findViewById<RadioButton>(selectedPurposeId).text.toString()
            } else null

            var selectedInquiry: String? = null
            if (selectedPurposeId != R.id.rBtnOtherFi) {
                val selectedCategoryId = binding.radioGroupCategory.checkedRadioButtonId
                selectedInquiry = if (selectedCategoryId != -1) {
                    findViewById<RadioButton>(selectedCategoryId).text.toString()
                } else null

                if (selectedInquiry == null) {
                    Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (message.isEmpty() || selectedPurpose == null) {
                Toast.makeText(this, "Vui lòng nhập nội dung và chọn mục đích", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.setMessage(message)
            viewModel.setPurpose(selectedPurpose)
            viewModel.setInquiry(selectedInquiry ?: "")  // inquiry có thể rỗng nếu là "Khác"

            if (selectedImageUri != null) {
                val resizedFile = ImageUtils.resizeImage(selectedImageUri!!, this)
                if (resizedFile == null) {
                    Toast.makeText(this, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.uploadImage(resizedFile)

                viewModel.imageUrl.observe(this) { url ->
                    if (!url.isNullOrEmpty()) {
                        sendFeedback(url)
                        viewModel.imageUrl.removeObservers(this)
                    }
                }
            } else {
                sendFeedback(null)
            }
        }


        viewModel.createResult.observe(this) { result ->
            if (result != null){
                Toast.makeText(this,"Feedback sent successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this,"Feedback sent failed", Toast.LENGTH_SHORT).show()
            }
        }

        setUpRadioGroup()
    }

    private fun setUpRadioGroup(){
        binding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rBtnOtherFi -> {
                    binding.radioGroupCategory.visibility = View.GONE
                    binding.textViewCategory.visibility = View.GONE
                }
                else -> {
                    binding.radioGroupCategory.visibility = View.VISIBLE
                    binding.textViewCategory.visibility = View.VISIBLE
                }
            }
        }

    }
    private fun sendFeedback(imageUrl: String?) {
        val request = FeedbackRequest(
            purpose = viewModel.purpose.value.orEmpty(),
            inquiry = viewModel.inquiry.value.orEmpty(),
            message = viewModel.message.value.orEmpty(),
            imageUrl = imageUrl
        )

        viewModel.createFeedback(request)

        viewModel.createResult.observe(this) {
            Toast.makeText(this, "Gửi phản hồi thành công", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
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