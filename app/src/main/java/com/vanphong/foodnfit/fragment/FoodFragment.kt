package com.vanphong.foodnfit.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.ChooseFoodActivity
import com.vanphong.foodnfit.activity.FoodScannerActivity
import com.vanphong.foodnfit.activity.MealInformationActivity
import com.vanphong.foodnfit.adapter.CalendarAdapter
import com.vanphong.foodnfit.adapter.CalendarFoodAdapter
import com.vanphong.foodnfit.adapter.FoodAdapter
import com.vanphong.foodnfit.databinding.FragmentFoodBinding
import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.util.CalendarUtils
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.FoodViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class FoodFragment : Fragment(), CalendarAdapter.OnItemListener {
    private var _binding:FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel:FoodViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarFoodAdapter

    private lateinit var chooseFoodLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chooseFoodLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("FoodFragment", "Received RESULT_OK from ChooseFoodActivity. Reloading data.")
                viewModel.loadFoodLogsForDate(CalendarUtils.selectedDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_food,container, false)
        binding.lifecycleOwner = this
        binding.foodViewModel = viewModel

        setupObservers()
        setupCalendar()
        setUpMenu()
        setupClickListeners()

        return binding.root
    }


    private fun setupObservers() {
        // Observe food logs data
        viewModel.foodLogs.observe(viewLifecycleOwner) { logs ->
            Log.d("FoodFragment", "Food logs updated: ${logs.size} items")
            updateUI()
        }

        // Observe nutrition data
        viewModel.totalCalories.observe(viewLifecycleOwner) { calories ->
            updateCaloriesDisplay(calories)
        }

        viewModel.totalProtein.observe(viewLifecycleOwner) { protein ->
            updateProteinDisplay(protein)
        }

        viewModel.totalFat.observe(viewLifecycleOwner) { fat ->
            updateFatDisplay(fat)
        }

        viewModel.totalCarbs.observe(viewLifecycleOwner) { carbs ->
            updateCarbsDisplay(carbs)
        }

        // Observe meal data
        viewModel.breakfastData.observe(viewLifecycleOwner) { breakfast ->
            updateMealDisplay("breakfast", breakfast)
        }

        viewModel.lunchData.observe(viewLifecycleOwner) { lunch ->
            updateMealDisplay("lunch", lunch)
        }

        viewModel.dinnerData.observe(viewLifecycleOwner) { dinner ->
            updateMealDisplay("dinner", dinner)
        }

        viewModel.snackData.observe(viewLifecycleOwner) { snack ->
            updateMealDisplay("snack", snack)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // You can show/hide loading indicator here
            Log.d("FoodFragment", "Loading: $isLoading")
        }

        // Observe error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Navigation observers
        viewModel.navigationChooseFood.observe(viewLifecycleOwner) { mealType  ->
            mealType?.let {
                val intent = Intent(requireContext(), ChooseFoodActivity::class.java)
                intent.putExtra("MEAL_TYPE", it) // Truyền loại bữa ăn
                chooseFoodLauncher.launch(intent)
                viewModel.onChooseFoodComplete()
            }
        }

        viewModel.navigateToMealDetail.observe(viewLifecycleOwner) { mealLogId ->
            mealLogId?.let {
                val intent = Intent(requireContext(), MealInformationActivity::class.java).apply {
                    // Gửi ID của FoodLog qua Intent
                    putExtra("MEAL_LOG_ID", it)
                }
                startActivity(intent)
                // Reset LiveData để tránh việc điều hướng lại khi xoay màn hình
                viewModel.onMealDetailNavigationComplete()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            }
            else {
                DialogUtils.hideLoadingDialog()
            }
        }
        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Reminder created successfully!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Failed to create reminder: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCaloriesDisplay(calories: Double) {
        // Update the main calories display
        binding.semiCircleArcProgressBar.setPercent(viewModel.getCaloriesProgress())

        // Find and update the calories TextView (you might need to adjust the ID)
        val caloriesText = binding.root.findViewById<android.widget.TextView>(R.id.tv_current_calories)
        caloriesText?.text = calories.toInt().toString()

        val targetCalories = viewModel.targetCalories.value ?: 2598.0
        val targetText = binding.root.findViewById<android.widget.TextView>(R.id.tv_target_calories)
        targetText?.text = "of ${targetCalories.toInt()} kcal"
    }

    private fun updateProteinDisplay(protein: Double) {
        val proteinProgress = binding.root.findViewById<android.widget.ProgressBar>(R.id.protein_progress)
        val proteinText = binding.root.findViewById<android.widget.TextView>(R.id.tv_protein_amount)

        val targetProtein = viewModel.targetProtein.value ?: 100
        proteinProgress?.max = targetProtein.toInt()

        proteinProgress?.progress = viewModel.getProteinProgress()
        proteinText?.text = "${protein.toInt()} g"
    }

    private fun updateFatDisplay(fat: Double) {
        val fatProgress = binding.root.findViewById<android.widget.ProgressBar>(R.id.fat_progress)
        val fatText = binding.root.findViewById<android.widget.TextView>(R.id.tv_fat_amount)

        val targetFat = viewModel.targetFat.value ?: 100
        fatProgress?.max = targetFat.toInt()

        fatProgress?.progress = viewModel.getFatProgress()
        fatText?.text = "${fat.toInt()} g"
    }

    private fun updateCarbsDisplay(carbs: Double) {
        val carbsProgress = binding.root.findViewById<android.widget.ProgressBar>(R.id.carbs_progress)
        val carbsText = binding.root.findViewById<android.widget.TextView>(R.id.tv_carbs_amount)

        val targetCarbs = viewModel.targetCarbs.value ?: 100
        carbsProgress?.max = targetCarbs.toInt()

        carbsProgress?.progress = viewModel.getCarbsProgress()
        carbsText?.text = "${carbs.toInt()} g"
    }

    private fun updateMealDisplay(mealType: String, mealData: com.vanphong.foodnfit.model.FoodLogResponse?) {
        val mealSummary = viewModel.getMealSummary(mealType)

        when (mealType) {
            "breakfast" -> {
                binding.tvFoodBreakfastCount.text = mealSummary
            }
            "lunch" -> {
                binding.tvFoodLunchCount.text = mealSummary
            }
            "dinner" -> {
                binding.tvFoodDinnerCount.text = mealSummary
            }
            "snack" -> {
                binding.tvFoodSnackCount.text = mealSummary
            }
        }
    }

    private fun updateUI() {
        Log.d("FoodFragment", "UI updated with latest data")
    }

    private fun setupCalendar() {
        previousWeekAction()
        nextWeekAction()
        setWeekView()
    }

    private fun setupClickListeners() {
        // You can add additional click listeners here if needed
    }

    private fun setUpMenu() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarFood)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.meal_reminder, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.meal_reminder -> {
                        showCreateReminderDialog()
                        true
                    }
                    else -> {
                        val intent = Intent(requireContext(), FoodScannerActivity::class.java)
                        startActivity(intent)
                        true
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setWeekView() {
        val selectedDate = CalendarUtils.selectedDate
        binding.tvMonthYear.text = CalendarUtils.monthYearFromDate(selectedDate)

        val days: ArrayList<LocalDate> = CalendarUtils.daysInWeekArray(selectedDate)

        calendarAdapter = CalendarFoodAdapter(days, object : CalendarFoodAdapter.OnItemListener {
            override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
                if (date != null) {
                    CalendarUtils.selectedDate = date
                    setWeekView()
                    // Load food logs for the selected date
                    viewModel.loadFoodLogsForDate(date)
                }
            }
        })

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7, LinearLayoutManager.VERTICAL, false)
        binding.rvCalendar.adapter = calendarAdapter
        Log.d("CalendarCheck", "Adapter set with item count: ${calendarAdapter.itemCount}")
        Log.d("CalendarCheck", "Days: $days")
    }

    private fun previousWeekAction() {
        binding.btnBackCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1)
            setWeekView()
            // Load food logs for the new selected date
            viewModel.loadFoodLogsForDate(CalendarUtils.selectedDate)
        }
    }

    private fun nextWeekAction() {
        binding.btnNextCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1)
            setWeekView()
            // Load food logs for the new selected date
            viewModel.loadFoodLogsForDate(CalendarUtils.selectedDate)
        }
    }

    override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showCreateReminderDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reminder, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val etReminderType = dialogView.findViewById<EditText>(R.id.spinner_reminder_type)
        val etMessage = dialogView.findViewById<EditText>(R.id.et_message)
        val etDate = dialogView.findViewById<EditText>(R.id.et_date)
        val etTime = dialogView.findViewById<EditText>(R.id.et_time)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnCreate = dialogView.findViewById<Button>(R.id.btn_create)

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    etDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    etTime.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.performClick() // 👈 Gọi để tránh warning
                val currentFocus = dialog.currentFocus ?: v
                v.context.hideKeyboard(currentFocus)
                v.clearFocus()
            }
            false
        }

        btnCreate.setOnClickListener {
            val type = etReminderType.text.toString()
            val message = etMessage.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (type.isBlank() || message.isBlank() || date.isBlank() || time.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            } else {
                val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateTimeStr = "$date $time"

                try {
                    val parsedDate = inputFormat.parse(dateTimeStr)
                    val formattedTime = outputFormat.format(parsedDate!!)

                    val request = ReminderRequest(
                        reminderType = type,
                        message = message,
                        scheduledTime = formattedTime,
                        frequency = "custom"
                    )

                    viewModel.createReminder(request)

                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Invalid date/time format", Toast.LENGTH_SHORT).show()
                }
            }
        }


        dialog.show()
    }

    fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showPopupMenu(anchor: TextView, options: List<String>) {
        val popup = PopupMenu(requireContext(), anchor)
        options.forEachIndexed { index, option ->
            popup.menu.add(Menu.NONE, index, index, option)
        }
        popup.setOnMenuItemClickListener { item ->
            anchor.text = options[item.itemId]
            true
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}