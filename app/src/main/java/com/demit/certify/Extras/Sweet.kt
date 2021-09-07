package com.demit.certify.Extras

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.demit.certify.R
import java.lang.Exception

class Sweet(var context: Context) {
    var alertDialog: SweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
    fun show(message: String?) {
        try {
            if (message != null) {
                alertDialog.titleText = "$message..."
            }
            alertDialog.progressHelper.barColor = context.resources
                .getColor(R.color.dblue)
            alertDialog.show()
        } catch (e: Exception) {
        }
    }

    fun dismiss() {
        alertDialog.dismiss()
    }

    init {
        alertDialog.setCancelable(false)
        alertDialog.titleText = "Loading..."
    }
}