package com.vanphong.foodnfit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView // Sử dụng SearchView của androidx
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.ChooseFoodAdapter
import com.vanphong.foodnfit.adapter.FoodAdapter
import com.vanphong.foodnfit.component.GridSpacingItemDecoration
import com.vanphong.foodnfit.databinding.ActivityChooseFoodBinding
import com.vanphong.foodnfit.model.FoodItemResponse // Import đúng model
import com.vanphong.foodnfit.viewModel.ChooseFoodViewModel
import com.vanphong.foodnfit.viewModel.FoodViewModel

class ChooseFoodActivity : BaseActivity() {
    private var _binding: ActivityChooseFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChooseFoodViewModel by viewModels()
    private var isGridLayout = false
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var chooseFoodAdapter: ChooseFoodAdapter
    private var mealType: String = "Breakfast" // Mặc định, nên được truyền từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChooseFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mealType = intent.getStringExtra("MEAL_TYPE") ?: "BREAKFAST"

        binding.lifecycleOwner = this
        binding.foodViewModel = viewModel

        setupUI()
        setupRecyclerViews()
        setupObservers()

        viewModel.loadInitialData()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbarFood)
        binding.toolbarFood.title = mealType

        val sharePrefs = getSharedPreferences("food_preferences", Context.MODE_PRIVATE)
        isGridLayout = sharePrefs.getBoolean("isGridLayout", false)

        binding.searchFood.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query) // Gọi hàm search mới
                binding.searchFood.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText) // Gọi hàm search mới
                return true
            }
        })

        // Listener cho nút "Add Meal"
        binding.btnAddMeal.setOnClickListener { // Đảm bảo nút này có id là btn_add_meal trong layout
            viewModel.saveSelectedMeal(mealType)
        }
    }

    private fun setupRecyclerViews() {
        foodAdapter = FoodAdapter(
            isGridView = isGridLayout,
            onAddFoodClick = { foodItem ->
                viewModel.addFoodToMeal(foodItem)
            },
            onItemClick = { foodItem ->
                val intent = Intent(this, FoodDetailActivity::class.java)
                intent.putExtra("FOOD_ID", foodItem.id)
                startActivity(intent)
            }
        )

        binding.rcvFoodList.adapter = foodAdapter

        chooseFoodAdapter = ChooseFoodAdapter { selectedFoodItem ->
            viewModel.removeFoodFromMeal(selectedFoodItem)
        }
        binding.rvFoodChoose.adapter = chooseFoodAdapter

        binding.rcvFoodList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager
                if (layoutManager != null && dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = when (layoutManager) {
                        is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                        is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                        else -> 0
                    }

                    // Điều kiện load more (ngưỡng 5 item)
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 && totalItemCount > 0) {
                        // Gọi hàm loadNextPage rõ ràng hơn
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        setLayoutManager()
    }

    private fun setupObservers() {
        // Observe danh sách món ăn từ API
        viewModel.allFoods.observe(this) { allFoods ->
            foodAdapter.submitList(allFoods)
        }

        // Observe danh sách món ăn đã chọn
        viewModel.selectedMeals.observe(this) { selectedMeals ->
            chooseFoodAdapter.submitList(selectedMeals.toList()) // toList() để tạo list mới, kích hoạt DiffUtil
            binding.rvFoodChoose.visibility = if (selectedMeals.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError() // Xóa lỗi sau khi hiển thị
            }
        }

        viewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "$mealType saved successfully!", Toast.LENGTH_SHORT).show()
                viewModel.onSaveComplete()
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu?.findItem(R.id.app_bar_view)?.setIcon(
            if (isGridLayout) R.drawable.ic_grid_view_with_background
            else R.drawable.ic_view_headline_with_background
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_view -> {
                isGridLayout = !isGridLayout
                setLayoutManager()
                foodAdapter.setLayoutType(isGridLayout)
                item.setIcon(
                    if (isGridLayout) R.drawable.ic_grid_view_with_background
                    else R.drawable.ic_view_headline_with_background
                )
                getSharedPreferences("food_preferences", Context.MODE_PRIVATE)
                    .edit().putBoolean("isGridLayout", isGridLayout).apply()
                true
            }
            R.id.app_bar_search -> {
                binding.searchFood.visibility = if(binding.searchFood.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setLayoutManager() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_spacing)
        if (binding.rcvFoodList.itemDecorationCount > 0) {
            binding.rcvFoodList.removeItemDecorationAt(0)
        }

        if (isGridLayout) {
            binding.rcvFoodList.layoutManager = GridLayoutManager(this, 2)
            binding.rcvFoodList.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
        } else {
            binding.rcvFoodList.layoutManager = LinearLayoutManager(this)
        }

        binding.rvFoodChoose.layoutManager = LinearLayoutManager(this)
    }
}