package com.vanphong.foodnfit.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.model.FoodItem
import com.vanphong.foodnfit.model.Ingredient
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.IngredientAdapter
import com.vanphong.foodnfit.adapter.RecipeAdapter
import com.vanphong.foodnfit.databinding.ActivityFoodDetailBinding
import com.vanphong.foodnfit.viewModel.FoodDetailViewModel

class FoodDetailActivity : BaseActivity() {
    private val viewModel: FoodDetailViewModel by viewModels()
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var binding: ActivityFoodDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_detail)
        binding.lifecycleOwner = this
        binding.foodDetailViewModel = viewModel // Kết nối ViewModel với layout

        setupToolbar()
        setupRecyclerViews()
        setupObservers()

        // Lấy foodId từ Intent
        val foodId = intent.getIntExtra("FOOD_ID", -1)
        if (foodId != -1) {
            // Gọi ViewModel để tải dữ liệu
            viewModel.loadFoodDetail(foodId)
        } else {
            // Xử lý trường hợp không có ID
            Toast.makeText(this, "Food not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.food_detail)
    }

    private fun setupRecyclerViews() {
        ingredientAdapter = IngredientAdapter()
        binding.rcvIngredient.layoutManager = LinearLayoutManager(this)
        binding.rcvIngredient.adapter = ingredientAdapter

        recipeAdapter = RecipeAdapter()
        binding.rcvRecipe.layoutManager = LinearLayoutManager(this)
        binding.rcvRecipe.adapter = recipeAdapter
    }

    private fun setupObservers() {
        // Lắng nghe dữ liệu món ăn thay đổi
        viewModel.foodItem.observe(this) { food ->
            // Cập nhật các UI không qua DataBinding (nếu cần)
            // Ví dụ: Load ảnh bằng Glide
            Glide.with(this)
                .load(food.imageUrl)
                .placeholder(R.drawable.thit_bo) // ảnh mặc định
                .into(binding.imgFood) // Giả sử ImageView có id là imgFood

            // Cập nhật title, serving size... qua DataBinding đã tự động
        }

        // Lắng nghe danh sách nguyên liệu
        viewModel.ingredients.observe(this) { ingredients ->
            ingredientAdapter.submitList(ingredients)
        }

        // Lắng nghe các bước công thức
        viewModel.recipeSteps.observe(this) { steps ->
            recipeAdapter.submitList(steps)
        }

        // Lắng nghe lỗi
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Bắt sự kiện nút back trên toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}