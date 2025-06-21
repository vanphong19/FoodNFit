package com.vanphong.foodnfit.admin.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.viewModel.FoodDetailViewModel

import com.vanphong.foodnfit.databinding.ActivityFoodDetailAdminBinding
import com.vanphong.foodnfit.util.DialogUtils

class FoodDetailAdminActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFoodDetailAdminBinding
    private var foodId: Int? = null
    private val viewModel: FoodDetailViewModel by viewModels()
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.detailViewModel = viewModel
        foodId = intent.getIntExtra("food_id", 0)
        foodId?.let { viewModel.getById(it) }
        setUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setUI(){
        viewModel.foodName.observe(this){name ->
            binding.tvFoodName.text = name
        }
        viewModel.imageUrl.observe(this){imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.ivFoodImage)
        }
        viewModel.calories.observe(this){calories ->
            binding.tvCalories.text = calories + " " + getString(R.string.kcal)
        }
        viewModel.carbs.observe(this){carbs->
            binding.tvCarbs.text = carbs + " " + getString(R.string.g)
        }
        viewModel.fat.observe(this){fat ->
            binding.tvFat.text = fat + " " + getString(R.string.g)
        }
        viewModel.protein.observe(this){protein ->
            binding.tvProtein.text = protein + " " + getString(R.string.g)
        }
        viewModel.servingSize.observe(this){serving ->
            binding.tvServingSize.text = serving
        }
        viewModel.ingredients.observe(this) { ingredient ->
            binding.tvIngredients.text = ingredient
        }
        viewModel.recipe.observe(this){recipe ->
            binding.tvRecipe.text = recipe
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }
    }
}