package com.vanphong.foodnfit.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vanphong.foodnfit.model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.ExerciseInformationActivity
import com.vanphong.foodnfit.activity.ExerciseListActivity
import com.vanphong.foodnfit.adapter.CalendarAdapter
import com.vanphong.foodnfit.adapter.ExerciseAdapter
import com.vanphong.foodnfit.databinding.FragmentExerciseBinding
import com.vanphong.foodnfit.model.DailyCaloriesExerciseDto
import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.util.CalendarUtils
import com.vanphong.foodnfit.util.DayAxisValueFormatter
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class ExerciseFragment : Fragment() { // Removed CalendarAdapter.OnItemListener, it's unused
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var exerciseAdapter: ExerciseAdapter
    private val viewModel: ExerciseViewModel by viewModels()
    private lateinit var chooseFoodLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chooseFoodLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("FoodFragment", "Received RESULT_OK from ChooseFoodActivity. Reloading data.")
                viewModel.fetchDataForDate(CalendarUtils.selectedDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner // Set lifecycle owner for LiveData binding
        binding.exerciseViewModel = viewModel // Bind viewModel to layout

        // Setup UI components
        previousWeekAction()
        nextWeekAction()
        setWeekView() // Initial view setup
        setUpMenu()
        setExerciseAdapter()
        observeNavigation()
        observeChartData()

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Reminder created successfully!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Failed to create reminder: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun observeChartData() {
        viewModel.exerciseChartData.observe(viewLifecycleOwner) { chartData ->
            if (chartData != null && chartData.entries.isNotEmpty()) {
                // Truyền dữ liệu đã được xử lý sẵn vào hàm vẽ
                setupLineChart(chartData.entries, chartData.labels)
                binding.lineChart.visibility = View.VISIBLE
            } else {
                binding.lineChart.visibility = View.GONE
            }
        }
    }

    private fun setupLineChart(entries: List<Entry>, labels: List<String>) {
        val dataSet = LineDataSet(entries, "Calories Per Exercise")

        // === Các tùy chỉnh giao diện có thể giữ nguyên ===
        dataSet.color = Color.parseColor("#FF9966")
        dataSet.valueTextColor = Color.BLACK
        dataSet.setCircleColor(Color.parseColor("#FF9966"))
        dataSet.lineWidth = 2f
        dataSet.valueTextSize = 10f
        dataSet.setDrawValues(true)

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData

        // === Cấu hình trục X để hiển thị TÊN BÀI TẬP ===
        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f
        xAxis.valueFormatter = DayAxisValueFormatter(labels)

        // ... các cấu hình khác ...
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.description.isEnabled = false
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)

        binding.lineChart.invalidate()
    }

    private fun observeNavigation() {
        viewModel.navigateExerciseInfo.observe(viewLifecycleOwner) { exerciseId ->
            exerciseId?.let {
                Log.d("idahbfkjadfkj", it.toString())
                val intent = Intent(requireContext(), ExerciseInformationActivity::class.java)
                intent.putExtra(ExerciseInformationActivity.EXTRA_EXERCISE_ID, it)
                startActivity(intent)
                viewModel.completeNavigateExerciseInfo()
            }
        }

        viewModel.navigateExerciseList.observe(viewLifecycleOwner) { navigate ->
            if (navigate == true) { // Check for true to avoid multiple navigations
                val intent = Intent(requireContext(), ExerciseListActivity::class.java)
                chooseFoodLauncher.launch(intent)
                viewModel.completeNavigateExerciseList()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }
    }


    private fun setExerciseAdapter(){
        exerciseAdapter = ExerciseAdapter(
            onClick = { exerciseId ->
                viewModel.onClickNavigateExerciseInfo(exerciseId)
            },
            onLongClick = { position ->
                showConfirmDialog(position)
            }
        )

        // Observe the list from ViewModel and submit it to the adapter
        viewModel.addExerciseList.observe(viewLifecycleOwner){ addList ->
            exerciseAdapter.submitList(addList)
        }

        binding.rvAddExercise.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAddExercise.adapter = exerciseAdapter
    }

    private fun showConfirmDialog(position: Int){
        val parent = requireActivity().window.decorView as ViewGroup
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_complete_exercise_dialog, parent, false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes

        dialog.setCanceledOnTouchOutside(false)

        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm_dialog)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        btnConfirm.setOnClickListener {
            // **CORRECTED:** Call the ViewModel function to handle the logic
            viewModel.markExerciseAsCompleted(position)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setUpMenu(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarExercise)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.exercise_reminder, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.exercise_reminder ->{
                        showCreateReminderDialog()
                        true
                    }
                    else -> false
                }
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setWeekView(){
        val selectedDate = CalendarUtils.selectedDate
        binding.tvMonthYear.text = CalendarUtils.monthYearFromDate(selectedDate)

        val days: ArrayList<LocalDate> = CalendarUtils.daysInWeekArray(selectedDate)

        calendarAdapter = CalendarAdapter(days, object : CalendarAdapter.OnItemListener {
            override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
                if (date != null) {
                    CalendarUtils.selectedDate = date
                    setWeekView() // Re-render the week view and fetch new data
                }
            }
        })

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = calendarAdapter

        // **KEY CHANGE**: Fetch data for the newly selected date
        viewModel.fetchDataForDate(selectedDate)
    }

    private fun showCreateReminderDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reminder, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
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


    private fun previousWeekAction(){
        binding.btnBackCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1)
            setWeekView()
        }
    }

    private fun nextWeekAction(){
        binding.btnNextCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1)
            setWeekView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to avoid memory leaks
    }
}