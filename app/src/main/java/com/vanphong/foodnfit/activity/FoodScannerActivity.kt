package com.vanphong.foodnfit.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vanphong.foodnfit.model.FoodInfo
import com.vanphong.foodnfit.databinding.ActivityFoodScannerBinding
import com.vanphong.foodnfit.databinding.BottomSheetFoodInfoBinding
import kotlinx.coroutines.launch

import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.vanphong.foodnfit.R

class FoodScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodScannerBinding
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = false
    private var lastScanTime = 0L

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val SCAN_INTERVAL = 2000L // 2 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Manual capture button
        binding.btnCapture.setOnClickListener {
            capturePhoto()
        }

        // Toggle flash
        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        // Toggle scanner mode
        binding.btnToggleScanner.setOnClickListener {
            toggleScannerMode()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FoodAnalyzer { bitmap ->
                        runOnUiThread {
                            processFoodImage(bitmap)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

                // Enable torch control
                binding.btnFlash.isEnabled = camera.cameraInfo.hasFlashUnit()

            } catch (exc: Exception) {
                Log.e("FoodScanner", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        // Show capture animation
        binding.captureOverlay.alpha = 1f
        binding.captureOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        // Simulate food detection and show result
        lifecycleScope.launch {
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.tvScanningStatus.text = "Đang phân tích món ăn..."

            // Simulate API call delay
            kotlinx.coroutines.delay(1500)

            binding.progressBar.visibility = android.view.View.GONE

            // Mock food data
            val foodInfo = FoodInfo(
                name = "Phở Bò",
                calories = 450,
                protein = 25.5f,
                carbs = 58.2f,
                fat = 12.8f,
                fiber = 3.2f,
                confidence = 0.92f,
                description = "Món phở bò truyền thống Việt Nam với nước dùng đậm đà, bánh phở mềm và thịt bò tươi ngon."
            )

            showFoodInfoDialog(foodInfo)
        }
    }

    private fun processFoodImage(bitmap: Bitmap) {
        if (!isScanning) return

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScanTime < SCAN_INTERVAL) return

        lastScanTime = currentTime

        // Show scanning animation
        binding.scanningFrame.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.pulse_animation)
        )

        binding.tvScanningStatus.text = "Đang quét thức ăn..."

        // Simulate food recognition
        lifecycleScope.launch {
            kotlinx.coroutines.delay(1000)

            // Mock detection result
            if (Math.random() > 0.7) { // 30% chance to detect food
                val foodInfo = generateMockFoodInfo()
                showFoodInfoDialog(foodInfo)
            } else {
                binding.tvScanningStatus.text = "Hướng camera vào món ăn để quét"
            }
        }
    }

    private fun generateMockFoodInfo(): FoodInfo {
        val foods = listOf(
            FoodInfo("Cơm Tấm", 520, 18.2f, 75.5f, 15.3f, 2.1f, 0.88f, "Cơm tấm sườn nướng với đầy đủ rau sống và nước mắm chua ngọt."),
            FoodInfo("Bánh Mì", 380, 12.8f, 45.2f, 16.7f, 3.5f, 0.91f, "Bánh mì Việt Nam với pate, chả lụa và rau thơm tươi ngon."),
            FoodInfo("Bún Bò Huế", 420, 22.1f, 48.9f, 14.2f, 4.1f, 0.85f, "Bún bò Huế cay nồng với nước dùng đậm đà đặc trưng miền Trung."),
            FoodInfo("Gỏi Cuốn", 180, 8.5f, 22.3f, 6.8f, 2.9f, 0.79f, "Gỏi cuốn tôm thịt với bánh tráng tươi và rau thơm đa dạng.")
        )
        return foods.random()
    }

    private fun showFoodInfoDialog(foodInfo: FoodInfo) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val dialogBinding = DataBindingUtil.inflate<BottomSheetFoodInfoBinding>(
            layoutInflater, R.layout.bottom_sheet_food_info, null, false
        )

        dialogBinding.foodInfo = foodInfo
        dialogBinding.executePendingBindings()

        // Setup click listeners
        dialogBinding.btnAddToMeal.setOnClickListener {
            // Add to meal logic
            Toast.makeText(this, "Đã thêm ${foodInfo.name} vào bữa ăn", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
            binding.tvScanningStatus.text = "Nhấn nút chụp để phân tích món ăn"
        }

        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    private fun toggleFlash() {
        // Flash toggle logic would go here
        binding.btnFlash.isSelected = !binding.btnFlash.isSelected
    }

    private fun toggleScannerMode() {
        isScanning = !isScanning
        binding.btnToggleScanner.isSelected = isScanning

        if (isScanning) {
            binding.tvScanningStatus.text = "Chế độ quét tự động đang bật"
            binding.scanningFrame.visibility = android.view.View.VISIBLE
        } else {
            binding.tvScanningStatus.text = "Nhấn nút chụp để phân tích món ăn"
            binding.scanningFrame.visibility = android.view.View.GONE
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Cần cấp quyền camera để sử dụng tính năng này.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

// Food Analyzer Class
private class FoodAnalyzer(private val onImageAnalyzed: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxyToBitmap(imageProxy)
        bitmap?.let { onImageAnalyzed(it) }
        imageProxy.close()
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}

