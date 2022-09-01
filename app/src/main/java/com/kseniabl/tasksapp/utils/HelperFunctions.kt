package com.kseniabl.tasksapp.utils

import android.app.Activity
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import com.kseniabl.tasksapp.R
import java.util.*
import java.util.regex.Pattern

object HelperFunctions {

    fun setTitle(label: CharSequence?, arguments: Bundle?): String {
        if (label == null) return ""
        val title = StringBuffer()
        val fillInPattern = Pattern.compile("\\{(.+?)\\}")
        val matcher = fillInPattern.matcher(label)
        while (matcher.find()) {
            val argName = matcher.group(1)
            if (arguments != null && arguments.containsKey(argName)) {
                matcher.appendReplacement(title, "")
                title.append(arguments[argName].toString())
            } else {
                throw IllegalArgumentException(
                    "Could not find $argName in $arguments to fill label $label"
                )
            }
        }
        matcher.appendTail(title)
        return title.toString()
    }

    fun getTextGradient(activity: Activity): Shader {
        return LinearGradient(
            0F, 0F, 0f, 70F,
            intArrayOf(activity.getColor(R.color.purple), activity.getColor(R.color.blue)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    fun generateRandomKey(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
}