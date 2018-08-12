package com.liverowing.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.io.ByteArrayOutputStream
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


class Utils {

    companion object {
        fun BitmapToString(bitmap: Bitmap): String? {
            try {
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val b = baos.toByteArray()
                return android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
            } catch (e: NullPointerException) {
                return null
            } catch (e: OutOfMemoryError) {
                return null
            }

        }

        fun StringToBitmap(encodedString: String): Bitmap? {
            try {
                val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: NullPointerException) {
                return null
            } catch (e: OutOfMemoryError) {
                return null
            }
        }

        fun StringToBytes(encodedString: String): ByteArray? {
            try {
                val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                return encodeByte
            } catch (e: NullPointerException) {
                return null
            } catch (e: OutOfMemoryError) {
                return null
            }
        }

        fun feetToInch(feet: String): String {
            val values = feet.split("'".toRegex())
            val feet = values.get(0).toInt()
            val inch = values.get(1).replace("″", "").toInt()
            val result = feet * 12 + inch
            return result.toString()
        }

        fun convertFeetToCM(feet: String): String {
            if (feet.isEmpty()) {
                return ""
            } else {
                val values = feet.split("'".toRegex())
                if (values.size > 1) {
                    val feet = values.get(0).toInt()
                    val inch = values.get(1).replace("″", "").toInt()
                    var result = feet * 12 + inch
                    result = Math.round(result * 2.54).toInt()
                    return result.toString()
                } else {
                    return ""
                }
            }
        }

        fun convertCMToFeet(cm: String): String {
            if (cm.isEmpty()) {
                return ""
            } else {
                val values = Math.round(cm.toInt() / 2.54).toInt()
                val feet = values / 12
                val inch = values % 12
                var result = String.format("%d'%d″", feet, inch)
                return result
            }
        }

        fun convertKgToPound(kg: String): String {
            if (kg.isEmpty()) {
                return ""
            } else {
                val pound = Math.round(kg.toInt() * 2.2).toInt()
                return pound.toString()
            }
        }

        fun convertPoundToKg(pound: String): String {
            if (pound.isEmpty()) {
                return ""
            } else {
                val pound = Math.round(pound.toInt() / 2.2).toInt()
                return pound.toString()
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm!!.hideSoftInputFromWindow(view!!.windowToken, 0)
        }

    }

}