package com.watasolutions.week8_permission.screens.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.watasolutions.week8_permission.R
import com.watasolutions.week8_permission.app.MyApp
import com.watasolutions.week8_permission.app.ViewModelFactory
import com.watasolutions.week8_permission.databinding.FragmentHomeBinding
import com.watasolutions.week8_permission.services.PhotoLibraryCallback
import com.watasolutions.week8_permission.services.PhotoLibraryManager
import com.watasolutions.week8_permission.services.location.LocationPermissionUtils
import com.watasolutions.week8_permission.services.photo.FilePermissionListener
import com.watasolutions.week8_permission.services.photo.FilePermissionsUtils
import java.io.File


class HomeFragment : Fragment(), LocationPermissionUtils.LocationComponentListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var vm: HomeViewModel
    var locationPermissionUtils: LocationPermissionUtils = LocationPermissionUtils()
    private var filePermissionUtils: FilePermissionsUtils = FilePermissionsUtils()
    private val photoLibraryManager: PhotoLibraryManager = PhotoLibraryManager()

    init {
        locationPermissionUtils.registerActivityResultForPermission(this)
        filePermissionUtils.registerActivityResultForPermission(this)
        photoLibraryManager.registerActivityResult(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this, ViewModelFactory(activity!!.application as MyApp)).get(
            HomeViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLocationEvent()
        binding.requestLocation.setOnClickListener {
            locationPermissionUtils.requestLocationPermission(this)
        }
        binding.btnOpenPhotoLibrary.setOnClickListener {
            filePermissionUtils.requestPermissions(context!!, filePermissionListener)
        }
    }

    private val filePermissionListener = object : FilePermissionListener {
        override fun onFilePermissionListener(isGranted: Boolean) {
            if (isGranted) {
                photoLibraryManager.openPhotoLibrary(object : PhotoLibraryCallback {
                    override fun onChoosePhoto(photoFile: File) {
                        Log.e("TAG", photoFile.path)
                    }
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        locationPermissionUtils.setListener(this)
    }

    override fun onStop() {
        super.onStop()
        locationPermissionUtils.setListener(null)
    }

    override fun onLocationPermission(isGranted: Boolean) {
        if (isGranted) {
            if (locationPermissionUtils.isLocationEnabled(context!!)) {
                vm.getLocation()
            } else {
                showRequestLocationServiceDialog();
            }
        } else {
            // show dialog to ask user open setting app to enable location permission
            showRequestLocationPermissionDialog();
        }
    }

    private fun registerLocationEvent() {
        vm.locationEvent.observe(viewLifecycleOwner) {
            binding.tvMyLocation.text = "${it.longitude} -- ${it.latitude}"
        }
    }

    private fun showRequestLocationPermissionDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Xác nhận quyền định vị")
        builder.setMessage("Để lấy được thông tin vị trí. Bạn cần xác nhận quyền định vị trong phần Cài Đặt")

        builder.setPositiveButton(R.string.setting) { dialog, _ ->
            dialog.cancel();
            context?.let {
                locationPermissionUtils.openAppSetting(it)
            }
        }

        builder.setNegativeButton(R.string.skip) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun showRequestLocationServiceDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Location Service")
        builder.setMessage("Để sử dụng tính năng. Vui lòng bật Location")

        builder.setPositiveButton(R.string.setting) { dialog, _ ->
            dialog.cancel();
            context?.let {
                locationPermissionUtils.openLocationInAppSetting(it)
            }
        }

        builder.setNegativeButton(R.string.skip) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}