package com.vanphong.foodnfit.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.vanphong.foodnfit.R

@BindingAdapter("android:text")
fun setText(view: TextInputEditText, value: String?) {
    if (view.text.toString() != value) {
        view.setText(value)
    }
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(view: TextInputEditText): String {
    return view.text.toString()
}

@BindingAdapter("android:textAttrChanged")
fun setTextWatcher(view: TextInputEditText, listener: InverseBindingListener?) {
    if (listener == null) {
        view.addTextChangedListener(null)
    } else {
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener.onChange()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

@BindingAdapter("afterTextChanged")
fun afterTextChanged(editText: TextInputEditText, listener: (CharSequence?) -> Unit) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
@BindingAdapter("app:visibleIfNotNull")
fun setVisibleIfNotNull(view: View, value: Any?) {
    view.visibility = if (value != null) View.VISIBLE else View.GONE
}

@BindingAdapter("imageUrlGlide")
fun loadImageWithGlide(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(view)
    } else {
        view.setImageResource(R.drawable.ic_placeholder)
    }
}
@BindingAdapter("videoId")
fun bindVideoId(view: YouTubePlayerView, videoId: String?) {
    if (videoId.isNullOrEmpty()) return

    // Dùng getYouTubePlayerWhenReady để gọi khi player sẵn sàng
    view.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
        override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
            youTubePlayer.cueVideo(videoId, 0f)
        }
    })
}


