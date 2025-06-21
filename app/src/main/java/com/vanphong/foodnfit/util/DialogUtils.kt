package com.vanphong.foodnfit.util

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.vanphong.foodnfit.R

object DialogUtils {
    private var loadingDialog: AlertDialog? = null

    fun showLoadingDialog(activity: Activity, message: String) {
        if (loadingDialog?.isShowing == true) return

        val builder = AlertDialog.Builder(activity)
        builder.setView(R.layout.dialog_loading)
        builder.setCancelable(false)

        loadingDialog = builder.create()
        loadingDialog?.show()

        loadingDialog?.findViewById<TextView>(R.id.loading_message)?.text = message
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}
