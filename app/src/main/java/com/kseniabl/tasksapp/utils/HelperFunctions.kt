package com.kseniabl.tasksapp.utils

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.kseniabl.tasksapp.R
import java.io.ByteArrayInputStream
import java.util.*
import java.util.regex.Matcher
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

    fun isValidPassword(password: String): Boolean {
        val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun getImageBitmap(content: String): Bitmap {
        val contentBytes = Base64.decode(content, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(contentBytes, 0, contentBytes.size)
        val exif = ExifInterface(ByteArrayInputStream(contentBytes))
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}