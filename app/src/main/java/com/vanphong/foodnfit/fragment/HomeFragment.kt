package com.vanphong.foodnfit.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.FoodScannerActivity
import com.vanphong.foodnfit.activity.NotificationActivity
import com.vanphong.foodnfit.adapter.StepDetailDialog
import com.vanphong.foodnfit.databinding.FragmentHomeBinding
import com.vanphong.foodnfit.model.HourlyStepSummary
import com.vanphong.foodnfit.model.StepSummary
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel
        binding.tvDate.text = getCurrentVietnameseDate()
        updateIndicator()
        viewModel.loadTodaySteps()
        viewModel.startAutoRefresh()
        viewModel.loadCalories()
        viewModel.loadInfo()
        viewModel.loadWaterCups()
        setupStepDetailButton()
        observeStepsData()
        setUpMenu()
        return binding.root
    }

    private fun setUpMenu() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarHome)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_notification, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home_notification -> {
                        val intent = Intent(requireContext(), NotificationActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupStepDetailButton() {
        val stepDetailButton = binding.root.findViewById<Button>(R.id.btnStepDetail)

        stepDetailButton?.setOnClickListener {
            viewModel.loadHourlySteps()
        }

        viewModel.hourlySteps.observe(viewLifecycleOwner) { hourlyData ->
            if (hourlyData.isNotEmpty()) {
                showStepDetailDialog(hourlyData)
            }
        }
    }

    private fun showStepDetailDialog(hourlyData: List<HourlyStepSummary>) {
        val dialog = StepDetailDialog(requireContext(), hourlyData)
        dialog.show()
    }

    private fun updateIndicator() {
        val minValue = binding.bmiProgressbar.min.toFloat()
        val maxValue = binding.bmiProgressbar.max.toFloat()
        val currentValue = binding.tvBmi.text.toString().toFloatOrNull()
        val percent = if (currentValue != null && maxValue > minValue) {
            ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
        } else {
            0f
        }

        binding.indicatorGuideline.setGuidelinePercent(percent)
    }

    private fun getCurrentVietnameseDate(): String{
        val currentDate = LocalDate.now()
        val vietnameseLocale = Locale("vi","VN")
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'tháng' M", vietnameseLocale)
        return currentDate.format(formatter)
    }

    @SuppressLint("SetTextI18n")
    private fun observeStepsData() {
        // Observe steps data
        viewModel.todaySteps.observe(viewLifecycleOwner) { summary ->
            updateUI(summary)
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showError(error)
            }
        }

        var tdeeValue = 0f
        var caloriesInValue = 0f

        viewModel.caloriesIn.observe(viewLifecycleOwner){
            binding.tvCaloriesIn.text = it
            caloriesInValue = it.toFloat()
            binding.caloProgressBar.progress = caloriesInValue
        }

        viewModel.caloriesOut.observe(viewLifecycleOwner){
            binding.tvCaloriesOut.text = it
        }

        viewModel.tdee.observe(viewLifecycleOwner){
            tdeeValue = it.toFloat()
            binding.tvTDEE.text = it
            binding.caloProgressBar.progressMax = tdeeValue
            binding.caloProgressBar.progress = caloriesInValue
        }

        viewModel.carbs.observe(viewLifecycleOwner){
            binding.tvCarbs.text = it
        }
        viewModel.protein.observe(viewLifecycleOwner){
            binding.tvProtein.text = it
        }
        viewModel.fat.observe(viewLifecycleOwner){
            binding.tvFat.text = it
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.height.observe(viewLifecycleOwner){
            binding.tvHeight.text = "$it cm"
        }

        viewModel.weight.observe(viewLifecycleOwner){
            binding.tvWeight.text = "$it kg"
        }

        viewModel.bmi.observe(viewLifecycleOwner) { bmiValue ->
            val bmi = bmiValue.toDoubleOrNull()
            val bmiRounded = bmi?.let { String.format("%.1f", it) } ?: ""

            binding.tvBmi.text = bmiRounded

            val category = when {
                bmi == null -> ""
                bmi < 18.5 -> getString(R.string.underweight)
                bmi < 25 -> getString(R.string.normal)
                bmi < 30 -> getString(R.string.overweight)
                bmi < 35 -> getString(R.string.severe_obesity)
                bmi < 40 -> getString(R.string.morbid_obesity)
                else -> getString(R.string.super_obesity)
            }

            binding.tvBmiText.text = category

            if (bmi != null) {
                binding.indicatorGuideline.setGuidelinePercent((bmi / 100).toFloat())
            }
        }
        updateWaterCup()
    }

    private fun updateWaterCup() {
        val progressBar = binding.waterProgressBar
        val tvCups = binding.tvCups
        val imgPlus = binding.imgPlus
        val imgMinus = binding.imgMinus

        val maxCups = 8
        progressBar.progressMax = maxCups.toFloat()

        viewModel.cups.observe(viewLifecycleOwner) { cups ->
            tvCups.text = cups.toString()
            progressBar.progress = cups.toFloat()
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        imgPlus.setOnClickListener {
            viewModel.increaseCup()
        }

        imgMinus.setOnClickListener {
            viewModel.decreaseCup()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(summary: StepSummary) {
        binding.apply {
            tvSteps.text = summary.totalSteps.toString()

            val target = 2000
            stepProgressBar.progressMax = target.toFloat()
            stepProgressBar.progress = summary.totalSteps.toFloat()
            tvStepsTarget.text = "/ ${target}"
        }
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        binding.tvDate.text = getCurrentVietnameseDate()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}