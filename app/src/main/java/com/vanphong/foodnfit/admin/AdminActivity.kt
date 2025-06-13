package com.vanphong.foodnfit.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.vanphong.foodnfit.BaseActivity

import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.SignInActivity

import com.vanphong.foodnfit.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = binding.toolbar
        drawerLayout = binding.main
        setSupportActionBar(toolbar)

        // ✅ Khởi tạo NavController từ NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // ✅ Gắn NavigationView với NavController
        NavigationUI.setupWithNavController(binding.navView, navController)

        // ✅ Bắt riêng sự kiện cho nút Logout
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    // Xóa dữ liệu đăng nhập
                    val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    // Hiển thị toast
                    Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()

                    // Đóng Drawer
                    drawerLayout.closeDrawer(GravityCompat.START)

                    // Chuyển về SignInActivity (hoặc LauncherActivity tuỳ app)
                    val intent = Intent(this, SignInActivity::class.java)
                    // Clear backstack để không thể back lại màn admin
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    true
                }
                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
        }

        // ✅ Đồng bộ nút Hamburger với Drawer
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
