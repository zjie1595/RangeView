package com.zj.rangeview

import android.app.Application
import com.drake.brv.utils.BRV

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BRV.modelId = BR.m
    }
}