package com.liverowing.android.extensions

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.shouldShowPermissionRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)

fun Fragment.requestPermission(permission: String, requestId: Int) =
        ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestId)

fun Fragment.batchRequestPermissions(permissions: Array<String>, requestId: Int) =
        ActivityCompat.requestPermissions(activity!!, permissions, requestId)