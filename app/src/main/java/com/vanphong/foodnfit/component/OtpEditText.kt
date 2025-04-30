package com.vanphong.foodnfit.component

import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import java.security.AccessControlContext

class OtpEditText(context: Context, attrs: AttributeSet):
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    private var onPasteListener: ((String) -> Unit)? = null

    fun setOnPasteListener(listener: (String) -> Unit) {
        onPasteListener = listener
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val pastedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            onPasteListener?.invoke(pastedText)
        }
        return super.onTextContextMenuItem(id)
    }
}