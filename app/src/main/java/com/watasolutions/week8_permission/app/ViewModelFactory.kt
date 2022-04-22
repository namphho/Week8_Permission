package com.watasolutions.week8_permission.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watasolutions.week8_permission.app.MyApp
import com.watasolutions.week8_permission.screens.home.HomeViewModel

class ViewModelFactory(val app: MyApp) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app.locationClient) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}