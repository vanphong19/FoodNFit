package com.vanphong.foodnfit.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.DataUtils
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.Model.Ingredient
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.IngredientAdapter
import com.vanphong.foodnfit.adapter.RecipeAdapter
import com.vanphong.foodnfit.databinding.ActivityFoodDetailBinding
import com.vanphong.foodnfit.viewModel.FoodDetailViewModel

class FoodDetailActivity : AppCompatActivity() {
    private val viewModel: FoodDetailViewModel by viewModels()
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var binding: ActivityFoodDetailBinding

    private val foodItem = FoodItem(
        id = 1,
        name = "Gà xào rau",
        calories = 200f,
        carbs = 30f,
        protein = 10f,
        fat = 5f,
        imageUrl = null,
        servingSize = "100g",
        recipe = "Cho thịt bò vào tô, thêm dầu hào, xì dầu, hạt nêm, đường và tỏi băm. Trộn đều và ướp khoảng 15–20 phút cho thấm gia vị.\n" +
                "\n" +
                "Làm nóng chảo với 1 thìa dầu ăn. Cho thịt bò vào xào nhanh tay trên lửa lớn khoảng 1–2 phút cho đến khi thịt vừa chín tới. Khi thịt đã chín, trút ra đĩa riêng để giữ độ mềm.\n" +
                "\n" +
                "Dùng lại chảo, thêm một chút dầu nếu cần. Cho hành tây vào xào khoảng 1 phút đến khi hành mềm nhưng vẫn giữ độ giòn nhẹ. Nêm thêm muối hoặc hạt nêm nếu cần.\n" +
                "\n" +
                "Cho thịt bò đã xào vào chảo hành tây. Đảo đều khoảng 30 giây trên lửa lớn cho hòa quyện hương vị. Rắc thêm tiêu và hành lá cắt khúc rồi tắt bếp.\n" +
                "\n" +
                "Dọn món ra đĩa và ăn kèm cơm trắng. Có thể ăn cùng rau luộc hoặc canh chua để thêm phần tròn vị.",
        foodTypeId = 2,
        isActive = true
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_food_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_detail)
        binding.lifecycleOwner = this
        binding.foodDetailViewModel = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.food_detail)

        ingredientAdapter = IngredientAdapter()
        binding.rcvIngredient.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvIngredient.adapter = ingredientAdapter

        viewModel.ingredients.observe(this){ ingredients ->
            ingredientAdapter.submitList(ingredients)
        }

        val ingredients = listOf(
            Ingredient("Tomato", "100g"),
            Ingredient("Onion", "50g"),
            Ingredient("Lettuce", "30g")
        )
        viewModel.setIngredients(ingredients)

        setPieChart()

        foodItem.recipe?.let {
            viewModel.setRecipeSteps(it)
        }

        val recipeAdapter = RecipeAdapter()
        binding.rcvRecipe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvRecipe.adapter = recipeAdapter

        viewModel.recipeSteps.observe(this, Observer { steps ->
            recipeAdapter.submitList(steps)
        })
    }

    private fun setPieChart(){
        viewModel.setFoodItem(foodItem)
    }
}