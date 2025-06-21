package com.vanphong.foodnfit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.model.OnboardingItem
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.OnboardingAdapter
import com.vanphong.foodnfit.databinding.ActivityOnboardingBinding

class OnboardingActivity : BaseActivity() {
    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.isUserInputEnabled = false

        val list = getData()

        adapter = OnboardingAdapter(list)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter

        formatViewPager(list)

        nextPage(list)

        skipPage()
    }

    private fun formatViewPager(list: List<OnboardingItem>) {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setText(list[position])
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == list.lastIndex) {
// Đang ở trang cuối
                    binding.btnNext.icon = null
                    binding.btnNext.text = getString(R.string.start)
                    binding.btnSkip.text = getString(R.string.sign_in)
                } else {
// Không phải trang cuối
                    binding.btnNext.icon = ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.ic_play_arrow)
                    binding.btnNext.text = ""
                    binding.btnSkip.text = getString(R.string.skip)
                }

                val progress = (position + 1)
                binding.nextProgressBar.setProgressWithAnimation(progress.toFloat(), 500L)
            }
        })
    }

    private fun getData(): List<OnboardingItem> {
        return listOf(
            OnboardingItem(imageRes = R.drawable.onboarding_diet, getString(R.string.onboarding_title_1), getString(R.string.onboarding_desc_1)),
            OnboardingItem(R.drawable.onboarding_workout, getString(R.string.onboarding_title_2), getString(R.string.onboarding_desc_2)),
            OnboardingItem(R.drawable.onboarding_success, getString(R.string.onboarding_title_3), getString(R.string.onboarding_desc_3))
        )
    }

    private fun nextPage(list: List<OnboardingItem>) {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < list.lastIndex) {
                binding.viewPager.currentItem += 1
                setText(list[binding.viewPager.currentItem])
            } else {
                setStatus()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }

    private fun setText(item: OnboardingItem){
        binding.titleText.text = item.title
        binding.descText.text = item.desc
    }

    private fun skipPage() {
        binding.btnSkip.setOnClickListener {
            setStatus()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun setStatus(){
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", true)
            .apply()
    }
}