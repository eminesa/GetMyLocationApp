package com.eminesa.getmylocationapp

import android.app.Application
import android.content.Context
import android.content.Intent
import com.eminesa.getmylocationapp.service.LocationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GetMyLocationApplication : Application() {
}