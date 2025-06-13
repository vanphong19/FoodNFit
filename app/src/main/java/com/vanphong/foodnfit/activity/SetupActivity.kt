package com.vanphong.foodnfit.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.SetupViewPagerAdapter
import com.vanphong.foodnfit.databinding.ActivitySetupBinding

class SetupActivity : BaseActivity() {
    private var _binding: ActivitySetupBinding? = null
    private val binding get() = _binding!!
    private lateinit var setupViewPager: SetupViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPagerSetup.isUserInputEnabled = false
        viewPager = binding.viewPagerSetup
        setUpViewPager()
    }

    private fun setUpViewPager(){
        setupViewPager = SetupViewPagerAdapter(this)
        viewPager.adapter = setupViewPager

        binding.btnNextSetup.setOnClickListener {
            val current = viewPager.currentItem
            if(current < setupViewPager.itemCount - 1){
                viewPager.currentItem = current + 1
                binding.setupProgressBar.setProgressWithAnimation((current + 2).toFloat())
                binding.btnNextSetup.text = ""
                binding.btnSkip.text = getString(R.string.skip)
            }
            else{
                binding.btnNextSetup.text = getString(R.string.start)
                binding.btnSkip.text = getString(R.string.sign_in)
            }
        }


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.setupProgressBar.setProgressWithAnimation((position + 1).toFloat())
            }
        })
    }
}