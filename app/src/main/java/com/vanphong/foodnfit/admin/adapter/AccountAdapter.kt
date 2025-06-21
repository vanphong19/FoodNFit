package com.vanphong.foodnfit.admin.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.model.UserResponse
import com.vanphong.foodnfit.R
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AccountAdapter(private val onItemClick:(String) -> Unit): ListAdapter<UserResponse, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {
    class AccountViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvFullName: TextView = itemView.findViewById(R.id.tvFullName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val ivAvatar: CircleImageView = itemView.findViewById(R.id.ivAvatar)
        val chipStatus: TextView = itemView.findViewById(R.id.chipStatus)
        val tvGender: TextView = itemView.findViewById(R.id.tvGender)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = getItem(position)
        holder.tvFullName.text = account.fullname
        holder.tvEmail.text = account.email
        val gender = if (!account.gender) {
            holder.itemView.context.getString(R.string.male)
        } else {
            holder.itemView.context.getString(R.string.female)
        }
        holder.tvGender.text = gender

        val status = if (!account.blocked){
            holder.itemView.context.getString(R.string.un_blocked)
        }
        else{
            holder.itemView.context.getString(R.string.blocked)
        }
        holder.chipStatus.text = status

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthday = account.birthday
        val age = if (birthday != null) {
            try {
                val birthDate = LocalDate.parse(birthday, formatter)
                val currentDate = LocalDate.now()
                ChronoUnit.YEARS.between(birthDate, currentDate).toInt()
            } catch (e: Exception) {
                // Trường hợp sai format ngày tháng
                -1
            }
        } else {
            -1 // hoặc null nếu bạn muốn dùng nullable
        }
        holder.tvAge.text = age.toString()

        val avatarUrl = account.avatarUrl

        val defaultAvatar = if (account.gender) {
            R.drawable.female
        } else {
            R.drawable.male
        }

        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(avatarUrl)
                .placeholder(defaultAvatar) // ảnh hiển thị trong khi tải
                .error(defaultAvatar)       // ảnh nếu URL lỗi
                .into(holder.ivAvatar)
        } else {
            // nếu avatarUrl null hoặc rỗng
            holder.ivAvatar.setImageResource(defaultAvatar)
        }

        holder.itemView.setOnClickListener {
            onItemClick(account.id)
        }

        val bgColor = if (position % 2 == 0)
            Color.parseColor("#F5F5F5")  // xám nhạt
        else
            Color.WHITE

        holder.itemView.setBackgroundColor(bgColor)
    }
}
class AccountDiffCallback: DiffUtil.ItemCallback<UserResponse>(){
    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }

}