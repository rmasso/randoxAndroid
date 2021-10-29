package com.demit.certifly.Extras

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.view.ViewTreeObserver.OnWindowAttachListener
import com.demit.certifly.Extras.DetachableClickListener

//this is a Dialog onClick listener
//this is used to prevent memory leaks
class DetachableClickListener private constructor(delegate: DialogInterface.OnClickListener) :
    DialogInterface.OnClickListener {
    private var delegateOrNull: DialogInterface.OnClickListener?
    override fun onClick(dialog: DialogInterface, which: Int) {
        if (delegateOrNull != null) {
            delegateOrNull!!.onClick(dialog, which)
        }
    }

    fun clearOnDetach(dialog: Dialog) {
        dialog.window
            ?.decorView
            ?.viewTreeObserver
            ?.addOnWindowAttachListener(object : OnWindowAttachListener {
                override fun onWindowAttached() {}
                override fun onWindowDetached() {
                    delegateOrNull = null
                }
            })
    }

    companion object {
        fun wrap(delegate: DialogInterface.OnClickListener): DetachableClickListener {
            return DetachableClickListener(delegate)
        }
    }

    init {
        delegateOrNull = delegate
    }
}