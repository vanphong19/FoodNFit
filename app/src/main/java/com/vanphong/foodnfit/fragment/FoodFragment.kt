package com.vanphong.foodnfit.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.CalendarAdapter
import com.vanphong.foodnfit.adapter.FoodAdapter
import com.vanphong.foodnfit.databinding.FragmentFoodBinding
import com.vanphong.foodnfit.util.CalendarUtils
import com.vanphong.foodnfit.viewModel.FoodViewModel
import java.time.LocalDate

class FoodFragment : Fragment(), CalendarAdapter.OnItemListener {
    private var _binding:FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel:FoodViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_food,container, false)
        previousWeekAction()
        nextWeekAction()
        setWeekView()
        setUpMenu()
        return binding.root
    }

    private fun setUpMenu(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarFood)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.meal_reminder, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.meal_reminder ->{
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setWeekView(){
        val selectedDate = CalendarUtils.selectedDate
        binding.tvMonthYear.text = CalendarUtils.monthYearFromDate(selectedDate)

        val days: ArrayList<LocalDate> = CalendarUtils.daysInWeekArray(selectedDate)

        calendarAdapter = CalendarAdapter(days, object : CalendarAdapter.OnItemListener {
            override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
                if (date != null) {
                    CalendarUtils.selectedDate = date
                    setWeekView()
                }
            }
        })

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7, LinearLayoutManager.VERTICAL, false)
        binding.rvCalendar.adapter = calendarAdapter
        Log.d("CalendarCheck", "Adapter set with item count: ${calendarAdapter.itemCount}")
        Log.d("CalendarCheck", "Days: $days")
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
    override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
        if(dayText != ""){
            val message = "Selected Date " + dayText + " " + CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}