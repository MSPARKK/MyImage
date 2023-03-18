package com.mspark.myimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mspark.myimage.databinding.FragmentMyImageBinding
import com.mspark.myimage.viewmodel.MainViewModel
import com.mspark.myimage.viewmodel.ViewModelFactory

class MyImageFragment: Fragment() {
    private lateinit var binding: FragmentMyImageBinding

    private val viewModel by lazy {
        ViewModelProvider(
            activity ?: requireActivity(),
            ViewModelFactory(context ?: requireContext())
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentMyImageBinding.inflate(inflater).apply {
            binding = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.view = viewModel
        }.root

    companion object {
        @JvmStatic
        fun newInstance() = MyImageFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}