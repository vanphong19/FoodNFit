package com.vanphong.foodnfit.fragment.Setup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.NumberPickerAdapter
import com.vanphong.foodnfit.databinding.FragmentTallBinding

class TallFragment : Fragment() {
    private var _binding: FragmentTallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTallBinding.inflate(inflater)
        val recyclerView = binding.rvPicker
        val numbers = (100..220).toList()
        val initialPos = numbers.indexOf(150)
        val adapter = NumberPickerAdapter(numbers, initialPos)

        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // Biến lưu vị trí đã toast lần gần nhất
        var lastCenterPos = -1

        recyclerView.post {
            val itemWidth = recyclerView.context.resources.displayMetrics.density * 80 // 80dp từ XML
            val rvWidth = recyclerView.width
            val offset = (rvWidth / 2) - (itemWidth / 2)
            layoutManager.scrollToPositionWithOffset(initialPos, offset.toInt())

            // Lấy snapView và centerPos ngay sau khi canh giữa
            recyclerView.post {
                val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                snapView?.let {
                    val centerPos = recyclerView.getChildAdapterPosition(it)
                    if (centerPos != RecyclerView.NO_POSITION) {
                        adapter.updateCenterPosition(centerPos)
                        if (centerPos != lastCenterPos) {
                            val value = numbers[centerPos]
                            Toast.makeText(requireContext(), "Bạn chọn: $value", Toast.LENGTH_SHORT).show()
                            lastCenterPos = centerPos
                        }
                    }
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                    snapView?.let {
                        val centerPos = recyclerView.getChildAdapterPosition(it)
                        if (centerPos != RecyclerView.NO_POSITION) {
                            adapter.updateCenterPosition(centerPos)
                            if (centerPos != lastCenterPos) {
                                val value = numbers[centerPos]
                                Toast.makeText(requireContext(), "Bạn chọn: $value", Toast.LENGTH_SHORT).show()
                                lastCenterPos = centerPos
                            }
                        }
                    }
                }
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
