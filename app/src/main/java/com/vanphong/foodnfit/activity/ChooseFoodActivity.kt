package com.vanphong.foodnfit.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.ChooseFoodAdapter
import com.vanphong.foodnfit.adapter.FoodAdapter
import com.vanphong.foodnfit.component.GridSpacingItemDecoration
import com.vanphong.foodnfit.databinding.ActivityChooseFoodBinding
import com.vanphong.foodnfit.viewModel.FoodViewModel

class ChooseFoodActivity : BaseActivity() {
    private var _binding: ActivityChooseFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by viewModels()
    private var isGridLayout = false
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var chooseFoodAdapter: ChooseFoodAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChooseFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.foodViewModel = viewModel
        val sharePrefs = getSharedPreferences("food_preferences", Context.MODE_PRIVATE)
        isGridLayout = sharePrefs.getBoolean("isGridLayout", false)

        foodAdapter = FoodAdapter(isGridLayout){foodItem ->
            Log.d("click","1")
            viewModel.addFoodToMeal(foodItem)
        }

        binding.rcvFoodList.adapter = foodAdapter

        chooseFoodAdapter = ChooseFoodAdapter{selectedFoodItem ->  
            viewModel.removeFoodFromMeal(selectedFoodItem.foodItem)
        }
        binding.rvFoodChoose.adapter = chooseFoodAdapter
        setLayoutManager()

        setSupportActionBar(binding.toolbarFood)
        viewModel.allFoods.observe(this) { allFoods ->
            foodAdapter.submitList(allFoods)
        }

        viewModel.selectedMeals.observe(this) { selectedMeals ->
            chooseFoodAdapter.submitList(selectedMeals)
        }

        setData()
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
        binding.rcvFoodList.itemDecorationCount.let {
            if (it > 0) binding.rcvFoodList.removeItemDecorationAt(0)
        }

        if (isGridLayout) {
            binding.rcvFoodList.layoutManager = GridLayoutManager(this, 2)
            binding.rcvFoodList.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
        } else {
            binding.rcvFoodList.layoutManager = LinearLayoutManager(this)
        }

        binding.rvFoodChoose.layoutManager = LinearLayoutManager(this)
    }


    private fun setData() {
        val foodList = listOf(
            FoodItem(
                id = 1,
                name = "Gà xào rau",
                calories = 200f,
                carbs = 30f,
                protein = 10f,
                fat = 5f,
                imageUrl = null,
                servingSize = "100g",
                recipe = "Xào gà với rau củ theo khẩu vị Việt Nam. Thêm dầu hào, nước tương và gia vị cơ bản. Xào chín và thưởng thức với cơm.",
                foodTypeId = 2,
                isActive = true
            ),
            FoodItem(
                id = 2,
                name = "Cơm chiên trứng",
                calories = 350f,
                carbs = 45f,
                protein = 8f,
                fat = 12f,
                imageUrl = null,
                servingSize = "150g",
                recipe = "Cơm nguội xào với trứng, hành lá, nước mắm và tiêu. Có thể thêm xúc xích hoặc lạp xưởng cho đậm đà.",
                foodTypeId = 1,
                isActive = true
            ),
            FoodItem(
                id = 3,
                name = "Salad cá ngừ",
                calories = 180f,
                carbs = 10f,
                protein = 15f,
                fat = 8f,
                imageUrl = null,
                servingSize = "120g",
                recipe = "Cá ngừ trộn cùng xà lách, cà chua, dưa leo và sốt mè rang hoặc dầu oliu. Món ăn nhẹ nhàng và tốt cho sức khỏe.",
                foodTypeId = 3,
                isActive = true
            ),
            FoodItem(
                id = 4,
                name = "Mì xào bò",
                calories = 400f,
                carbs = 50f,
                protein = 20f,
                fat = 15f,
                imageUrl = null,
                servingSize = "180g",
                recipe = "Thịt bò xào với mì và rau cải, nêm nếm với xì dầu, tỏi và tiêu. Món ăn đậm đà, dễ làm.",
                foodTypeId = 2,
                isActive = true
            )
        )

        viewModel.setAllFoods(foodList)
    }
}