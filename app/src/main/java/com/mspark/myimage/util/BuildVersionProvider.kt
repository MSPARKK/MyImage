package com.mspark.myimage.util

import android.os.Build

interface BuildVersionProvider {
    fun isOreoAndAbove(): Boolean
}

class BuildVersionProviderImpl : BuildVersionProvider {
    override fun isOreoAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}