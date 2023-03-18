package com.mspark.myimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mspark.myimage.databinding.FragmentSearchBinding
import com.mspark.myimage.viewmodel.MainViewModel
import com.mspark.myimage.viewmodel.ViewModelFactory

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private val viewModel by lazy {
        ViewModelProvider(
            activity ?: requireActivity(),
            ViewModelFactory(context ?: requireContext())
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentSearchBinding.inflate(inflater).apply {
            binding = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.view = viewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}