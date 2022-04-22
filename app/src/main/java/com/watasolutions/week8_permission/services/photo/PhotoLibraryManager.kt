package com.watasolutions.week8_permission.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.watasolutions.week8_permission.services.photo.FileUtils
import java.io.File
import java.io.IOException


interface PhotoLibraryCallback {
    fun onChoosePhoto(photoFile: File)
}

class PhotoLibraryManager {
    private lateinit var requestLauncher: ActivityResultLauncher<Intent>
    private var photoLibraryCallback: PhotoLibraryCallback? = null

    fun registerActivityResult(fragment: Fragment) {
        requestLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                handleActivityResult(fragment.requireContext(), it)
            }
    }


    fun openPhotoLibrary(photoLibraryCallback: PhotoLibraryCallback) {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        this.photoLibraryCallback = photoLibraryCallback

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        requestLauncher.launch(intent)
    }

    fun recycle() {
        photoLibraryCallback = null
    }

    private fun handleActivityResult(context: Context, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also {
                val path = FileUtils.getUriRealPath(context, it)
                photoLibraryCallback?.onChoosePhoto(File(path))
            }
        }
    }
}