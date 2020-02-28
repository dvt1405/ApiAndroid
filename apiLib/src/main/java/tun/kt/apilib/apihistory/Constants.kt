package tun.kt.apilib.apihistory

import android.view.View
import android.view.Window
import android.view.WindowManager
import java.util.*

object Constants {
    var isPhone: Boolean? = false
    fun parseTime(date: Date, isAmPmEnd: Boolean): String {
        val dateTime = Calendar.getInstance()
        dateTime.time = date
        var hours = dateTime.get(Calendar.HOUR_OF_DAY)
        val minutes = dateTime.get(Calendar.MINUTE)
        var min = minutes.toString()

        if (minutes == 0) min = "00"
        else if (minutes < 10) min = "0${minutes}"
        if (hours >= 12) {
            if (hours > 12) hours = hours - 12
            return if (isAmPmEnd) "${hours}:${min}pm" else "${hours+12}:${min}"
        } else {
            if (hours == 0) hours = 12
            return if (isAmPmEnd) "${hours}:${min}am" else "${hours}:${min}"
        }
    }
    fun setWindowDialogHideNavigationBar(window: Window?) {
        window?.let {
            it.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }

    fun clearFlagWindowDialogNotFocusable(window: Window?) =
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}