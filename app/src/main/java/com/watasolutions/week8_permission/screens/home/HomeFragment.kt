package com.watasolutions.week8_permission.screens.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.watasolutions.week8_permission.app.MyApp
import com.watasolutions.week8_permission.app.ViewModelFactory
import com.watasolutions.week8_permission.databinding.FragmentHomeBinding
import com.watasolutions.week8_permission.services.location.LocationPermissionUtils
import com.watasolutions.week8_permission.services.PhotoLibraryCallback
import com.watasolutions.week8_permission.services.PhotoLibraryManager
import com.watasolutions.week8_permission.services.photo.FilePermissionListener
import com.watasolutions.week8_permission.services.photo.FilePermissionsUtils
import java.io.File

class HomeFragment : Fragment(), LocationPermissionUtils.LocationComponentListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var vm: HomeViewModel
    var locationPermissionUtils: LocationPermissionUtils = LocationPermissionUtils()
    private var filePermissionUtils : FilePermissionsUtils = FilePermissionsUtils()
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
            filePermissionUtils.askPermissions(context!!, filePermissionListener)
        }
    }

    private val filePermissionListener = object : FilePermissionListener{
        override fun onFilePermissionListener(isGranted: Boolean) {
            if (isGranted) {
                photoLibraryManager.openPhotoLibrary(object : PhotoLibraryCallback{
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

    override fun onLocationFeatureEnable(isEnabled: Boolean) {
        if (isEnabled) {
            vm.getLocation()
        } else {
            Toast.makeText(context, "Permission is denied", Toast.LENGTH_SHORT).show()
        }

    }

    private fun registerLocationEvent() {
        vm.locationEvent.observe(this) {
            Log.e("TAG", "${it.longitude} -- ${it.latitude}")
        }
    }
}