package com.watasolutions.week8_permission.services.photo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.watasolutions.week8_permission.services.PhotoLibraryCallback

interface FilePermissionListener{
    fun onFilePermissionListener(isGranted: Boolean)
}

class FilePermissionsUtils {
    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private var filePermissionListener: FilePermissionListener? = null


    private var requestMultiplePermissionsContract: ActivityResultContracts.RequestMultiplePermissions? = null
    private var multiplePermissionActivityResultLauncher: ActivityResultLauncher<Array<String>>? =
        null

    fun registerActivityResultForPermission(fragment: Fragment) {
        requestMultiplePermissionsContract = ActivityResultContracts.RequestMultiplePermissions()
        multiplePermissionActivityResultLauncher =
            fragment.registerForActivityResult(requestMultiplePermissionsContract!!) { isGranted ->
                if (isGranted.containsValue(false)) {
                    Log.d(
                        "PERMISSIONS",
                        "At least one of the permissions was not granted, launching again..."
                    )
                    filePermissionListener?.onFilePermissionListener(false)
                } else {
                    filePermissionListener?.onFilePermissionListener(true)
                }
            }
    }

    fun askPermissions(context: Context, listener: FilePermissionListener) {
        this.filePermissionListener = listener
        if (!hasPermissions(context, PERMISSIONS)) {
            Log.d(
                "PERMISSIONS",
                "Launching multiple contract permission launcher for ALL required permissions"
            )
            multiplePermissionActivityResultLauncher!!.launch(PERMISSIONS)
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted")
            filePermissionListener?.onFilePermissionListener(true)
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("PERMISSIONS", "Permission is not granted: $permission")
                    return false
                }
                Log.d("PERMISSIONS", "Permission already granted: $permission")
            }
            return true
        }
        return false
    }

    fun recycle() {
        filePermissionListener = null
    }
}