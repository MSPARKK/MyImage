package com.mspark.myimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mspark.myimage.databinding.FragmentSearchBinding

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentSearchBinding.inflate(inflater).apply {
            binding = this
        }.root
}