package com.kseniabl.tasksapp.dialogs

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

abstract class BaseDialog: DialogFragment() {

    override fun onResume() {
        val wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val window = dialog!!.window
        var width = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = wm.currentWindowMetrics
            val windowInsets: WindowInsets = windowMetrics.windowInsets

            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            val insetsWidth = insets.right + insets.left
            val b = windowMetrics.bounds
            width = b.width() - insetsWidth
        } else {
            val size = Point()
            @Suppress("DEPRECATION")
            val display = window!!.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getSize(size)
            width = size.x
        }

        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        super.onResume()
    }

}