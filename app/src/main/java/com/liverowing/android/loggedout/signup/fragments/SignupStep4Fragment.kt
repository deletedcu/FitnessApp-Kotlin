package com.liverowing.android.activity.login

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.liverowing.android.BuildConfig
import com.liverowing.android.R
import com.liverowing.android.extensions.getResizedBitmap
import com.liverowing.android.extensions.rotateImageIfRequired
import com.liverowing.android.loggedout.signup.fragments.BaseStepFragment
import com.liverowing.android.loggedout.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_4.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "VARIABLE_WITH_REDUNDANT_INITIALIZER")
class SignupStep4Fragment(override var listener: ResultListener) : BaseStepFragment() {

    private var permissionsToRequest = arrayListOf<String>()
    private var permissionsRejected = arrayListOf<String>()
    private var permissions = arrayListOf<String>()

    private val ALL_PERMISSIONS_RESULT = 107
    private val REQUEST_PHOTO = 1001

    var myBitmap: Bitmap? = null

    var birthday: String = ""
        get() = a_signup_birthday_text.text.toString()

    var gender: String = "male"
        get() {
            if (a_signup_gender_male.isChecked) {
                return "male"
            } else {
                return "female"
            }
        }

    var myCalendar: Calendar = Calendar.getInstance()

    var userName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(year, month, dayOfMonth)
            updateBirthday()
        }

        a_signup_birthday_text.setOnClickListener {
            if (a_signup_birthday_text.error != null) {
                a_signup_birthday_text.error = null
            }
            DatePickerDialog(context, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

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
                    startActivityForResult(getPickImageChooserIntent(), REQUEST_PHOTO)
                }
            } else {
                startActivityForResult(getPickImageChooserIntent(), REQUEST_PHOTO)
            }

        }
    }

    private fun updateBirthday() {
        val pattern = "MM/dd/yyyy"
        var simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
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

    /**
     * Create a chooser intent to select the source to get image from.<br />
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br />
     * All possible sources are added to the intent chooser.
     */
    private fun getPickImageChooserIntent(): Intent {

        // Determine Uri of camera image to save.
        val outputFileUri = getCaptureImageOutputUri()

        var allIntents = arrayListOf<Intent>()
        val packageManager = activity!!.packageManager

        // collect all camera intents
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        var mainIntent = allIntents.get(allIntents.size - 1)
        for (intent in allIntents) {
            if (intent.component.className.equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        val array = arrayOfNulls<Intent>(allIntents.size)
        allIntents.toArray(array)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, array)

        return chooserIntent
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private fun getCaptureImageOutputUri(): Uri? {
        val file = getOutputMediaFile(activity!!.resources.getString(R.string.app_name) + File.separator + "profile")
        if (file != null) {
            val outputUri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".fileprovider", file)
            return outputUri
        } else {
            return null
        }
    }

    private fun getOutputMediaFile(folderName: String): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val mediaFile = File(mediaStorageDir.path + File.separator + "profile_pic.jpg")
        return  mediaFile
    }

    /**
     * Get the URI of the selected image from [.getPickImageChooserIntent].<br></br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
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
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATED_IDENTITY_EQUALS")
                return ContextCompat.checkSelfPermission(activity!!, permission) === PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
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
                    startActivityForResult(getPickImageChooserIntent(), REQUEST_PHOTO)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap: Bitmap? = null
        when (requestCode) {
            REQUEST_PHOTO -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val picUri = getPickImageResultUri(data)
                        if (picUri != null) {
                            try {
                                var tempBitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, picUri)
                                tempBitmap = tempBitmap.rotateImageIfRequired(activity!!, picUri)
                                myBitmap = tempBitmap.getResizedBitmap(500)

                                btn_signup_profile_picture.background = BitmapDrawable(activity!!.resources, myBitmap)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            if (data != null) {
                                bitmap = data.extras.get("data") as? Bitmap
                                if (bitmap != null) {
                                    myBitmap = bitmap
                                    btn_signup_profile_picture.background = BitmapDrawable(activity!!.resources, myBitmap)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
