package com.mspark.myimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mspark.myimage.databinding.FragmentMyImageBinding

class MyImageFragment: Fragment() {
    private lateinit var binding: FragmentMyImageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentMyImageBinding.inflate(inflater).apply {
            binding = this
        }.root
}