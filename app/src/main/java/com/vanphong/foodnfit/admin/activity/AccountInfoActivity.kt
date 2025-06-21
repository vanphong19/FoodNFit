package com.vanphong.foodnfit.admin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.impl.Observable.Observer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.adapter.UserHistoryAdapter
import com.vanphong.foodnfit.admin.viewModel.AccountInfoViewModel
import com.vanphong.foodnfit.databinding.ActivityAccountInfoBinding
import com.vanphong.foodnfit.util.DialogUtils

class AccountInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInfoBinding
    private val viewModel: AccountInfoViewModel by viewModels()
    private lateinit var userHistoryAdapter: UserHistoryAdapter
    private var loadingDialog: AlertDialog? = null
    private var userId: String? = null

    private val accountInfoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            viewModel.getAccountById(userId!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.accountInfoViewModel = viewModel
        userId = intent.getStringExtra("user_id")
        userId?.let {
            viewModel.getAccountById(it)
        }
        setUI()
        setRvHistory()
        setupListeners()
        observeViewModel()
        viewModel.blocked.observe(this){block ->
            if(block){
                binding.btnBlock.text = getString(R.string.un_blocked)
            }
            else binding.btnBlock.text = getString(R.string.blocked)
        }
    }

    private fun setRvHistory(){
        userHistoryAdapter = UserHistoryAdapter()
        binding.rvUserHistories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.accountHistory.observe(this) { historyList ->
            userHistoryAdapter.submitList(historyList)
        }
        binding.rvUserHistories.adapter = userHistoryAdapter
    }

    private fun setUI(){
        viewModel.avatar.observe(this) { avatarUrl ->
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.male)  // hoặc female tùy giới tính
                .error(R.drawable.male)
                .into(binding.imgAvatar)
        }

        viewModel.fullname.observe(this) { name ->
            binding.tvFullName.text = name
        }

        viewModel.email.observe(this) { email ->
            binding.tvEmail.text = email
        }

        viewModel.gender.observe(this) { gender ->
            binding.tvGender.text = if (gender == true) getString(R.string.female) else getString(R.string.male)
        }

        viewModel.birthday.observe(this) { birthday ->
            binding.tvBirthday.text = birthday ?: "Không rõ"
        }

        viewModel.isLoading.observe(this){loading ->
            if(loading){
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.notify.observe(this){message ->
            if(message != null){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            else{
                Toast.makeText(this, getString(R.string.remove_account_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBlock.setOnClickListener {
            userId?.let {
                viewModel.lockAccount(it)
            }
        }
        binding.btnDelete.setOnClickListener {
            userId?.let {
                viewModel.remove(it)
            }
        }
        binding.btnUpdate.setOnClickListener {
            val intent = Intent(this, AddEditAccountActivity::class.java)
            intent.putExtra("user_id", userId)
            accountInfoLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoadingDialog(getString(R.string.processing)) else hideLoadingDialog()
            binding.btnBlock.isEnabled = !isLoading
        }

        viewModel.lockAccountResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Khoá tài khoản thành công", Toast.LENGTH_SHORT).show()
                // Có thể load lại thông tin user hoặc quay về
                userId?.let {
                    viewModel.getAccountById(it)
                }
            }
            result.onFailure {
                Toast.makeText(this, "Lỗi khi khoá tài khoản: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
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
    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}