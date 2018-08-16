package com.liverowing.android.loggedout.signup.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.liverowing.android.R
import com.liverowing.android.util.Constants
import com.liverowing.android.util.DpHandler
import com.liverowing.android.util.Utils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_signup_4.*
import kotlinx.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "VARIABLE_WITH_REDUNDANT_INITIALIZER")
class SignupStep4Fragment : BaseStepFragment() {
    override lateinit var listener: ResultListener

    private var permissionsToRequest = arrayListOf<String>()
    private var permissionsRejected = arrayListOf<String>()
    private var permissions = arrayListOf<String>()

    private val ALL_PERMISSIONS_RESULT = 107

    var bitmapBytes: ByteArray? = null

    var birthday: String = ""
        get() = a_signup_birthday_text.text.toString()

    var gender: String = "Male"
        get() {
            if (a_signup_gender_male.isChecked) {
                return "Male"
            } else {
                return "Female"
            }
        }

    var myCalendar: Calendar = Calendar.getInstance()

    var userName: String = ""

    var defaultYear = 1980
    var defaultMonth = 5
    var defaultDay = 15

    companion object {
        fun newInstance(listener: ResultListener) : SignupStep4Fragment {
            var f = SignupStep4Fragment()
            f.listener = listener;
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(year, month, dayOfMonth)
            updateBirthday()
        }

        a_signup_birthday_text.setOnClickListener {
            if (a_signup_birthday_text.error != null) {
                a_signup_birthday_text.error = null
            }
            if (birthday.isNotEmpty()) {
                val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.US)
                val date = simpleDateFormat.parse(birthday)
                myCalendar.time = date
                defaultYear = myCalendar.get(Calendar.YEAR)
                defaultMonth = myCalendar.get(Calendar.MONTH)
                defaultDay = myCalendar.get(Calendar.DAY_OF_MONTH)
            }
            DatePickerDialog(context, dateSetListener, defaultYear, defaultMonth, defaultDay).show()
        }

        a_signup_birthday_text.hint = "Jun 15, 1980"
        a_signup_username_textview.text = userName

        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        btn_signup_profile_picture.setOnClickListener {
            permissionsToRequest = findUnAskedPermissions(permissions)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size > 0) {
                    val array = arrayOfNulls<String>(permissionsToRequest.size)
                    permissionsToRequest.toArray(array)
                    requestPermissions(array, ALL_PERMISSIONS_RESULT)
                } else {
                    startCropImageActivity()
                }
            } else {
                startCropImageActivity()
            }

        }
    }

    private fun updateBirthday() {
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.US)
        a_signup_birthday_text.setText(simpleDateFormat.format(myCalendar.time))
    }

    override fun checkValidation() {
        if (birthday.isEmpty()) {
            a_signup_birthday_text.requestFocus()
            a_signup_birthday_text.error = "Empty birthday!"
        } else {
            var data = HashMap<String, String>()
            data.put("birthday", birthday)
            data.put("gender", gender)
            listener.onResultListener(true, data)
        }
    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = arrayListOf<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (Utils.canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATED_IDENTITY_EQUALS")
                return ContextCompat.checkSelfPermission(activity!!, permission) === PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity!!)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsRejected.clear()
        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    DialogInterface.OnClickListener { _, _ ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(permissionsRejected.toArray(arrayOfNulls<String>(permissionsRejected.size)), ALL_PERMISSIONS_RESULT)
                                        }
                                    })
                            return
                        }
                    }

                } else {
                    startCropImageActivity()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val uri = result.uri
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
                            if (bitmap != null) {
                                val baos = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                bitmapBytes = baos.toByteArray()
                                btn_signup_profile_picture.background = BitmapDrawable(context!!.resources, CropImage.toOvalBitmap(bitmap!!))
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context!!, e.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                        Toast.makeText(context!!, result.error.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun startCropImageActivity() {
        val defaultResultSize = DpHandler.dpToPx(context!!,500)
        CropImage.activity()
                .setActivityTitle("Crop")
                .setCropMenuCropButtonTitle("Done")
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(50)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(defaultResultSize, defaultResultSize)
                .setMaxCropResultSize(defaultResultSize, defaultResultSize)
                .start(context!!, this)
    }

}
