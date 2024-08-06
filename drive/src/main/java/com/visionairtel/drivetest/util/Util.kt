package com.visionairtel.drivetest.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.visionairtel.drivetest.R

object Util {
    fun showLog(tag: String, msg: Any?) {
        Log.d(tag, msg.toString())
    }

    enum class NetworkTypeEnum(val color: Color, val title: String) {
        LTE(color = Color.Red, title = "4G"),
        WCDMA(color = Color(0xFF673AB7), title = "3G"),
        GSM(color = Color.Blue, title = "2G"),
        NS(color = Color.Yellow, title = "NS"),
    }

    fun Context.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Context.showLongToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun Context.showAlert(
        title: String,
        msg: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String = "NO",
        onPressedPositiveButton: ((dialog: DialogInterface) -> Unit)? = null,
        onPressedNegativeButton: ((dialog: DialogInterface) -> Unit)? = null,
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                if (onPressedPositiveButton != null) onPressedPositiveButton(dialog)
                else dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                if (onPressedNegativeButton != null) onPressedNegativeButton(dialog)
                else dialog.dismiss()
            }
            .create().apply {
                setOnShowListener {
                    window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
                }
                show()
            }
    }

}