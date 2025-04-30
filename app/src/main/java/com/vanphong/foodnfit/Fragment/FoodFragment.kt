package com.vanphong.foodnfit.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchView
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.FoodAdapter
import com.vanphong.foodnfit.component.GridSpacingItemDecoration
import com.vanphong.foodnfit.databinding.FragmentFoodBinding
import com.vanphong.foodnfit.viewModel.FoodViewModel

class FoodFragment : Fragment() {
    private var _binding:FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel:FoodViewModel by viewModels()

    private var isGridLayout = false
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_food,container, false)

        binding.lifecycleOwner = this
        binding.foodViewModel = viewModel

        val sharePrefs = requireContext().getSharedPreferences("food_preferences", Context.MODE_PRIVATE)
        isGridLayout = sharePrefs.getBoolean("isGridLayout", false)

        foodAdapter = FoodAdapter(isGridLayout)
        binding.rcvFoodList.adapter = foodAdapter
        setLayoutManager()

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarFood)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.app_bar_view ->{
                        isGridLayout = !isGridLayout
                        setLayoutManager()
                        foodAdapter.setLayoutType(isGridLayout)
                        val newIcon = if (isGridLayout) R.drawable.ic_grid_view_with_background
                                        else R.drawable.ic_view_headline_with_background
                        menuItem.setIcon(newIcon)

                        sharePrefs.edit().putBoolean("isGridLayout", isGridLayout).apply()
                        true
                    }
                    R.id.app_bar_search -> {
                        binding.searchFood.visibility = View.VISIBLE
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.allFoods.observe(viewLifecycleOwner, Observer { allFoods ->
            foodAdapter.submitList(allFoods)
        })

        setData()
        //setupMenu()
        return binding.root
    }

    private fun setLayoutManager() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_spacing)
        binding.rcvFoodList.itemDecorationCount.let {
            if (it > 0) binding.rcvFoodList.removeItemDecorationAt(0)
        }

        if (isGridLayout) {
            binding.rcvFoodList.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rcvFoodList.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
        } else {
            binding.rcvFoodList.layoutManager = LinearLayoutManager(requireContext())
        }
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


//    private fun setupMenu() {
//        val menuHost: MenuHost = requireActivity()
//
//        menuHost.addMenuProvider(object: MenuProvider{
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_toolbar, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

//    fun showSearchView(){
//        binding.svFood.visibility = View.VISIBLE
//        binding.svFood.show()
//        binding.svFood.editText.requestFocus()
//    }
//
//    private fun hideSearchView() {
//        binding.svFood.hide()
//        binding.svFood.visibility = View.GONE
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}